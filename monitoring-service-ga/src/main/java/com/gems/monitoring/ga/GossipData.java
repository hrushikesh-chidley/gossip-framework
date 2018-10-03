package com.gems.monitoring.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gems.monitoring.domain.Instance;
import com.gems.monitoring.domain.InstanceId;

public class GossipData {

	private final Logger logger = LoggerFactory.getLogger(getClass());

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
		liveMap.put(false, new ArrayList<>());
	}

	public static final void initialize(final InstanceId selfInstanceId, final int tCleanupCount,
			final int tPartitionCount) {
		if (gossipData == null) {
			gossipData = new GossipData(selfInstanceId, tCleanupCount, tPartitionCount);
		}
	}

	public final void updateForScheduledGossipTrigger() {
		logGossipData("updateForScheduledGossipTrigger", "after-entry");
		gossipMap.keySet().parallelStream().filter(instanceId -> !instanceId.equals(selfInstanceId))
				.forEach(instanceId -> {
					Integer waitCount = gossipMap.get(instanceId);
					gossipMap.replace(instanceId, ++waitCount);
					if (waitCount >= tCleanupCount) {
						if(!suspectMap.get(instanceId)) {
							suspectMap.replace(instanceId, true);
							suspectMatrix.replace(selfInstanceId, suspectMap);
						}
						buildConsensus(instanceId);
					}
				});
		logGossipData("updateForScheduledGossipTrigger", "before-exit");
	}

	public final boolean compareGossipMaps(final Map<InstanceId, Integer> receivedGossipMap,
			final Map<InstanceId, Map<InstanceId, Boolean>> receivedSuspectMatrix) {
		logGossipData("compareGossipMaps", "after-entry");

		boolean doEnquireInstanceList = false;

		logger.debug("Received Gossip Map : " + receivedGossipMap);
		logger.debug("Received Suspect Matrix : " + receivedSuspectMatrix);

		//checkForNetworkPartitioning(receivedSuspectMatrix);

		for (Entry<InstanceId, Integer> receivedEntries : receivedGossipMap.entrySet()) {
			InstanceId instanceId = receivedEntries.getKey();
			Integer waitCount = receivedEntries.getValue();
			logger.debug("Checking recevied Gossip Map entry (" + instanceId + ", " + waitCount + ")!!");

			if (gossipMap.containsKey(instanceId)) {
				logger.debug("This entry exists locally. Checking the wait counts!!");
				if (waitCount < gossipMap.get(instanceId)) {
					logger.debug("Lower wait counts detected. Updating local Gossip Map!!");
					gossipMap.replace(instanceId, waitCount);
					if (waitCount < tCleanupCount) {
						logger.debug("Removing suspicion on the instance " + instanceId);
						suspectMap.replace(instanceId, false);
					}
					logger.debug("Updating local Suspect Matrix for instance " + instanceId
							+ " based on value in received Suspect Matrix");
					suspectMatrix.replace(instanceId, receivedSuspectMatrix.get(instanceId));
				}
				logger.debug("The wait count check completed");
			} else {
				logger.debug("This entry does not exist locally. Inserting in local Gossip Map!!");
				gossipMap.put(instanceId, waitCount);
				suspectMap.put(instanceId, false);
				suspectMatrix.put(instanceId, receivedSuspectMatrix.get(instanceId));
				doEnquireInstanceList = true;
			}
		}
		logGossipData("compareGossipMaps", "before-exit");
		return doEnquireInstanceList;
	}

	private void buildConsensus(final InstanceId suspectedInstanceId) {
		logGossipData("buildConsensus", "after-entry");
		final List<InstanceId> liveInstances = liveMap.get(true);
		final long globalSuspectCount = suspectMatrix.keySet().parallelStream()
				.filter(instanceId -> liveInstances.contains(instanceId))
				.filter(instanceId -> !consensusExcluded.contains(instanceId))
				.filter(instanceId -> suspectMatrix.get(instanceId).get(suspectedInstanceId)).count();
		logger.info("globalSuspectCount -->"+globalSuspectCount);
		logger.info("Consensus excluded -->"+consensusExcluded.size());

		final int consensusJuryCount = liveInstances.size() - consensusExcluded.size();
		logger.info("ConsensusJuryCount -->"+consensusJuryCount);

		if (globalSuspectCount == consensusJuryCount) {
			liveMap.get(true).remove(suspectedInstanceId);
			final List<InstanceId> deadInstances = liveMap.get(false);
			if(!deadInstances.contains(suspectedInstanceId)) {
				deadInstances.add(suspectedInstanceId);
				liveMap.put(false, deadInstances);
			}
			consensusExcluded.remove(suspectedInstanceId);
		} else if (globalSuspectCount >= consensusJuryCount / 2) {
			consensusExcluded.add(suspectedInstanceId);
		}
		logGossipData("buildConsensus", "before-exit");
	}

	@SuppressWarnings("unused")
	private void checkForNetworkPartitioning(final Map<InstanceId, Map<InstanceId, Boolean>> receivedSuspectMatrix) {
		logGossipData("checkForNetworkPartitioning", "after-entry");
		suspectMap.keySet().parallelStream().filter(instanceid -> suspectMap.get(instanceid))
				.filter(instanceId -> !isValueChanged(receivedSuspectMatrix, instanceId))
				.filter(instanceId -> gossipMap.get(instanceId) >= tPartitionCount).forEach(instanceId -> {
					logger.debug("Network partitioning detected. Instance with id " + instanceId
							+ " on other side of partition. Marking it as non-live!!");
					liveMap.get(true).remove(instanceId);
					liveMap.get(false).add(instanceId);
					consensusExcluded.remove(instanceId);
				});
		logGossipData("checkForNetworkPartitioning", "before-exit");
	}

	private boolean isValueChanged(final Map<InstanceId, Map<InstanceId, Boolean>> receivedSuspectMatrix,
			final InstanceId instanceId) {
		boolean isValueDifferent = false;
		for (InstanceId id : suspectMatrix.keySet()) {
			final Map<InstanceId, Boolean> localSuspectMap = suspectMatrix.get(id);
			final Map<InstanceId, Boolean> receivedSuspectMap = receivedSuspectMatrix.get(id);

			isValueDifferent = !(localSuspectMap.get(instanceId).equals(receivedSuspectMap.get(instanceId)));
			if (isValueDifferent) {
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

	public final Optional<Instance> getRandomLiveInstanceIfAvailable() {
		final List<InstanceId> liveInstances = liveMap.get(true);
		final int liveInstancesCount = liveInstances.size();
		if(liveInstancesCount <= 1) {
			return Optional.empty();
		}
		while (true) {
			final int randomIndex = randomNumGenerator.nextInt(liveInstancesCount);
			final InstanceId instanceId = liveInstances.get(randomIndex);
			if (!instanceId.equals(selfInstanceId)) {
				return Optional.of(instances.get(instanceId));
			}
		}
	}

	public final void markInstanceAsLive(final Instance instance) {
		logGossipData("markInstanceAsLive", "after-entry");
		logger.debug("Marking the instance " + instance + " live as just received gossip message from it!!");
		final InstanceId instanceId = instance.getInstanceId();
		if (!liveMap.get(true).contains(instanceId)) {
			liveMap.get(true).add(instanceId);
			addInstanceToInstanceList(instance);
		} else {
			logger.debug("Instance was already marked as Live. Proceeding!!");
		}
		liveMap.get(false).remove(instanceId);
		logger.debug("Current Live Map : " + liveMap);
		logGossipData("markInstanceAsLive", "before-exit");
	}

	public final void mergeInstanceLists(final List<Instance> receivedInstances) {
		logGossipData("mergeInstanceLists", "after-entry");
		receivedInstances.parallelStream().filter(instance -> {
			InstanceId instanceId = instance.getInstanceId();
			return !instanceId.equals(selfInstanceId) && !instances.containsKey(instanceId);
		}).forEach(instance -> {
			liveMap.get(true).add(instance.getInstanceId());
			addInstanceToInstanceList(instance);
		});
		logGossipData("mergeInstanceLists", "before-exit");
	}

	public List<Instance> getAllInstances() {
		return new ArrayList<Instance>(instances.values());
	}

	private void addInstanceToInstanceList(final Instance instance) {
		instances.put(instance.getInstanceId(), instance);
	}

	private void logGossipData(String methodName, String methodReach) {
		logger.info(methodName+"#"+methodReach+" -> Gossip Map: " + gossipMap);
		logger.info(methodName+"#"+methodReach+" -> Suspect Map: " + suspectMap);
		logger.info(methodName+"#"+methodReach+" -> Suspect Matrix: " + suspectMatrix);
		logger.info(methodName+"#"+methodReach+" -> Live Map: " + liveMap);
	}
}
