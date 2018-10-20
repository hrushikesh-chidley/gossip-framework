package com.framework.gossip.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.framework.gossip.GossipAgent;
import com.framework.gossip.common.Constants;
import com.framework.gossip.domain.NetworkAddress;
import com.framework.gossip.error.ErrorCodes;
import com.framework.gossip.error.GossipException;
import com.framework.gossip.message.GossipMessage;
import com.framework.gossip.message.InstanceEnquiryRequest;
import com.framework.gossip.message.InstanceEnquiryResponse;
import com.framework.gossip.message.Message;

public final class NetworkAdapter {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final ExecutorService receviedMessageExecutors = Executors.newCachedThreadPool();
	private final ExecutorService messageReceiver = Executors.newFixedThreadPool(1);

	private final String broadcastIP;
	private final int localPort;
	private final int basePort;

	private final NetworkAddress selfAddress;

	private GossipAgent gossipAgent;

	private MessagePartAggregator messageAggregator;

	private volatile static NetworkAdapter network;

	private NetworkAdapter(final int basePort, final int localPort, final String broadcastIP)
			throws GossipException {
		this.basePort = basePort;
		this.localPort = localPort;
		this.broadcastIP = broadcastIP;
		try {
			this.selfAddress = new NetworkAddress(Inet4Address.getLocalHost().getHostAddress(), localPort);
		} catch (UnknownHostException e) {
			throw ErrorCodes.INTERNAL_ERROR.createException(e);
		}
		startUDPListening();
	}

	public static final NetworkAdapter getInstance(final int basePort, final int localPort, final String broadcastIP)
			throws GossipException {
		if (network == null) {
			synchronized (NetworkAdapter.class) {
				if (network == null) {
					network = new NetworkAdapter(basePort, localPort, broadcastIP);
					network.messageAggregator = MessagePartAggregator.initialize();
				}
			}
		}
		return network;
	}

	public final void registerForGossipMessage(final GossipAgent gossipAgent) {
		this.gossipAgent = gossipAgent;
	}

	public final NetworkAddress getSelfNetworkAddress() {
		return selfAddress;
	}

	public final void sendToDestination(final Message message, final NetworkAddress address)
			throws GossipException {
		send(serializeMessage(message), address);
	}

	public final void broadcastMessage(final GossipMessage message) throws GossipException {
		final byte[] messageBytes = serializeMessage(message);
		for (int i = 0; i < Constants.MAX_INSTANCES_PER_HOST; i++) {
			final NetworkAddress broadcastAddress = new NetworkAddress(broadcastIP, basePort + i);
			send(messageBytes, broadcastAddress);
		}
	}

	private void startUDPListening() throws GossipException {
		try {
			final DatagramSocket socket = new DatagramSocket(localPort);
			messageReceiver.submit(() -> {
				try {
					while (true) {
						final byte[] buffer = new byte[1480];
						final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						socket.receive(packet);
						final byte[] receivedData = packet.getData();
						receviedMessageExecutors.submit(() -> receive(receivedData));
					}
				} catch (IOException e) {
					throw ErrorCodes.GOSSIP_RECEIVING_FAILED.createException(e);
				} finally {
					socket.close();
				}
			});
		} catch (IOException e) {
			throw ErrorCodes.GOSSIP_RECEIVING_FAILED.createException(e);
		}
	}

	private void receive(final byte[] receivedData) {
		try {
			final NetworkPacket networkPacket = deserializePacket(receivedData);
			final Optional<byte[]> messageBytes = messageAggregator.getMessageBytesIfComplete(networkPacket);
			if (messageBytes.isPresent()) {
				processReceivedMessage(messageBytes.get());
			}
		} catch (GossipException | RuntimeException e) {
			logger.error("Error occured while processing the received message. Error message :" + e.getMessage());
			logger.error("Exception Trace", e);
		} 
	}

	private void processReceivedMessage(final byte[] receivedData) throws GossipException {
		final Message message = deserializeMessage(receivedData);
		if (message.getSourceAddress().equals(selfAddress)) {
			logger.debug("Received self broadcasted message. Ignoring!!");
			return;
		}
		logger.debug("Received the message. Processing!!");
		switch (message.getType()) {
		case GOSSIP:
			gossipAgent.processReceivedGossipMessage((GossipMessage) message);
			break;
		case INSTANCE_LIST_ENQUIRY_REQUEST:
			gossipAgent.processReceivedInstanceEnquireRequest((InstanceEnquiryRequest) message);
			break;
		case INSTANCE_LIST_ENQUIRY_RESPONSE:
			gossipAgent.processReceivedInstanceEnquireResponse((InstanceEnquiryResponse) message);
			break;
		}
	}

	private void send(final byte[] messageBytes, final NetworkAddress address)
			throws GossipException {
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.setBroadcast(true);
			for (NetworkPacket packetToSend : splitMessage(messageBytes)) {
				final byte[] packetBytes = serializePacket(packetToSend);
				final DatagramPacket packet = new DatagramPacket(packetBytes, packetBytes.length,
						InetAddress.getByName(address.getIp()), address.getPort());
				socket.send(packet);
			}
		} catch (IOException ioe) {
			throw ErrorCodes.GOSSIP_SENDING_FAILED.createException(ioe);
		}
		logger.debug("Message sent over UDP (connectionless) to " + address);
	}

	private byte[] serializeMessage(final Message message) throws GossipException {
		return serializeObject(message);
	}

	private byte[] serializePacket(final NetworkPacket packet) throws GossipException {
		return serializeObject(packet);
	}

	private Message deserializeMessage(final byte[] messageBytes) throws GossipException {
		return (Message) deserializeObject(messageBytes);
	}

	private NetworkPacket deserializePacket(final byte[] packetBytes) throws GossipException {
		return (NetworkPacket) deserializeObject(packetBytes);
	}

	private byte[] serializeObject(final Object object) throws GossipException {

		try (final ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
				final ObjectOutputStream out = new ObjectOutputStream(bos)) {
			out.writeObject(object);
			return bos.toByteArray();
		} catch (IOException ioe) {
			throw ErrorCodes.INTERNAL_ERROR.createException(ioe);
		}

	}

	private Object deserializeObject(final byte[] bytes) throws GossipException {
		try (final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				final ObjectInputStream in = new ObjectInputStream(bis)) {
			return in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw ErrorCodes.INTERNAL_ERROR.createException(e);
		}
	}
	
	private final NetworkPacket[] splitMessage(final byte[] messageBytes) {
		final int maxPacketSize = Constants.MAX_PACKET_SIZE;
		final int messageSize = messageBytes.length;
		final int packetsRequired = (int) ((messageSize % maxPacketSize == 0) ? (messageSize / maxPacketSize)
				: ((messageSize / maxPacketSize) + 1));

		final NetworkPacket[] packets = new NetworkPacket[packetsRequired];

		final String packetId = UUID.randomUUID().toString();
		for (int i = 1; i <= packetsRequired; i++) {
			byte[] dataPart;
			if (i == packetsRequired) {
				dataPart = new byte[messageSize - ((i-1) * maxPacketSize)];
			} else {
				dataPart = new byte[maxPacketSize];
			}
			System.arraycopy(messageBytes, (i-1) * maxPacketSize, dataPart, 0, dataPart.length);
			final NetworkPacket packet = new NetworkPacket(packetId, i, packetsRequired, dataPart);
			packets[i - 1] = packet;
		}
		return packets;
	}

}
