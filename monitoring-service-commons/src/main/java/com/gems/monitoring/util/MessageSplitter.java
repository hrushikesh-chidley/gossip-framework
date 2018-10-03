package com.gems.monitoring.util;

import java.util.UUID;

import com.gems.monitoring.net.NetworkPacket;

public class MessageSplitter {

	private static final int MAX_PACKET_SIZE = 1000;

	public static final NetworkPacket[] splitMessage(final byte[] messageBytes) {
		final int messageSize = messageBytes.length;
		final int packetsRequired = (int) ((messageSize % MAX_PACKET_SIZE == 0) ? (messageSize / MAX_PACKET_SIZE)
				: ((messageSize / MAX_PACKET_SIZE) + 1));

		final NetworkPacket[] packets = new NetworkPacket[packetsRequired];

		final String packetId = UUID.randomUUID().toString();
		for (int i = 1; i <= packetsRequired; i++) {
			byte[] dataPart;
			if (i == packetsRequired) {
				dataPart = new byte[messageSize - ((i-1) * MAX_PACKET_SIZE)];
			} else {
				dataPart = new byte[MAX_PACKET_SIZE];
			}
			System.arraycopy(messageBytes, (i-1) * MAX_PACKET_SIZE, dataPart, 0, dataPart.length);
			final NetworkPacket packet = new NetworkPacket(packetId, i, packetsRequired, dataPart);
			packets[i - 1] = packet;
		}
		return packets;
	}
}
