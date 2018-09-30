package com.gems.monitoring.ga.gossipdata;

import java.util.Timer;
import java.util.UUID;

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

	private ResourceMonitoringAgent resourceMonitoringAgent;

	private InstanceId ownInstanceId;
	private NetworkProxy network;

	private long lastBroadcastTime;
	
	private long tGossip;
	private int tCleanupCount;

	private InstanceId acquireInstanceId(final Configurations config) {
		return new InstanceId(config.getProvidedInstanceId().orElse(UUID.randomUUID().toString()));
	}

	@Override
	public final void initialize(final Configurations config) throws MonitoringServiceException {
		tGossip = config.getGossipDelay();
		tCleanupCount = config.getCleanupCount();
		
		ownInstanceId = acquireInstanceId(config);
		GossipData.initialize(ownInstanceId, config.getCleanupCount(), config.getPartitionCount());

		network = NetworkProxy.getInstance(config.getBasePort(), config.getLocalPort(), config.getBroadcastIP());
		network.registerForGossipMessage(this);
		GossipScheduler gossipScheduler = new GossipScheduler(this);

		new Timer().scheduleAtFixedRate(gossipScheduler, config.getGossipDelay(), config.getGossipDelay());
	}

	@Override
	public void registerResourceMonitoringAgent(final ResourceMonitoringAgent resourceMonitoringAgent) {
		this.resourceMonitoringAgent = resourceMonitoringAgent;
	}

	@Override
	public void processReceivedGossipMessage(final GossipMessage message) throws MonitoringServiceException {
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
		if ((currentTime - lastBroadcastTime) > tGossip * tCleanupCount) {
			network.broadcastMessage(createGossipMessageToSend());
			lastBroadcastTime = currentTime;
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

		return new GossipMessage(ownInstanceId, gossipData.getGossipMapToSend(), gossipData.getSuspectMatrixToSend(),
				network.getSelfNetworkAddress(), monitoringData);
	}


}
