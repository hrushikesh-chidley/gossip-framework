package com.gems.monitoring.net;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gems.monitoring.domain.GossipMessage;
import com.gems.monitoring.domain.InstanceEnquiryRequest;
import com.gems.monitoring.domain.InstanceEnquiryResponse;
import com.gems.monitoring.domain.Message;
import com.gems.monitoring.error.ErrorCodes;
import com.gems.monitoring.error.MonitoringServiceException;
import com.gems.monitoring.function.GossipAgent;

public final class NetworkProxy {

	private final ExecutorService receviedMessageExecutors = Executors.newCachedThreadPool();
	private final ExecutorService messageReceiver = Executors.newFixedThreadPool(1);

	
	private final String broadcastIP;
	private final int localPort;
	private final int basePort;

	private final NetworkAddress sourceAddress;

	private GossipAgent gossipAgent;

	private volatile static NetworkProxy network;

	private NetworkProxy(final int basePort, final int localPort, final String broadcastIP)
			throws MonitoringServiceException {
		this.basePort = basePort;
		this.localPort = localPort;
		this.broadcastIP = broadcastIP;
		try {
			this.sourceAddress = new NetworkAddress(Inet4Address.getLocalHost().getHostAddress(), localPort);
		} catch (UnknownHostException e) {
			throw ErrorCodes.INTERNAL_ERROR.createException(e);
		}
		startUDPListening();
	}

	public static final NetworkProxy getInstance(final int basePort, final int localPort, final String broadcastIP)
			throws MonitoringServiceException {
		if (network == null) {
			synchronized (NetworkProxy.class) {
				if (network == null) {
					network = new NetworkProxy(basePort, localPort, broadcastIP);
				}
			}
		}
		return network;
	}

	public final void registerForGossipMessage(final GossipAgent gossipAgent) {
		this.gossipAgent = gossipAgent;
	}

	public final NetworkAddress getSelfNetworkAddress() {
		return sourceAddress;
	}
	
	public final void sendToDestination(final Message message, final NetworkAddress address)
			throws MonitoringServiceException {
		sendToDestination(serializeMessage(message), address);
	}

	public final void broadcastMessage(final GossipMessage message) throws MonitoringServiceException {
		final byte[] messageBytes = serializeMessage(message);
		for (int i = 0; i < 5; i++) {
			final NetworkAddress broadcastAddress = new NetworkAddress(broadcastIP, basePort + i);
			sendToDestination(messageBytes, broadcastAddress);
		}
	}

	private void startUDPListening() throws MonitoringServiceException {
		try {
			final DatagramSocket socket = new DatagramSocket(localPort);
			messageReceiver.submit(() -> {
				while (true) {
					try {
						final byte[] buffer = new byte[10240];
						final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						socket.receive(packet);
						final byte[] receivedData = packet.getData();
						receviedMessageExecutors.submit(() -> {
							processReceivedData(receivedData);
							return null;
						});
					} catch (IOException e) {
						throw ErrorCodes.GOSSIP_RECEIVING_FAILED.createException(e);
					} finally {
						socket.close();
					}
				}
			});
		} catch (IOException e) {
			throw ErrorCodes.GOSSIP_RECEIVING_FAILED.createException(e);
		}
	}

	private void processReceivedData(final byte[] receivedData) throws MonitoringServiceException {
		final Message message = deserializeMessage(receivedData);
		switch(message.getType()) {
		case GOSSIP :
			gossipAgent.processReceivedGossipMessage((GossipMessage)message);
		case INSTANCE_LIST_ENQUIRY_REQUEST : 
			gossipAgent.processReceivedInstanceEnquireRequest((InstanceEnquiryRequest)message);
		case INSTANCE_LIST_ENQUIRY_RESPONSE :
			gossipAgent.processReceivedInstanceEnquireResponse((InstanceEnquiryResponse)message);
		}
	}

	private void sendToDestination(final byte[] messageBytes, final NetworkAddress address)
			throws MonitoringServiceException {
		try {
			final DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length,
					InetAddress.getByName(address.getIp()), address.getPort());

			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
				socket.setBroadcast(true);
				socket.send(packet);
			} finally {
				socket.close();
			}
		} catch (IOException ioe) {
			throw ErrorCodes.GOSSIP_SENDING_FAILED.createException(ioe);
		}
	}

	private byte[] serializeMessage(final Message message) throws MonitoringServiceException {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream out = null;
		try {
			try {
				bos = new ByteArrayOutputStream(1000);
				out = new ObjectOutputStream(bos);
				out.writeObject(message);
				return bos.toByteArray();
			} finally {
				out.close();
				bos.close();
			}
		} catch (IOException ioe) {
			throw ErrorCodes.INTERNAL_ERROR.createException(ioe);
		}

	}

	private Message deserializeMessage(final byte[] messageBytes) throws MonitoringServiceException {
		ByteArrayInputStream bis = null;
		ObjectInputStream in = null;
		try {
			try {
				bis = new ByteArrayInputStream(messageBytes);
				in = new ObjectInputStream(bis);
				final Object message = in.readObject();
				return (Message) message;
			} finally {
				in.close();
				bis.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			throw ErrorCodes.INTERNAL_ERROR.createException(e);
		}
	}

}
