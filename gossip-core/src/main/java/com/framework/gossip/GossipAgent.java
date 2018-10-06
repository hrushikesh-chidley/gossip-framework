package com.framework.gossip;

import java.io.Serializable;

import com.framework.gossip.common.Configurations;
import com.framework.gossip.domain.Instance;
import com.framework.gossip.domain.InstancesInfo;
import com.framework.gossip.error.GossipException;
import com.framework.gossip.message.GossipMessage;
import com.framework.gossip.message.InstanceEnquiryRequest;
import com.framework.gossip.message.InstanceEnquiryResponse;

public interface GossipAgent {

	void initialize(Configurations config) throws GossipException;
	void processReceivedGossipMessage( GossipMessage message) throws GossipException;
	void processReceivedInstanceEnquireRequest( InstanceEnquiryRequest message) throws GossipException;
	void processReceivedInstanceEnquireResponse( InstanceEnquiryResponse message) throws GossipException;
	void broadcastIdMessageIfAppropriate() throws GossipException;
	void gossipWithNeighbor(Instance neighbor) throws GossipException;

	
	void registerMessagePayloadAgent( GossipMessagePayloadAgent<? extends Serializable> gossipMessagePayloadAgent );
	InstancesInfo getCurrentInstancesInfo();
	
}
