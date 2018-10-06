package com.framework.gossip.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.framework.gossip.GossipAgent;
import com.framework.gossip.GossipMessagePayloadAgent;
import com.framework.gossip.common.Configurations;
import com.framework.gossip.domain.Instance;
import com.framework.gossip.domain.InstanceId;
import com.framework.gossip.error.MonitoringServiceException;
import com.framework.gossip.message.GossipMessage;
import com.framework.gossip.message.InstanceEnquiryRequest;
import com.framework.gossip.message.InstanceEnquiryResponse;
import com.framework.gossip.network.NetworkProxy;

public class GossipAgentImpl implements GossipAgent {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private InstanceId ownInstanceId;
	private NetworkProxy network;

	private long lastBroadcastTime;
	
	private long tGossip;
	private int tCleanupCount;


	@Override
	public final void initialize(final Configurations config) throws MonitoringServiceException {
		config.validateConfigComplete();
		logger.debug("Initializing the Gossip Agent with configurations : "+config);
		tGossip = config.getGossipDelay();
		tCleanupCount = config.getCleanupCount();
		
		ownInstanceId = config.getInstanceId();
		GossipData.initialize(ownInstanceId, config.getCleanupCount(), config.getPartitionCount());

		network = NetworkProxy.getInstance(config.getBasePort(), config.getLocalPort(), config.getBroadcastIP());
		network.registerForGossipMessage(this);
		final GossipScheduler gossipScheduler = new GossipScheduler(this);

		logger.debug("Scheduling gossip every "+config.getGossipDelay()+" milliseconds");
		new Timer().scheduleAtFixedRate(gossipScheduler, config.getGossipDelay(), config.getGossipDelay());
		logger.debug("Gossip Agent initialized");
	}

	@Override
	public void processReceivedGossipMessage(final GossipMessage message) throws MonitoringServiceException {
		logger.debug("Received Gossip Message. "+message);
		final GossipData gossipData = GossipData.getInstance();

		final InstanceId receivedInstanceId = message.getInstanceId();
		final Instance instance = new Instance(receivedInstanceId, message.getSourceAddress());
		gossipData.markInstanceAsLive(instance);
		if (gossipData.compareGossipMaps(message.getGossipMap(), message.getSuspectMatrix())) {
			final InstanceEnquiryRequest messageToSend = new InstanceEnquiryRequest(network.getSelfNetworkAddress());
			network.sendToDestination(messageToSend, message.getSourceAddress());
		}
		
		message.getPayloadData().forEach((agentId, payloadData) -> {
			final GossipMessagePayloadAgent<? extends Serializable> gossipMessagePayloadAgent = payloadAgents.get(agentId);
			if(gossipMessagePayloadAgent != null) {
				gossipMessagePayloadAgent.submitReceivedPayloadData(payloadData);
			}
		});
	}

	@Override
	public void broadcastIdMessageIfAppropriate() throws MonitoringServiceException {
		final long currentTime = System.currentTimeMillis();
		final long timeSinceLastBroadcast = currentTime-lastBroadcastTime;
		logger.debug("Time since last broadcast message was sent is: "+timeSinceLastBroadcast+" milliseconds");
		if ((timeSinceLastBroadcast) > (tGossip * tCleanupCount)) {
			logger.debug("Sending the next broadcast message");
			network.broadcastMessage(createGossipMessageToSend());
			lastBroadcastTime = currentTime;
		} else {
			logger.debug("Ignoring the trigger!!");
		}
	}

	@Override
	public void gossipWithNeighbor(Instance neighbor) throws MonitoringServiceException {
		network.sendToDestination(createGossipMessageToSend(), neighbor.getNetworkAddress());
	}

	@Override
	public void processReceivedInstanceEnquireRequest(final InstanceEnquiryRequest message)
			throws MonitoringServiceException {
		network.sendToDestination(new InstanceEnquiryResponse(network.getSelfNetworkAddress(),
				GossipData.getInstance().getAllInstances()), message.getSourceAddress());
	}

	@Override
	public void processReceivedInstanceEnquireResponse( final InstanceEnquiryResponse message )
			throws MonitoringServiceException {
		GossipData.getInstance().mergeInstanceLists(message.getReceivedInstanceList());
	}
	
	private GossipMessage createGossipMessageToSend() {
		final GossipData gossipData = GossipData.getInstance();
		final GossipMessage gossipMessage = new GossipMessage(ownInstanceId, gossipData.getGossipMapToSend(), gossipData.getSuspectMatrixToSend(),
				network.getSelfNetworkAddress());
		payloadAgents.forEach( (agentId, agent) -> {
			gossipMessage.addPayloadData(agentId, agent.getPayloadDataForInstance(ownInstanceId));
		});
		
		logger.debug("Gossip message created : "+gossipMessage);
		return gossipMessage;
	}
	
	/*
	private void printMonitoringDataRegularly() {
		final Runnable monitoringJob = () -> {
			final StringBuffer buffer = new StringBuffer();
			
			final GossipData gossipData = GossipData.getInstance();
			final Set<InstanceId> liveInstances = gossipData.getLiveMap().get(true);
			final Set<InstanceId> downInstances = gossipData.getLiveMap().get(false);
			
			buffer.append("\n{");
			buffer.append("\n\t\"live-instances\": \"");
			final String liveInstanceList = String.join(", ", liveInstances.parallelStream()
					.map(instanceId -> instanceId.toString()).collect(Collectors.toList()));
			
			buffer.append(liveInstanceList+"\",");
			buffer.append("\n\t\"down-instances\": \"");
			final String downInstanceList = String.join(", ", downInstances.parallelStream()
					.map(instanceId -> instanceId.toString()).collect(Collectors.toList()));
			
			buffer.append(downInstanceList+"\",");
			
			liveInstances.forEach(instanceId -> {
				final MonitoredData monitoredData = resourceMonitoringAgent.getMonitoredDataForInstance(instanceId);
				
				buffer.append("\n\t{");
				buffer.append("\n\t\t\"instance-id\" : \""+monitoredData.getInstanceId()+"\",");
				buffer.append("\n\t\t\"monitoring-data\" : {\n\t\t\t");
				
				String monitoringData = String.join(",\n\t\t\t",
						monitoredData.getMonitoredData().parallelStream()
								.map(data -> data.getResourceName() + "\" : \"" + data.getCurrentValue() + "\"")
								.collect(Collectors.toList()));
				buffer.append(monitoringData);

				buffer.append("\n\t\t}");
				buffer.append("\n\t}");
			});
			buffer.append("\n}");
			logger.info(buffer.toString());
		};
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(monitoringJob, 10, 10, TimeUnit.SECONDS);
	}
	*/

	@Override
	public void registerMessagePayloadAgent( final 
			GossipMessagePayloadAgent<? extends Serializable> gossipMessagePayloadAgent) {
		payloadAgents.put(gossipMessagePayloadAgent.getClass().getName(), gossipMessagePayloadAgent);
		
	}
	
	private final Map<String, GossipMessagePayloadAgent<? extends Serializable>> payloadAgents = new ConcurrentHashMap<>(5);


}
