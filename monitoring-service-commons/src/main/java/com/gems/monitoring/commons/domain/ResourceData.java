package com.gems.monitoring.commons.domain;

import java.io.Serializable;

public interface ResourceData<T extends Serializable> {

	String getResourceName();
	
	T getCurrentValue();
}
