package com.gems.monitoring.function;

import com.gems.monitoring.domain.GossipMessage;

public interface GossipAgent {

	void registerResourceMonitoringAgent( ResourceMonitoringAgent resourceMonitoringAgent );
	
	void processReceivedGossipMessage( GossipMessage message);
	
}
