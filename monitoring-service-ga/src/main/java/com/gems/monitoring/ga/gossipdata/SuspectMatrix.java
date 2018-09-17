package com.gems.monitoring.ga.gossipdata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.gems.monitoring.commons.domain.InstanceId;

public final class SuspectMatrix {

	private final Map<InstanceId, Map<InstanceId, Boolean>> suspectMatrix;
	
	public SuspectMatrix() {
		suspectMatrix = new HashMap<>(500);
	}
	
	public final void updateSuspectMatrix( final InstanceId instanceId, final Map<InstanceId, Boolean> suspectMap ) {
		suspectMatrix.put(instanceId, suspectMap);
	}
	
	public final Map<InstanceId, Map<InstanceId, Boolean>> getSuspectMatrix() {
		return Collections.unmodifiableMap(suspectMatrix);
	}
	
}
