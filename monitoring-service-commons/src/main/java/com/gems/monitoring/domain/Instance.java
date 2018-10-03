package com.gems.monitoring.domain;

import java.io.Serializable;

import com.gems.monitoring.net.NetworkAddress;

public final class Instance implements Serializable {

	private static final long serialVersionUID = -7038623667760442368L;
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
	
	@Override
	public String toString() {
		return "[{Instance Id: "+instanceId+"}, {Network Address : "+networkAddress+"}]"; 
	}
}
