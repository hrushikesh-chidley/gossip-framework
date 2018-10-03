package com.gems.monitoring.tests;

import com.gems.monitoring.domain.Configurations;
import com.gems.monitoring.error.MonitoringServiceException;
import com.gems.monitoring.function.GossipAgent;
import com.gems.monitoring.function.ResourceMonitoringAgent;
import com.gems.monitoring.ga.GossipAgentImpl;
import com.gems.monitoring.rma.ResouceMonitoringAgentImpl;

public class SampleMicroservice1 {
	
	public static void main(String [] argv) throws MonitoringServiceException {
		final Configurations config = new Configurations();
		
		config.setBasePort(40025);
		config.setBroadcastIP("192.168.1.255");
		config.setCleanupCount(5);
		config.setGossipDelay(1000);
		config.setLocalPort(40025);
		config.setPartitionCount(40);
		config.setInstanceId("1");
		
		final GossipAgent gossipAgent = new GossipAgentImpl();
		final ResourceMonitoringAgent resourceMonitorinAgent = new ResouceMonitoringAgentImpl();
		
		resourceMonitorinAgent.initialize(config);
		gossipAgent.registerResourceMonitoringAgent(resourceMonitorinAgent);
		gossipAgent.initialize(config);
	}

}
