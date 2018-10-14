package com.monitoring.extension.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class TestConfiguration {

	@Value("${application.instance.id}")
	private String instanceId;
	
	@Value("${application.broadcast.ip}")
	private String broadcastIP;
	
	@Value("${application.local.port}")
	private int localPort;
	
	@Value("${application.gossip.delay}")
	private int gossipDelay;

	@Value("${application.cleanup.count}")
	private int cleanupCount;

	@Value("${application.partition.count}")
	private int partitionCount;

	public final String getInstanceId() {
		return instanceId;
	}

	public final String getBroadcastIP() {
		return broadcastIP;
	}

	public final int getGossipDelay() {
		return gossipDelay;
	}

	public final int getCleanupCount() {
		return cleanupCount;
	}

	public final int getPartitionCount() {
		return partitionCount;
	}

	public final void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public final void setBroadcastIP(String broadcastIP) {
		this.broadcastIP = broadcastIP;
	}

	public final void setGossipDelay(int gossipDelay) {
		this.gossipDelay = gossipDelay;
	}

	public final void setCleanupCount(int cleanupCount) {
		this.cleanupCount = cleanupCount;
	}

	public final void setPartitionCount(int partitionCount) {
		this.partitionCount = partitionCount;
	}

	public final int getLocalPort() {
		return localPort;
	}

	public final void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

}
