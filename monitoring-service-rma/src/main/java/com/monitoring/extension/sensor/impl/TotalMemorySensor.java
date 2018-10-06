package com.monitoring.extension.sensor.impl;

import com.monitoring.extension.domain.ResourceData;

public class TotalMemorySensor extends AbstractResourceSensor<String> {

	@Override
	public final ResourceData<String> senseResourceDataInt() {
		return new TotalMemoryData(Runtime.getRuntime().totalMemory());
	}

}
