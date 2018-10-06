package com.framework.gossip.network;

import java.io.Serializable;
import java.util.Arrays;

public final class NetworkPacket implements Serializable, Comparable<NetworkPacket> {

	private static final long serialVersionUID = -3580292436961733732L;

	private String id;
	private int seqNum;
	private int maximumSeqNum;
	private byte[] data;

	public NetworkPacket(final String id, final int seqNum, final int maximumSeqNum, final byte[] data) {
		this.id = id;
		this.seqNum = seqNum;
		this.maximumSeqNum = maximumSeqNum;
		this.data = Arrays.copyOf(data, data.length);
	}

	public final String getId() {
		return id;
	}

	public final int getSeqNum() {
		return seqNum;
	}

	public final int getMaximumSeqNum() {
		return maximumSeqNum;
	}

	public final byte[] getData() {
		return Arrays.copyOf(data, data.length);
	}

	@Override
	public int compareTo( final NetworkPacket other) {
		return this.seqNum - other.seqNum;
	}

}
