package com.framework.gossip.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public final class InstancesInfo implements Serializable {

	private static final long serialVersionUID = 1367126048655729433L;

	private final Set<InstanceId> liveInstances;
	private final Set<InstanceId> downInstances;
	
	public InstancesInfo( final Set<InstanceId> liveInstances, final Set<InstanceId> downInstances ) {
		this.liveInstances = new HashSet<InstanceId>(liveInstances);
		this.downInstances = new HashSet<InstanceId>(downInstances);
	}
	
	public final Set<InstanceId> getLiveInstances() {
		return liveInstances;
	}

	public final Set<InstanceId> getDownInstances() {
		return downInstances;
	}
	
}
