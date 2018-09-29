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
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gems.monitoring.domain.GossipMessage;
import com.gems.monitoring.function.GossipAgent;

public class NetworkProxy {

	private ExecutorService executors = Executors.newCachedThreadPool();

	private String broadcastIP;
	private int localPort;
	private int basePort;

	private NetworkAddress sourceAddress;
	
	private GossipAgent gossipAgent;

	public NetworkProxy(final int port) {
		this.localPort = port;
		try {
			this.sourceAddress = new NetworkAddress(Inet4Address.getLocalHost().getHostAddress(), localPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		startUDPListening();
	}
	
	public void registerForGossipMessage( final GossipAgent gossipAgent ) {
		this.gossipAgent = gossipAgent;
	}
	
	public final NetworkAddress getSourceNetworkAddress() {
		return sourceAddress;
	}

	public void sendOverNetwork(final GossipMessage message, final Optional<NetworkAddress> destinationAddress) {
		try {
			if(destinationAddress.isPresent()) {
				sendToDestination(message, destinationAddress.get());
			} else {
				broadcastMessage(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
						executors.submit(() -> {
							processReceivedData(receivedData);
							return null;
						});
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

	private void processReceivedData( final byte[] receivedData ) throws ClassNotFoundException, IOException {
		final GossipMessage message = deserializeMessage(receivedData);
		gossipAgent.processReceivedGossipMessage(message);
	}
	
	private void broadcastMessage( final GossipMessage message ) throws IOException {
		final byte [] messageBytes = serializeMessage(message);
		for(int i = 0; i < 5; i++) {
			final NetworkAddress broadcastAddress = new NetworkAddress(broadcastIP, basePort+i);
			sendToDestination(messageBytes, broadcastAddress);
		}
	}

	private void sendToDestination(final GossipMessage message, final NetworkAddress address) throws IOException {
		sendToDestination(serializeMessage(message), address);
	}
	
	private void sendToDestination(final byte[] messageBytes, final NetworkAddress address) throws IOException {
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
	
	private GossipMessage deserializeMessage(final byte [] messageBytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = null;
		ObjectInputStream in = null;
		try {
			bis = new ByteArrayInputStream(messageBytes);
			in = new ObjectInputStream(bis);
			final Object message = in.readObject();
			return (GossipMessage)message;
		} finally {
			in.close();
			bis.close();
		}
	}

}
