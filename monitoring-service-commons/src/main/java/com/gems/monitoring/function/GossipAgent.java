package com.gems.monitoring.function;

import com.gems.monitoring.domain.Configurations;
import com.gems.monitoring.domain.GossipMessage;
import com.gems.monitoring.domain.Instance;
import com.gems.monitoring.domain.InstanceEnquiryRequest;
import com.gems.monitoring.domain.InstanceEnquiryResponse;
import com.gems.monitoring.error.MonitoringServiceException;

public interface GossipAgent {

	void initialize(Configurations config) throws MonitoringServiceException;

	void registerResourceMonitoringAgent( ResourceMonitoringAgent resourceMonitoringAgent );
	
	void processReceivedGossipMessage( GossipMessage message) throws MonitoringServiceException;
	void processReceivedInstanceEnquireRequest( InstanceEnquiryRequest message) throws MonitoringServiceException;
	void processReceivedInstanceEnquireResponse( InstanceEnquiryResponse message) throws MonitoringServiceException;
	
	void broadcastIdMessageIfAppropriate() throws MonitoringServiceException;
	
	void gossipWithNeighbor(Instance neighbor) throws MonitoringServiceException;
	
}
