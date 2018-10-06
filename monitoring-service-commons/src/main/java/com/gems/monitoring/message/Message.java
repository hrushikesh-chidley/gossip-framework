package com.gems.monitoring.message;

import java.io.Serializable;

import com.gems.monitoring.domain.NetworkAddress;

public abstract class Message implements Serializable {

	private static final long serialVersionUID = -1340939030589571562L;

	private final MessageTypes type;
	private final NetworkAddress sourceAddress;
	
	
	public Message( final MessageTypes type, final NetworkAddress sourceAddress ) {
		this.type = type;
		this.sourceAddress = sourceAddress;
	}
	
	public final MessageTypes getType() {
		return type;
	}
	
	public final NetworkAddress getSourceAddress() {
		return sourceAddress;
	}
	
}
