package com.gems.monitoring.rma.sensor.impl;

public class RemainingMemoryData extends AbstractResourceData<Long>{

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
	public Long getCurrentValue() {
		return freeMemory/1000000;
	}
	
}
