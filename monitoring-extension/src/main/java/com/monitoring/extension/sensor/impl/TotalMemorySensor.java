package com.monitoring.extension.sensor.impl;

import com.monitoring.extension.domain.ResourceData;
import com.monitoring.extension.sensor.AbstractResourceSensor;

public class TotalMemorySensor extends AbstractResourceSensor<String> {

	@Override
	public final ResourceData<String> senseResourceDataInt() {
		return new TotalMemoryData(Runtime.getRuntime().totalMemory());
	}

}
