package com.gems.monitoring.rma.sensor.impl;

import com.gems.monitoring.domain.ResourceData;
import com.gems.monitoring.rma.sensor.ResourceSensor;

public class ProcessorCountSensor implements ResourceSensor<Long> {

	@Override
	public final ResourceData<Long> senseResourceData() {
		final long numOfProcessors = Runtime.getRuntime().availableProcessors();
		
		return new ResourceData<Long>() {

			private static final long serialVersionUID = -6282201639683639612L;

			@Override
			public String getResourceName() {
				return "no-of-processors";
			}

			@Override
			public Long getCurrentValue() {
				return numOfProcessors;
			}
		};
	}

}
