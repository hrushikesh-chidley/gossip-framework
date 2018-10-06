package com.monitoring.extension.tests;

import com.framework.gossip.GossipAgent;
import com.framework.gossip.common.Configurations;
import com.framework.gossip.error.MonitoringServiceException;
import com.framework.gossip.impl.GossipAgentImpl;
import com.monitoring.extension.ResouceMonitoringAgentImpl;

public class SampleMicroservice1 {
	
	public static void main(String [] argv) throws MonitoringServiceException {
		final Configurations config = new Configurations();
		
		config.setBasePort(40025);
		config.setBroadcastIP("192.168.1.255");
		config.setCleanupCount(15);
		config.setGossipDelay(200);
		config.setLocalPort(40025);
		config.setPartitionCount(200);
		config.setInstanceId("1");
		
		final GossipAgent gossipAgent = new GossipAgentImpl();
		final ResouceMonitoringAgentImpl resourceMonitorinAgent = new ResouceMonitoringAgentImpl();
		
		resourceMonitorinAgent.initialize(config);
		gossipAgent.registerMessagePayloadAgent(resourceMonitorinAgent);
		gossipAgent.initialize(config);
	}

}
