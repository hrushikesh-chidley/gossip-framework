package com.monitoring.extension.sensor;

import java.io.Serializable;

import com.monitoring.extension.domain.ResourceData;

public abstract class AbstractResourceData<T extends Serializable> implements ResourceData<T>{

	private static final long serialVersionUID = 7934363172290519505L;

	public String toString() {
		return "{"+getResourceName()+": "+getCurrentValue()+"}";
	}
	
}
