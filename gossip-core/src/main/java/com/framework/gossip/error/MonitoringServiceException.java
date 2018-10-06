package com.framework.gossip.error;

public class MonitoringServiceException extends Exception {

	private static final long serialVersionUID = 6963300019505182378L;

	private int code;
	
	public MonitoringServiceException( final int code, final String message) {
		super(message);
		this.code = code;
	}
	
	public MonitoringServiceException( final int code, final String message, final Exception cause) {
		super(message, cause);
		this.code = code;
	}
	
	public final int getCode() {
		return code;
	}
}
