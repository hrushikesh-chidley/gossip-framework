package com.gems.monitoring.rma.sensor.impl;

import com.gems.monitoring.domain.ResourceData;

public class TotalMemorySensor extends AbstractResourceSensor<Long> {

	@Override
	public final ResourceData<Long> senseResourceDataInt() {
		return new TotalMemoryData(Runtime.getRuntime().totalMemory());
	}

}
