package com.gems.monitoring.rma.domain;

import java.io.Serializable;

public interface ResourceData<T extends Serializable> extends Serializable {

	String getResourceName();
	
	T getCurrentValue();
}
