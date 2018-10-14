package com.monitoring.extension.sensor;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitoring.extension.domain.ResourceData;

public abstract class AbstractResourceSensor<T extends Serializable> implements ResourceSensor<T> {

	private Logger logger = LoggerFactory.getLogger(getClass());

	
	public final ResourceData<T> senseResourceData() {
		final ResourceData<T> data = senseResourceDataInt();
		logger.debug("Resource data collected!!");
		logger.debug(data.toString());
		return data;
	}
	
	protected abstract ResourceData<T> senseResourceDataInt();
}
