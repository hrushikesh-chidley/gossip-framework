package com.gems.monitoring.ga.gossipdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gems.monitoring.domain.InstanceId;

public final class GossipAndSuspectMap {

	private final Map<InstanceId, Integer> gossipMap;
	private final Map<InstanceId, Boolean> suspectMap;
	
	private final InstanceId selfInstanceId;
	
	public GossipAndSuspectMap( final InstanceId instanceId ) {
		super();
		gossipMap = new HashMap<>(500);
		suspectMap = new HashMap<>(500);
		
		gossipMap.put(instanceId, 0);
		suspectMap.put(instanceId, false);
		
		this.selfInstanceId = instanceId;
	}
	
	public final void incrementGossipWaitCountForAllExceptSelf() {
		gossipMap.forEach((instanceId, waitCount) -> {
			if(!instanceId.equals(selfInstanceId)) 
				waitCount = waitCount + 1;
				gossipMap.put(instanceId, waitCount);
				if(waitCount >= 20) {
					suspectMap.put(instanceId, true);
				}
			}
		);
	}
	
	public final List<InstanceId> compareGossipMaps( final Map<InstanceId, Integer> receivedMap ) {
		final List<InstanceId> updateSuspectMatrixFor = new ArrayList<>(500);
		receivedMap.forEach((instanceId, waitCount) -> {
			if(gossipMap.containsKey(instanceId)) {
				if(waitCount < gossipMap.get(instanceId)) {
					gossipMap.put(instanceId, waitCount);
					if(waitCount < 20) {
						suspectMap.put(instanceId, false);
					}
					updateSuspectMatrixFor.add(instanceId);
				} 
			} else {
				gossipMap.put(instanceId, waitCount);
				updateSuspectMatrixFor.add(instanceId);
			}
		});
		return updateSuspectMatrixFor;
	}
	
	public final Map<InstanceId, Integer> getGossipMapToSend() {
		return Collections.unmodifiableMap(gossipMap);
	}
	
	public final Map<InstanceId, Boolean> getSuspectMap() {
		return Collections.unmodifiableMap(suspectMap);
	}
	
	
}
