package com.framework.gossip.error;

import java.text.MessageFormat;

public enum ErrorCodes {
	
	INTERNAL_ERROR(1, "Internal Error Occured"),
	
	GOSSIP_SENDING_FAILED(21, "Error while sending the Gossip Message out!"),
	GOSSIP_RECEIVING_FAILED(22, "Error while receiving the Gossip Message"),
	
	;
	
	private int code;
	private String message;
	
	private ErrorCodes(final int code, final String message) {
		this.code = code;
		this.message = message;
	}
	
	public final GossipException createException(final Object ... params) {
		return new GossipException(code, MessageFormat.format(message, params));
	}

	public final GossipException createException( final Exception cause, final Object ... params) {
		return new GossipException(code, MessageFormat.format(message, params), cause);
	}
}
