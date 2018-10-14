package com.framework.gossip;

import java.io.Serializable;

import com.framework.gossip.common.Configuration;
import com.framework.gossip.domain.Instance;
import com.framework.gossip.domain.InstancesInfo;
import com.framework.gossip.error.GossipException;
import com.framework.gossip.message.GossipMessage;
import com.framework.gossip.message.InstanceEnquiryRequest;
import com.framework.gossip.message.InstanceEnquiryResponse;

/**
 * The interface describing operations of the Gossip Agent.
 * <p>
 * This component provides the interface for all other components to interact
 * with each other. The central entity which starts the gossip process by
 * starting the scheduled sending of gossip message and processes each received
 * gossip message response. <br>
 * It also maintains the registered {@link GossipMessagePayloadAgent}, and
 * communicates with these registered agents for each outgoing and incoming
 * gossip message.
 * 
 * @author Hrushikesh.chidley@gmail.com
 *
 */
public interface GossipAgent {

	/**
	 * Initializes the Gossip Agent.
	 * <p>
	 * 
	 * This method shall be called to initialize this GossipAgent for receiving
	 * and/or sending the gossip message. This method initializes all other
	 * components of the Gossip Framework based on the {@link Configuration}
	 * provided.
	 *
	 * <br>
	 * Specifically, following steps are performed in the sequence given
	 * 
	 * <ul>
	 * <li>Acquire a unique instance id for itself based on {@link Configuration#getInstanceId()}</li>
	 * <li>Initialize an empty ‘Instance List’</li>
	 * <li>Insert its own instance id in ‘Gossip Map’ with value 0, in ‘Suspect Map’ with value 'FALSE' and in ‘Live Map’ 
	 * with value 'TRUE'</li>
	 * <li>Create ‘Suspect Matrix’ using its own ‘Suspect Map’</li>
	 * <li>Create a UDP socket on the port provided in {@link Configuration#getLocalPort()} to listen to gossip messages</li>
	 * <li>Broadcast the IP and port on which gossip messages can be listened alognwith its instance id over network</li>
	 * <li>Start a scheduler thread which is invoked after every {@link Configuration#getGossipDelay()} time period</li>
	 * </ul>
	 * 
	 * @param config the configurations used to initialize this agent
	 * @throws GossipException in case of any error during initialization
	 */

	void initialize(Configuration config) throws GossipException;

	/**
	 * This method is called whenever the {@link GossipMessage} from some other
	 * instance is received in this instance.
	 * 
	 * <p>
	 * This method processes the received gossip message as per the gossip
	 * algorithm. More specifically, following steps are performed in the given
	 * sequence
	 * 
	 * <ul>
	 * <li>If the received message is from instance whose id is not present in the ‘Instance List’, then add the instance id, 
	 * IP and port from received message in the ‘Instance List’</li>
	 * <li>If the received message is from instance whose id is not present in the ‘Live Map’, then add the instance id in the 
	 * ‘Live Map’ with value true</li>
	 * <li>If the received message is from instance whose id is present in the ‘Live Map’ with value false, then update the 
	 * instance id in the ‘Live Map’ with value true</li>
	 * <li>Iterate the received ‘Gossip Map’ and for each key of instance id read from map</li>
	 * <li>if this key does not exist in the local ‘Gossip Map’, enter the key and its value from received map to local map. 
	 * Mark ‘Enquire Instance List Flag’ as true</li>
	 * <li>if this key exists in the local ‘Gossip Map’, compare the value from local map with received map
	 * if the received map value is less than local map value, then update the local map value with received map value
	 * if the received map value is equal to or greater than local map value, then do nothing and move on to next entry</li>
	 * <li>For all the instance id keys for which local ‘Gossip Map’ was updated (either via insert or update), read their ‘Suspect Map’ 
	 * from received ‘Suspect Matrix’ and update it into local ‘Suspect Matrix’</li>
	 * <li>If ‘Enquire Instance List Flag’ is set to true, then send ‘Enquire Instance List’ message to instance from which the gossip 
	 * message is received and update local ‘Instance List’ to contain all instances from the response</li>
	 * </ul>
	 * 
	 * @param message the GossipMessage received in this instance
	 * @throws GossipException in case of any error during processing
	 */
	void processReceivedGossipMessage(GossipMessage message) throws GossipException;

	void processReceivedInstanceEnquireRequest(InstanceEnquiryRequest message) throws GossipException;

	void processReceivedInstanceEnquireResponse(InstanceEnquiryResponse message) throws GossipException;

	/**
	 * This method broadcasts the information about this instance i.e. it's instance id, Host IP and port only if following two conditions 
	 * are met
	 * <br>
	 * <ol>
	 * <li>If there are no live instances currently available for gossiping as per 'Live Map' maintained by this node and</li>
	 * <li>If {@link Configuration#getGossipDelay()} * {@link Configuration#getCleanupCount()} milliseconds has passed since the last 
	 * broadcast message was sent</li>
	 * </ol>
	 * 
	 * This broadcast message serves as repeated attempts to form a group in case two live instances are not aware about each other. The 
	 * repeated broadcast serves to avoid the non-reliable delivery of UDP broadcast message and the timeout value serves to ensure that
	 * the network is not flooded with broadcast messages.
	 *  
	 * @throws GossipException in case of any error during sending of broadcast message
	 */
	void broadcastIdMessageIfAppropriate() throws GossipException;

	void gossipWithNeighbor(Instance neighbor) throws GossipException;

	/**
	 * Registers the provided {@link GossipMessagePayloadAgent} with this {@link GossipAgent}
	 * <p>
	 * 
	 * This Gossip Agent calls the method {@link GossipMessagePayloadAgent#getPayloadDataForInstance(com.framework.gossip.domain.InstanceId)} passing 
	 * it's own instance id for each registered payload agent. The values returned by these calls are added as piggybacking payload on outgoing 
	 * Gossip Message before it is sent on network. 
	 * 
	 * 
	 * Additionally, this Gossip Agent calls {@link GossipMessagePayloadAgent#submitReceivedPayloadData(Serializable)} for each associated payload agent
	 * when it receives the gossip message containing the payload. 
	 * 
	 * @param gossipMessagePayloadAgent the agent to be registered 
	 */
	void registerMessagePayloadAgent(GossipMessagePayloadAgent<? extends Serializable> gossipMessagePayloadAgent);

	InstancesInfo getCurrentInstancesInfo();

}
