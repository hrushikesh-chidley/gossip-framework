package com.gems.monitoring.domain;

import java.util.UUID;

public class Configurations {

	private InstanceId instanceId;
	private String broadcastIP;
	private int basePort;
	private int localPort;
	private long tGossip;
	private int tCleanupCount;
	private int tPartitionCount;

	public Configurations() {
		super();
		instanceId = new InstanceId(UUID.randomUUID().toString());
	}

	public final InstanceId getInstanceId() {
		return instanceId;
	}

	public final String getBroadcastIP() {
		return broadcastIP;
	}

	public final int getBasePort() {
		return basePort;
	}

	public final int getLocalPort() {
		return localPort;
	}

	public final long getGossipDelay() {
		return tGossip;
	}

	public final int getCleanupCount() {
		return tCleanupCount;
	}

	public final int getPartitionCount() {
		return tPartitionCount;
	}

	public final void setInstanceId(String providedInstanceId) {
		instanceId = new InstanceId(providedInstanceId);
	}

	public final void setBroadcastIP(String broadcastIP) {
		this.broadcastIP = broadcastIP;
	}

	public final void setBasePort(int basePort) {
		this.basePort = basePort;
	}

	public final void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public final void setGossipDelay(long tGossip) {
		this.tGossip = tGossip;
	}

	public final void setCleanupCount(int tCleanupCount) {
		this.tCleanupCount = tCleanupCount;
	}

	public final void setPartitionCount(int tPartitionCount) {
		this.tPartitionCount = tPartitionCount;
	}

	public String toString() {
		return "Configuration : [{instanceId: " + instanceId + "}, {broadcastIP: " + broadcastIP + "}, {basePort: "
				+ basePort + "}, {localPort: " + localPort + "}, {Gossip Delay(ms): " + tGossip + "}, {Cleanup Count: "
				+ tCleanupCount + "}, {Partition Count: " + tPartitionCount + "}]";
	}

}
