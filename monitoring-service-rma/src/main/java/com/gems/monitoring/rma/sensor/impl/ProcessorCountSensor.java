package com.gems.monitoring.rma.sensor.impl;

import com.gems.monitoring.domain.ResourceData;

public class ProcessorCountSensor extends AbstractResourceSensor<Long> {

	@Override
	public final ResourceData<Long> senseResourceDataInt() {
		return new ProcessorCountData(Runtime.getRuntime().availableProcessors());
	}

}
