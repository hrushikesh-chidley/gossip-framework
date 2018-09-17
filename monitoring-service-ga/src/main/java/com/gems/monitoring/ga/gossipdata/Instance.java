package com.gems.monitoring.ga.gossipdata;

import com.gems.monitoring.commons.domain.InstanceId;

public final class Instance {

	private final InstanceId instanceId;
	private final NetworkAddress networkAddress;
	
	public Instance(final InstanceId instanceId, final NetworkAddress networkAddress) {
		super();
		this.instanceId = instanceId;
		this.networkAddress = networkAddress;
	}

	public final InstanceId getInstanceId() {
		return instanceId;
	}

	public final NetworkAddress getNetworkAddress() {
		return networkAddress;
	}

	@Override
	public boolean equals(Object anotherInstance) {
		return anotherInstance instanceof Instance 
				&& ((Instance) anotherInstance).getInstanceId().equals(instanceId);
	}

	@Override
	public int hashCode() {
		return instanceId.hashCode();
	}
}
