package com.gems.monitoring.ga.gossipdata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gems.monitoring.commons.domain.InstanceId;

public class NetworkProxy {

	private int localPort;

	private final InstanceId instanceId;

	private ExecutorService executors = Executors.newCachedThreadPool();

	public NetworkProxy(final InstanceId instanceId, final int port) {
		this.instanceId = instanceId;
		this.localPort = port;
		startUDPListening();
	}

	private void startUDPListening() {
		try {
			final DatagramSocket socket = new DatagramSocket(localPort);
			new Thread(() -> {
				while (true) {
					try {
						final byte [] buffer = new byte[10240];
						final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						socket.receive(packet);
						final byte [] receivedData = packet.getData();
						executors.submit(() -> processReceivedData(receivedData));
					} catch(IOException e) {
						e.printStackTrace();
						socket.close();
						break;
					}
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private void processReceivedData( final byte[] receivedData ) {
		
	}

	public void sendOverNetwork(final Optional<NetworkAddress> destinationAddress,
			final Map<InstanceId, Integer> gossipMap, final Map<InstanceId, Map<InstanceId, Boolean>> suspectMatrix) {

		try {
			final NetworkAddress sourceAddress = new NetworkAddress(Inet4Address.getLocalHost().getHostAddress(), localPort);
			final GossipMessage message = new GossipMessage(instanceId, gossipMap, suspectMatrix, sourceAddress);
	
			final NetworkAddress address = destinationAddress.orElseGet(() -> getBroadcastAddress());
			sendToDestination(message, address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private NetworkAddress getBroadcastAddress() {
		return null;
	}

	private void sendToDestination(final GossipMessage message, final NetworkAddress address) throws IOException {
		final byte[] messageBytes = serializeMessage(message);
		
		System.out.println("Length -->" + messageBytes.length);
		
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
	}

	private byte[] serializeMessage(final GossipMessage message) throws IOException {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream out = null;
		try {
			bos = new ByteArrayOutputStream(1000);
			out = new ObjectOutputStream(bos);
			out.writeObject(message);
			return bos.toByteArray();
		} finally {
			out.close();
			bos.close();
		}
	}

}
