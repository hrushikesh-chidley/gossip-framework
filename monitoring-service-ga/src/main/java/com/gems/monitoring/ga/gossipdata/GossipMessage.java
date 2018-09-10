package com.gems.monitoring.ga.gossipdata;

import java.io.Serializable;
import java.util.Map;

import com.gems.monitoring.commons.domain.InstanceId;

public final class GossipMessage implements Serializable {

	private static final long serialVersionUID = 6162811529659385490L;

	private final InstanceId instanceId;
	private final Map<InstanceId, Integer> gossipMap;
	private final Map<InstanceId, Map<InstanceId, Boolean>> suspectMatrix;
	private NetworkAddress sourceAddress;

	public GossipMessage(final InstanceId instanceId, final Map<InstanceId, Integer> gossipMap,
			final Map<InstanceId, Map<InstanceId, Boolean>> suspectMatrix, final NetworkAddress sourceAddress) {
		super();
		this.instanceId = instanceId;
		this.gossipMap = gossipMap;
		this.suspectMatrix = suspectMatrix;
		this.sourceAddress = sourceAddress;
	}

	public final InstanceId getInstanceId() {
		return instanceId;
	}

	public final Map<InstanceId, Integer> getGossipMap() {
		return gossipMap;
	}

	public final Map<InstanceId, Map<InstanceId, Boolean>> getSuspectMatrix() {
		return suspectMatrix;
	}

	public final NetworkAddress getSourceAddress() {
		return sourceAddress;
	}

}
