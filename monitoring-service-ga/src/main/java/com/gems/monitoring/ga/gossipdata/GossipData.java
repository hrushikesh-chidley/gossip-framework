package com.gems.monitoring.ga.gossipdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.gems.monitoring.domain.Instance;
import com.gems.monitoring.domain.InstanceId;

public class GossipData {

	private final Map<InstanceId, Integer> gossipMap = new ConcurrentHashMap<>(53);
	private final Map<InstanceId, Boolean> suspectMap = new ConcurrentHashMap<>(53);
	private final Map<Boolean, List<InstanceId>> liveMap = new ConcurrentHashMap<>(2);
	private final Map<InstanceId, Map<InstanceId, Boolean>> suspectMatrix = new ConcurrentHashMap<>(53);
	private final Map<InstanceId, Instance> instances = new ConcurrentHashMap<>(53);
	
	private final Set<InstanceId> consensusExcluded = ConcurrentHashMap.newKeySet(53);

	private final InstanceId selfInstanceId;
	private final int tCleanupCount;
	private final int tPartitionCount;
	

	private final Random randomNumGenerator = new Random();

	private static GossipData gossipData;

	private GossipData(final InstanceId selfInstanceId, final int tCleanupCount, final int tPartitionCount) {
		super();
		this.selfInstanceId = selfInstanceId;
		this.tCleanupCount = tCleanupCount;
		this.tPartitionCount = tPartitionCount;

		gossipMap.put(selfInstanceId, 0);
		suspectMap.put(selfInstanceId, false);
		suspectMatrix.put(selfInstanceId, suspectMap);
		final List<InstanceId> instances = new ArrayList<>(53);
		instances.add(selfInstanceId);
		liveMap.put(true, instances);
	}

	public static final void initialize(final InstanceId selfInstanceId, final int tCleanupCount, final int tPartitionCount) {
		if (gossipData == null) {
			gossipData = new GossipData(selfInstanceId, tCleanupCount, tPartitionCount);
		}
	}

	public final void updateForScheduledGossipTrigger() {
		gossipMap.keySet().parallelStream().filter(instanceId -> !instanceId.equals(selfInstanceId))
				.forEach(instanceId -> {
					Integer waitCount = gossipMap.get(instanceId);
					gossipMap.put(instanceId, ++waitCount);
					if (waitCount >= tCleanupCount) {
						suspectMap.replace(instanceId, true);
						suspectMatrix.replace(selfInstanceId, suspectMap);
						buildConsensus(instanceId);
					}
				});
	}

	private void buildConsensus( final InstanceId suspectedInstanceId ) {
		final List<InstanceId> liveInstances = liveMap.get(true);
		final long globalSuspectCount = suspectMatrix.keySet().parallelStream()
			.filter(instanceId -> liveInstances.contains(instanceId))
			.filter(instanceId -> !consensusExcluded.contains(instanceId))
			.filter(instanceId -> suspectMatrix.get(instanceId).get(suspectedInstanceId))
			.count();
		
		final int consensusJuryCount = liveInstances.size() - consensusExcluded.size();
		if(globalSuspectCount == consensusJuryCount) {
			liveMap.get(true).remove(suspectedInstanceId);
			liveMap.get(false).add(suspectedInstanceId);
			consensusExcluded.remove(suspectedInstanceId);
		}
		if(globalSuspectCount >= consensusJuryCount/2) {
			consensusExcluded.add(suspectedInstanceId);
		}
	}

	public final boolean compareGossipMaps(final Map<InstanceId, Integer> receivedGossipMap,
			final Map<InstanceId, Map<InstanceId, Boolean>> receivedSuspectMatrix) {
		boolean doEnquireInstanceList = false;
		
		checkForNetworkPartitioning(receivedSuspectMatrix);

		for (Entry<InstanceId, Integer> receivedEntries : receivedGossipMap.entrySet()) {
			InstanceId instanceId = receivedEntries.getKey();
			Integer waitCount = receivedEntries.getValue();

			if (gossipMap.containsKey(instanceId)) {
				if (waitCount < gossipMap.get(instanceId)) {
					gossipMap.put(instanceId, waitCount);
					if (waitCount < tCleanupCount) {
						suspectMap.put(instanceId, false);
					}
					suspectMatrix.put(instanceId, receivedSuspectMatrix.get(instanceId));
				}
			} else {
				gossipMap.put(instanceId, waitCount);
				suspectMatrix.put(instanceId, receivedSuspectMatrix.get(instanceId));
				doEnquireInstanceList = true;
			}
		}
		return doEnquireInstanceList;
	}

	private void checkForNetworkPartitioning(final Map<InstanceId, Map<InstanceId, Boolean>> receivedSuspectMatrix) {
		suspectMap.keySet().parallelStream()
			.filter(instanceid -> suspectMap.get(instanceid))
			.filter(instanceId -> !isValueChanged(receivedSuspectMatrix, instanceId))
			.filter(instanceId -> gossipMap.get(instanceId) >= tPartitionCount)
			.forEach(instanceId -> {
				liveMap.get(true).remove(instanceId);
				liveMap.get(false).add(instanceId);
				consensusExcluded.remove(instanceId);
			});
	}
	
	private boolean isValueChanged(final Map<InstanceId, Map<InstanceId, Boolean>> receivedSuspectMatrix, final InstanceId instanceId) {
		boolean isValueDifferent = false;
		for(InstanceId id : suspectMatrix.keySet()) {
			final Map<InstanceId, Boolean> localSuspectMap = suspectMatrix.get(id);
			final Map<InstanceId, Boolean> receivedSuspectMap = receivedSuspectMatrix.get(id);
			
			isValueDifferent = !(localSuspectMap.get(instanceId).equals(receivedSuspectMap.get(instanceId)));
			if(isValueDifferent) {
				break;
			}
		}
		return isValueDifferent;
	}

	public static final GossipData getInstance() {
		return gossipData;
	}

	public final Map<InstanceId, Integer> getGossipMapToSend() {
		return Collections.unmodifiableMap(gossipMap);
	}

	public final Map<InstanceId, Map<InstanceId, Boolean>> getSuspectMatrixToSend() {
		return Collections.unmodifiableMap(suspectMatrix);
	}

	public final boolean AreInstancesAvailable() {
		return instances.size() != 0;
	}

	public final Instance getRandomLiveInstance() {
		final List<InstanceId> liveInstances = liveMap.get(true);
		final int liveInstancesCount = liveInstances.size();
		while (true) {
			final int randomIndex = randomNumGenerator.nextInt(liveInstancesCount - 1);
			final InstanceId instanceId = liveInstances.get(randomIndex);
			if (!instanceId.equals(selfInstanceId)) {
				return instances.get(instanceId);
			}
		}
	}

	public final void markInstanceAsLive(final Instance instance) {
		final InstanceId instanceId = instance.getInstanceId();
		if (!liveMap.get(true).contains(instanceId)) {
			liveMap.get(true).add(instanceId);
			addInstanceToInstanceList(instance);
		}
		liveMap.get(false).remove(instanceId);
	}

	public final void mergeInstanceLists(final List<Instance> receivedInstances) {
		receivedInstances.parallelStream().filter(instance -> !instances.containsKey(instance.getInstanceId()))
				.forEach(instance -> {
					liveMap.get(true).add(instance.getInstanceId());
					addInstanceToInstanceList(instance);
				});
	}

	public List<Instance> getAllInstances() {
		return new ArrayList<Instance>(instances.values());
	}

	private void addInstanceToInstanceList(final Instance instance) {
		instances.put(instance.getInstanceId(), instance);
	}
}
