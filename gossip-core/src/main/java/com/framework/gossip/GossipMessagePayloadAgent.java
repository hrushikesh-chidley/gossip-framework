package com.framework.gossip;

import java.io.Serializable;

import com.framework.gossip.common.Configuration;
import com.framework.gossip.domain.InstanceId;

public interface GossipMessagePayloadAgent<Payload extends Serializable> {

	void initialize(Configuration config);
	
	/**
	 * The method is called by gossip agent to collect the payload data to be sent
	 * as piggybacking data on outgoing gossip message.
	 * <p>
	 * 
	 * The payload data must be serializable to network. 
	 * The implementation should not wait indefinitely for the payload data to be returned by agent. 
	 * It is advised that agents cache their data instead of returning it during runtime.
	 * 
	 * @param instanceId the instance id for which the data us required
	 * @return the serializable data to be sent with gossip message
	 */
	Payload getPayloadDataForInstance(InstanceId instanceId);

	/**
	 * The method is called by gossip agent to submit the received payload data to this agent.
	 * 
	 * The GossipAgent is not responsible if the data in the payload is corrupted. As such this method
	 * shall not throw any checked exception. This agent must handle all error conditions without 
	 * need to communicate these to gossip agent.
	 *  
	 * @param payloadData the data received as part of gossip message
	 */
	void submitReceivedPayloadData(Serializable payloadData);

}
