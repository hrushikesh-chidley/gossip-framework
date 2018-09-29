package com.gems.monitoring.rma.sensor.impl;

import com.gems.monitoring.domain.ResourceData;
import com.gems.monitoring.rma.sensor.ResourceSensor;

public class RemainingMemorySensor implements ResourceSensor<Long> {

	@Override
	public final ResourceData<Long> senseResourceData() {
		System.gc();
		final long freeMemory = Runtime.getRuntime().freeMemory();
		
		return new ResourceData<Long>() {

			@Override
			public String getResourceName() {
				return "free-memory";
			}

			@Override
			public Long getCurrentValue() {
				return freeMemory;
			}
		};
	}

}
