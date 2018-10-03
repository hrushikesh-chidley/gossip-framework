package com.gems.monitoring.rma.sensor.impl;

import com.gems.monitoring.domain.ResourceData;

public class RemainingMemorySensor extends AbstractResourceSensor<Long> {

	@Override
	public final ResourceData<Long> senseResourceDataInt() {
		System.gc();
		return new RemainingMemoryData(Runtime.getRuntime().freeMemory());
	}

}
