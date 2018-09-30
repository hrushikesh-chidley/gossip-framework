package com.gems.monitoring.domain;

import java.util.Optional;

public class Configurations {

	private Optional<String> providedInstanceId = Optional.empty();
	private String broadcastIP;
	private int basePort;
	private int localPort;
	private long tGossip;
	private int tCleanupCount;
	private int tPartitionCount;
	
	public final Optional<String> getProvidedInstanceId() {
		return providedInstanceId;
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
	public final void setProvidedInstanceId(String providedInstanceId) {
		this.providedInstanceId = Optional.ofNullable(providedInstanceId);
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
	
	
}
