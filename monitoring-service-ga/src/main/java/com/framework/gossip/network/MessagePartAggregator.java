package com.framework.gossip.network;

import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class MessagePartAggregator {

	private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

	private Map<String, Queue<NetworkPacket>> receivedPackets = new ConcurrentHashMap<>();

	private Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

	private Map<String, Long> timeStamps = new ConcurrentHashMap<>();

	private static MessagePartAggregator messageCombiner;

	private MessagePartAggregator() {
		super();
		scheduledExecutor.scheduleWithFixedDelay(() -> scheduledCleanup(), 10, 10, TimeUnit.SECONDS);
	}

	public static final MessagePartAggregator initialize() {
		if (messageCombiner == null) {
			messageCombiner = new MessagePartAggregator();
		}
		return messageCombiner;
	}

	public final Optional<byte[]> getMessageBytesIfComplete(final NetworkPacket packet) {
		final String packetId = packet.getId();

		final ReentrantLock lock = acquireLock(packetId);
		lock.lock();
		try {
			Queue<NetworkPacket> packets = receivedPackets.remove(packetId);
			if (packets == null) {
				packets = new PriorityQueue<>();
			}
			packets.add(packet);

			if (packets.size() != packet.getMaximumSeqNum()) {
				receivedPackets.put(packetId, packets);
				timeStamps.put(packetId, System.currentTimeMillis());
				return Optional.empty();
			} else {
				byte[] message = new byte[0];
				NetworkPacket sequencedPacket = packets.poll();
				while (sequencedPacket != null) {
					message = combineByteArrays(message, sequencedPacket.getData());
					sequencedPacket = packets.poll();
				}
				timeStamps.remove(packetId);
				locks.remove(packetId);
				return Optional.of(message);
			}
		} finally {
			lock.unlock();
		}
	}

	private ReentrantLock acquireLock(final String packetId) {
		ReentrantLock lock = locks.get(packetId);
		if (lock == null) {
			synchronized (this) {
				lock = locks.get(packetId);
				if (lock == null) {
					lock = new ReentrantLock();
					locks.put(packetId, lock);
				}
			}
		}
		return lock;
	}

	private byte[] combineByteArrays(final byte[] first, final byte[] second) {
		final int firstLength = first.length;
		final int secondLength = second.length;
		final byte[] combined = new byte[firstLength + secondLength];
		System.arraycopy(first, 0, combined, 0, firstLength);
		System.arraycopy(second, 0, combined, firstLength, secondLength);
		return combined;
	}

	private void scheduledCleanup() {
		try {
			final long currentTime = System.currentTimeMillis();
			timeStamps.keySet().parallelStream().filter(packetId -> ((currentTime - timeStamps.get(packetId)) > 10000))
					.forEach(packetId -> {
						final ReentrantLock lock = acquireLock(packetId);
						lock.lock();
						receivedPackets.remove(packetId);
						timeStamps.remove(packetId);
						lock.unlock();
						locks.remove(packetId);
					});
		} catch (Exception e) {
			// do not throw any exception
		}
	}

}
