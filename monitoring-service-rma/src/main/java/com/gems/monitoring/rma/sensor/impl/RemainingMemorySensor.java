package com.gems.monitoring.rma.sensor.impl;

import com.gems.monitoring.rma.domain.ResourceData;

public class RemainingMemorySensor extends AbstractResourceSensor<String> {

	@Override
	public final ResourceData<String> senseResourceDataInt() {
		System.gc();
		return new RemainingMemoryData(Runtime.getRuntime().freeMemory());
	}

}
