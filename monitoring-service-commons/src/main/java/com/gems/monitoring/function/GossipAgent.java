package com.gems.monitoring.function;

import java.io.Serializable;

import com.gems.monitoring.config.Configurations;
import com.gems.monitoring.domain.Instance;
import com.gems.monitoring.error.MonitoringServiceException;
import com.gems.monitoring.message.GossipMessage;
import com.gems.monitoring.message.InstanceEnquiryRequest;
import com.gems.monitoring.message.InstanceEnquiryResponse;

public interface GossipAgent {

	void initialize(Configurations config) throws MonitoringServiceException;
	void processReceivedGossipMessage( GossipMessage message) throws MonitoringServiceException;
	void processReceivedInstanceEnquireRequest( InstanceEnquiryRequest message) throws MonitoringServiceException;
	void processReceivedInstanceEnquireResponse( InstanceEnquiryResponse message) throws MonitoringServiceException;
	void broadcastIdMessageIfAppropriate() throws MonitoringServiceException;
	void gossipWithNeighbor(Instance neighbor) throws MonitoringServiceException;

	
	void registerMessagePayloadAgent( GossipMessagePayloadAgent<? extends Serializable> gossipMessagePayloadAgent );
	
	
}
