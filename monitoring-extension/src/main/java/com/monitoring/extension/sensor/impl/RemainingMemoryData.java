package com.monitoring.extension.sensor.impl;

public class RemainingMemoryData extends AbstractResourceData<String>{

	private static final long serialVersionUID = 6216327093587873378L;
	private long freeMemory;
	
	public RemainingMemoryData( final long freeMemory) {
		this.freeMemory = freeMemory;
	}

	@Override
	public String getResourceName() {
		return "free-memory(mb)";
	}

	@Override
	public String getCurrentValue() {
		return freeMemory+" [("+freeMemory/1000+" kb), ("+freeMemory/1000000+" mb)]";
	}
	
}
