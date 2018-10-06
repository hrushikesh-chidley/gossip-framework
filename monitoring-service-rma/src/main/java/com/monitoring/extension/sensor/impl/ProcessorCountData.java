package com.monitoring.extension.sensor.impl;

public class ProcessorCountData extends AbstractResourceData<Long>{

	private static final long serialVersionUID = -6282201639683639612L;
	private long processorCount;
	
	public ProcessorCountData(final int count) {
		processorCount = count;
	}
	
	@Override
	public String getResourceName() {
		return "no-of-processors";
	}

	@Override
	public Long getCurrentValue() {
		return processorCount;
	}
}
