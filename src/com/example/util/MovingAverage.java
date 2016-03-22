package com.example.util;

import java.util.LinkedList;
import java.util.Queue;

public class MovingAverage {
	/*
	 * Algorithm implementation of moving average filter.
	 */
	private static float filterSum = 0;
	private static float filterResult = 0;
	private final static Queue<Float> maWindow = new LinkedList<Float>();

	public static float movingAverage(float accModule, int length) {
		
		filterSum += accModule;
		maWindow.add(accModule);
		if (maWindow.size() > length) {
			float head = maWindow.remove();
			filterSum -= head;
		}
		if (! maWindow.isEmpty()) {
			filterResult = filterSum / maWindow.size();
		}
		return filterResult;
	}
	
}
