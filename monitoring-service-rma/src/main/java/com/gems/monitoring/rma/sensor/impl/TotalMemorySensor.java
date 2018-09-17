package com.gems.monitoring.rma.sensor.impl;

import com.gems.monitoring.commons.domain.ResourceData;
import com.gems.monitoring.rma.sensor.ResourceSensor;

public class TotalMemorySensor implements ResourceSensor<Long> {

	@Override
	public final ResourceData<Long> senseResourceData() {
		final long totalMemory = Runtime.getRuntime().totalMemory();
		
		return new ResourceData<Long>() {

			@Override
			public String getResourceName() {
				return "total-memory";
			}

			@Override
			public Long getCurrentValue() {
				return totalMemory;
			}
		};
	}

}
