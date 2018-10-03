package com.gems.monitoring.rma.sensor.impl;

public class TotalMemoryData extends AbstractResourceData<Long>{

	private static final long serialVersionUID = -2267261076327722922L;
	private long totalMemory;
	
	public TotalMemoryData( final long totalMemory ) {
		this.totalMemory = totalMemory;
	}
	
	@Override
	public String getResourceName() {
		return "total-memory(mb)";
	}

	@Override
	public Long getCurrentValue() {
		return totalMemory/1000000;
	}
}
