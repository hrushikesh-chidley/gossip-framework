package com.gems.monitoring.rma.sensor.impl;

import java.io.Serializable;

import com.gems.monitoring.rma.domain.ResourceData;

public abstract class AbstractResourceData<T extends Serializable> implements ResourceData<T>{

	private static final long serialVersionUID = 7934363172290519505L;

	public String toString() {
		return "{"+getResourceName()+": "+getCurrentValue()+"}";
	}
	
}
