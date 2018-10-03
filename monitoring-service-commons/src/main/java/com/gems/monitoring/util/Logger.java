package com.gems.monitoring.util;

public class Logger {

	private String id;

	public Logger(final String id) {
		this.id = id;
	}

	public final void debug(final String message) {

	}

	public final void info(final String message) {

	}

	public final void warn(final String message) {

	}

	public final void error(final String message) {

	}

	public final void error(final String message, final Throwable error) {

	}

	private void log(final LogLevel level, final String message) {
		System.out.println();
	}
	
	private enum LogLevel {
		DEBUG("DEBUG"),
		INFO("INFO"),
		WARN("WARNING"),
		ERROR("ERROR")
		
		;
		
		private String levelId;
		private LogLevel(final String levelId) {
			this.levelId = levelId;
		}
	}
}
