package com.gems.monitoring.ga;

import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gems.monitoring.domain.Configurations;
import com.gems.monitoring.domain.GossipMessage;
import com.gems.monitoring.domain.Instance;
import com.gems.monitoring.domain.InstanceEnquiryRequest;
import com.gems.monitoring.domain.InstanceEnquiryResponse;
import com.gems.monitoring.domain.InstanceId;
import com.gems.monitoring.domain.MonitoredData;
import com.gems.monitoring.error.MonitoringServiceException;
import com.gems.monitoring.function.GossipAgent;
import com.gems.monitoring.function.ResourceMonitoringAgent;
import com.gems.monitoring.net.NetworkProxy;

public class GossipAgentImpl implements GossipAgent {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ResourceMonitoringAgent resourceMonitoringAgent;

	private InstanceId ownInstanceId;
	private NetworkProxy network;

	private long lastBroadcastTime;
	
	private long tGossip;
	private int tCleanupCount;


	@Override
	public final void initialize(final Configurations config) throws MonitoringServiceException {
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
	public void registerResourceMonitoringAgent(final ResourceMonitoringAgent resourceMonitoringAgent) {
		this.resourceMonitoringAgent = resourceMonitoringAgent;
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
		
		message.getMonitoringData().ifPresent(monitoringData -> resourceMonitoringAgent.submitReceivedMonitoringData(monitoringData));

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
		final MonitoredData monitoringData = resourceMonitoringAgent.getMonitoredDataForInstance(ownInstanceId);
		GossipData gossipData = GossipData.getInstance();

		final GossipMessage gossipMessage = new GossipMessage(ownInstanceId, gossipData.getGossipMapToSend(), gossipData.getSuspectMatrixToSend(),
				network.getSelfNetworkAddress(), monitoringData);
		logger.debug("Gossip message created : "+gossipMessage);
		return gossipMessage;
	}


}
