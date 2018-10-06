package com.monitoring.extension.sensor;

import java.io.Serializable;

import com.monitoring.extension.domain.ResourceData;

public interface ResourceSensor<T extends Serializable> {
	
	ResourceData<T> senseResourceData();

}
