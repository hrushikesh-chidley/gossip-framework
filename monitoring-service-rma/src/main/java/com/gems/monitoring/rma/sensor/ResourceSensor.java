package com.gems.monitoring.rma.sensor;

import java.io.Serializable;

import com.gems.monitoring.domain.ResourceData;

public interface ResourceSensor<T extends Serializable> {
	
	ResourceData<T> senseResourceData();

}
