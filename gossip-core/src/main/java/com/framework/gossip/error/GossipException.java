package com.framework.gossip.error;

public class GossipException extends Exception {

	private static final long serialVersionUID = 6963300019505182378L;

	private int code;
	
	public GossipException( final int code, final String message) {
		super(message);
		this.code = code;
	}
	
	public GossipException( final int code, final String message, final Exception cause) {
		super(message, cause);
		this.code = code;
	}
	
	public final int getCode() {
		return code;
	}
}
