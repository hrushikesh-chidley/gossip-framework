package com.gems.monitoring.util;

public class LoggerFactory {

	public static final Logger getLogger( final String loggerId ) {
		return new Logger(loggerId);
	}
	
}
