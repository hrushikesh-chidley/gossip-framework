package com.gems.monitoring.rma.sensor;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

@SuppressWarnings("rawtypes")
public class Sensors {
	
	private static Set<ResourceSensor<?>> sensors = new HashSet<>(20);
	
	static {
		Reflections reflection = new Reflections("com");
		final Set<Class<? extends ResourceSensor>> sensorClasses = reflection.getSubTypesOf(ResourceSensor.class);
		
		sensorClasses.forEach(sensorClass -> { 
		try {
			sensors.add(sensorClass.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
		}});
	}
	
	public static final Set<ResourceSensor<? extends Serializable>> getAllSensors() {
		return Collections.unmodifiableSet(sensors);
	}

}
