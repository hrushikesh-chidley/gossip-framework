package com.gems.monitoring.domain;

import java.util.Map;
import java.util.Optional;

import com.gems.monitoring.net.NetworkAddress;

public final class GossipMessage extends Message {

	private static final long serialVersionUID = 6162811529659385490L;

	private final InstanceId instanceId;
	private final Map<InstanceId, Integer> gossipMap;
	private final Map<InstanceId, Map<InstanceId, Boolean>> suspectMatrix;
	
	private MonitoredData monitoringData;

	public GossipMessage(final InstanceId instanceId, final Map<InstanceId, Integer> gossipMap,
			final Map<InstanceId, Map<InstanceId, Boolean>> suspectMatrix, final NetworkAddress sourceAddress) {
		super(MessageTypes.GOSSIP, sourceAddress);
		this.instanceId = instanceId;
		this.gossipMap = gossipMap;
		this.suspectMatrix = suspectMatrix;
	}

	public GossipMessage(final InstanceId instanceId, final Map<InstanceId, Integer> gossipMap,
			final Map<InstanceId, Map<InstanceId, Boolean>> suspectMatrix, 
			final NetworkAddress sourceAddress, final MonitoredData monitoringData ) {
		this(instanceId, gossipMap, suspectMatrix, sourceAddress);
		this.monitoringData = monitoringData;
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

	public final Optional<MonitoredData> getMonitoringData() {
		return monitoringData == null ? Optional.empty() : Optional.of(monitoringData);
	}
	
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("\nGossip Message : [");
		buffer.append("\n{Instance Id: "+instanceId+"}");
		buffer.append("\n{Gossip Map: ");
		gossipMap.forEach((instanceId, count) -> buffer.append("("+instanceId+","+count+")"));
		buffer.append("}");
		buffer.append("\n{Suspect Matrix: ");
		suspectMatrix.forEach((instanceId, suspectMap) ->{
			buffer.append("\n\t"+instanceId+" ->");
			suspectMap.forEach((id, suspicion) -> buffer.append("("+id+","+suspicion+")"));
			
		});
		buffer.append("\n}\n{Monitoring Data: "+monitoringData+"}");
		buffer.append("\n]");
		return buffer.toString();
	}

}
