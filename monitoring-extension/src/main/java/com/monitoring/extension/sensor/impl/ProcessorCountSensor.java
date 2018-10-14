package com.monitoring.extension.sensor.impl;

import com.monitoring.extension.domain.ResourceData;
import com.monitoring.extension.sensor.AbstractResourceSensor;

public class ProcessorCountSensor extends AbstractResourceSensor<Long> {

	@Override
	public final ResourceData<Long> senseResourceDataInt() {
		return new ProcessorCountData(Runtime.getRuntime().availableProcessors());
	}

}
