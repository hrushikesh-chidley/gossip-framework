package com.framework.gossip;

import java.io.Serializable;

import com.framework.gossip.common.Configurations;
import com.framework.gossip.domain.Instance;
import com.framework.gossip.error.MonitoringServiceException;
import com.framework.gossip.message.GossipMessage;
import com.framework.gossip.message.InstanceEnquiryRequest;
import com.framework.gossip.message.InstanceEnquiryResponse;

public interface GossipAgent {

	void initialize(Configurations config) throws MonitoringServiceException;
	void processReceivedGossipMessage( GossipMessage message) throws MonitoringServiceException;
	void processReceivedInstanceEnquireRequest( InstanceEnquiryRequest message) throws MonitoringServiceException;
	void processReceivedInstanceEnquireResponse( InstanceEnquiryResponse message) throws MonitoringServiceException;
	void broadcastIdMessageIfAppropriate() throws MonitoringServiceException;
	void gossipWithNeighbor(Instance neighbor) throws MonitoringServiceException;

	
	void registerMessagePayloadAgent( GossipMessagePayloadAgent<? extends Serializable> gossipMessagePayloadAgent );
	
	
}
