package com.gems.monitoring.ga.gossipdata;

import java.util.HashMap;
import java.util.Map;

import com.gems.monitoring.commons.domain.InstanceId;

public class LiveMap {
	
	private final Map<InstanceId, Boolean> liveMap;
	
	public LiveMap( final InstanceId selfInstance) {
		super();
		liveMap = new HashMap<>(500);
		
		liveMap.put(selfInstance, true);
	}

}
