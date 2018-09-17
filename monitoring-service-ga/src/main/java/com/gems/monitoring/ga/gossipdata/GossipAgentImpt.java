package com.gems.monitoring.ga.gossipdata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.gems.monitoring.commons.domain.InstanceId;

public class GossipAgentImpt {
	
	public static void main(String [] argv) {
		final InstanceId instanceId = new InstanceId("1");
		final InstanceList instanceList = new InstanceList();
		final GossipAndSuspectMap gossipAndSuspectMap = new GossipAndSuspectMap(instanceId);
		final LiveMap liveMap = new LiveMap(instanceId);
		final SuspectMatrix suspectMatrix = new SuspectMatrix();
		suspectMatrix.updateSuspectMatrix(instanceId, gossipAndSuspectMap.getSuspectMap());
		
		final Map<InstanceId, Integer> newMap = new HashMap<>();
		final InstanceId instanceId2 = new InstanceId("2");
		final InstanceId instanceId3 = new InstanceId("3");
		final InstanceId instanceId4 = new InstanceId("4");
		newMap.put(instanceId2, 12);
		newMap.put(instanceId3, 56);
		newMap.put(instanceId4, 2);
		
		final Map<InstanceId, Boolean> newSMap = new HashMap<>();
		newSMap.put(instanceId2, true);
		newSMap.put(instanceId3, false);
		newSMap.put(instanceId4, true);
		
		final Map<InstanceId, Map<InstanceId, Boolean>> newSMat = new HashMap<>();
		newSMat.put(instanceId2, newSMap);
		newSMat.put(instanceId3, newSMap);
		newSMat.put(instanceId4, newSMap);
		
		gossipAndSuspectMap.compareGossipMaps(newMap);
		suspectMatrix.updateSuspectMatrix(instanceId2, newSMap);
		suspectMatrix.updateSuspectMatrix(instanceId3, newSMap);
		suspectMatrix.updateSuspectMatrix(instanceId4, newSMap);
		
	
		final NetworkProxy networkProxy = new NetworkProxy(instanceId, 10023);
		networkProxy.sendOverNetwork(Optional.empty(), gossipAndSuspectMap.getGossipMapToSend(), suspectMatrix.getSuspectMatrix());
	}

}
