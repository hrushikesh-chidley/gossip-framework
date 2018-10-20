package com.framework.gossip.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.framework.gossip.GossipAgent;
import com.framework.gossip.GossipMessagePayloadAgent;
import com.framework.gossip.common.Configuration;
import com.framework.gossip.domain.Instance;
import com.framework.gossip.domain.InstanceId;
import com.framework.gossip.domain.InstancesInfo;
import com.framework.gossip.error.GossipException;
import com.framework.gossip.message.GossipMessage;
import com.framework.gossip.message.InstanceEnquiryRequest;
import com.framework.gossip.message.InstanceEnquiryResponse;
import com.framework.gossip.network.NetworkAdapter;

public class GossipAgentImpl implements GossipAgent {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ExecutorService executor = Executors.newCachedThreadPool();

	private final Map<String, GossipMessagePayloadAgent<? extends Serializable>> payloadAgents = new ConcurrentHashMap<>(
			5);

	private InstanceId ownInstanceId;
	private NetworkAdapter network;

	private long lastBroadcastTime;

	private long tGossip;
	private int tCleanupCount;

	@Override
	public final void initialize(final Configuration config) throws GossipException {
		config.validateConfigComplete();
		logger.debug("Initializing the Gossip Agent with configurations : " + config);
		tGossip = config.getGossipDelay();
		tCleanupCount = config.getCleanupCount();

		ownInstanceId = config.getInstanceId();
		GossipData.initialize(ownInstanceId, config.getCleanupCount(), config.getPartitionCount());

		network = NetworkAdapter.getInstance(config.getBasePort(), config.getLocalPort(), config.getBroadcastIP());
		network.registerForGossipMessage(this);
		final GossipScheduler gossipScheduler = new GossipScheduler(this);

		logger.debug("Scheduling gossip every " + config.getGossipDelay() + " milliseconds");
		new Timer().scheduleAtFixedRate(gossipScheduler, config.getGossipDelay(), config.getGossipDelay());
		logger.debug("Gossip Agent initialized");
	}

	@Override
	public void processReceivedGossipMessage(final GossipMessage message) throws GossipException {
		logger.debug("Received Gossip Message. " + message);
		final GossipData gossipData = GossipData.getInstance();

		final InstanceId receivedInstanceId = message.getInstanceId();
		final Instance instance = new Instance(receivedInstanceId, message.getSourceAddress());
		gossipData.markInstanceAsLive(instance);
		if (gossipData.compareGossipMaps(message.getGossipMap(), message.getSuspectMatrix())) {
			final InstanceEnquiryRequest messageToSend = new InstanceEnquiryRequest(network.getSelfNetworkAddress());
			network.sendToDestination(messageToSend, message.getSourceAddress());
		}

		message.getPayloadData().forEach((agentId, payloadData) -> {
			final GossipMessagePayloadAgent<? extends Serializable> gossipMessagePayloadAgent = payloadAgents
					.get(agentId);
			if (gossipMessagePayloadAgent != null) {
				submitPayloadData(gossipMessagePayloadAgent, payloadData);
			}
		});
	}

	@Override
	public void broadcastIdMessageIfAppropriate() throws GossipException {
		final long currentTime = System.currentTimeMillis();
		final long timeSinceLastBroadcast = currentTime - lastBroadcastTime;
		logger.debug("Time since last broadcast message was sent is: " + timeSinceLastBroadcast + " milliseconds");
		if ((timeSinceLastBroadcast) > (tGossip * tCleanupCount)) {
			logger.debug("Sending the next broadcast message");
			network.broadcastMessage(createGossipMessageToSend());
			lastBroadcastTime = currentTime;
		} else {
			logger.debug("Ignoring the trigger!!");
		}
	}

	@Override
	public void gossipWithNeighbor(Instance neighbor) throws GossipException {
		network.sendToDestination(createGossipMessageToSend(), neighbor.getNetworkAddress());
	}

	@Override
	public void processReceivedInstanceEnquireRequest(final InstanceEnquiryRequest message)
			throws GossipException {
		network.sendToDestination(new InstanceEnquiryResponse(network.getSelfNetworkAddress(),
				GossipData.getInstance().getAllInstances()), message.getSourceAddress());
	}

	@Override
	public void processReceivedInstanceEnquireResponse(final InstanceEnquiryResponse message)
			throws GossipException {
		GossipData.getInstance().mergeInstanceLists(message.getReceivedInstanceList());
	}

	@Override
	public void registerMessagePayloadAgent(
			final GossipMessagePayloadAgent<? extends Serializable> gossipMessagePayloadAgent) {
		payloadAgents.put(gossipMessagePayloadAgent.getClass().getName(), gossipMessagePayloadAgent);

	}

	@Override
	public InstancesInfo getCurrentInstancesInfo() {
		final GossipData gossipData = GossipData.getInstance();
		return new InstancesInfo(gossipData.getLiveMap().get(true), gossipData.getLiveMap().get(false));
	}

	private GossipMessage createGossipMessageToSend() {
		final GossipData gossipData = GossipData.getInstance();
		final GossipMessage gossipMessage = new GossipMessage(ownInstanceId, gossipData.getGossipMapToSend(),
				gossipData.getSuspectMatrixToSend(), network.getSelfNetworkAddress());

		payloadAgents.forEach((agentId, agent) -> {
			collectPayloadData(agent).ifPresent(data -> gossipMessage.addPayloadData(agentId, data));
		});

		logger.debug("Gossip message created : " + gossipMessage);
		return gossipMessage;
	}

	private void submitPayloadData(final GossipMessagePayloadAgent<? extends Serializable> payloadAgent,
			final Serializable payloadData) {
		new Thread(() -> payloadAgent.submitReceivedPayloadData(payloadData)).start();
	}

	private Optional<Serializable> collectPayloadData(
			final GossipMessagePayloadAgent<? extends Serializable> payloadAgent) {
		try {
			return Optional.ofNullable(executor.submit(() -> {
				return payloadAgent.getPayloadDataForInstance(ownInstanceId);
			}).get(15, TimeUnit.MILLISECONDS));
		} catch (TimeoutException | InterruptedException | ExecutionException te) {
			logger.warn("Could not collect payload data from " + payloadAgent.getClass().getCanonicalName()
					+ " due to timeout/exception while collecting!");
			return Optional.empty();
		}
	}
}
