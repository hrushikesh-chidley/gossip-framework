package com.gems.monitoring.rma.sensor.impl;

import com.gems.monitoring.rma.domain.ResourceData;

public class TotalMemorySensor extends AbstractResourceSensor<String> {

	@Override
	public final ResourceData<String> senseResourceDataInt() {
		return new TotalMemoryData(Runtime.getRuntime().totalMemory());
	}

}
