package com.monitoring.extension.sensor;

import java.io.Serializable;

import com.monitoring.extension.domain.ResourceData;

public interface ResourceSensor<T extends Serializable> {
	
	/**
	 * Senses the resource data and returns it.
	 * <br>
	 * 
	 * Each implementation of this interface must be specific to a resource e.g free memory,
	 * number of threads, etc. 
	 * Collections of all such sensors shall provide the complete monitoring infor the 
	 * instance. 
	 * 
	 * @return the sensed resource data. Must be serializable to network
	 */
	ResourceData<T> senseResourceData();

}
