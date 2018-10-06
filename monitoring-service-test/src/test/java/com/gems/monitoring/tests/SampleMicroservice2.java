package com.gems.monitoring.tests;

import com.gems.monitoring.config.Configurations;
import com.gems.monitoring.error.MonitoringServiceException;
import com.gems.monitoring.function.GossipAgent;
import com.gems.monitoring.ga.GossipAgentImpl;
import com.gems.monitoring.rma.ResouceMonitoringAgentImpl;

public class SampleMicroservice2 {
	
	public static void main(String [] argv) throws MonitoringServiceException {
		final Configurations config = new Configurations();
		
		config.setBasePort(40025);
		config.setBroadcastIP("192.168.1.255");
		config.setCleanupCount(15);
		config.setGossipDelay(200);
		config.setLocalPort(40026);
		config.setPartitionCount(200);
		config.setInstanceId("2");
		
		final GossipAgent gossipAgent = new GossipAgentImpl();
		final ResouceMonitoringAgentImpl resourceMonitorinAgent = new ResouceMonitoringAgentImpl();
		
		resourceMonitorinAgent.initialize(config);
		gossipAgent.registerMessagePayloadAgent(resourceMonitorinAgent);
		gossipAgent.initialize(config);
	}

}
