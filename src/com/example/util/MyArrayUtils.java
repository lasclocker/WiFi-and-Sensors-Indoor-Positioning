package com.example.util;

import org.apache.commons.lang3.math.NumberUtils;

public class MyArrayUtils {
	/*
	 * the common tools of custom defined.
	 */

	public static double[] StrArrayToDouble(String[] arr) {
		double[] nums = new double[arr.length];
		for (int i = 0; i < nums.length; i++) {
		    nums[i] = Double.parseDouble(arr[i]);
		}
		return nums;
	}
	
	public static float[] StrArrayToFloat(String[] arr) {
		int len = 0;
		for (int i = 0; i < arr.length; i++) {
			if (NumberUtils.isNumber(arr[i])) {
				len++;
			}
		}
		float[] nums = new float[len];
		for (int i = 0; i < nums.length; i++) {
			if (NumberUtils.isNumber(arr[i])) {
				nums[i] = Float.parseFloat(arr[i]);
			}
		}
		return nums;
	}
	
	public static int[] floatArrToInt(float[] arr) {
		int[] tmp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			tmp[i] = (int)arr[i];
		}
		return tmp;
	}
	
	
	public static float getEuclideanDistance(float[] arr1, float[] arr2) {
		float minSum = 0;
		for (int i = 0; i < arr1.length; i++) {
			minSum += Math.pow(arr1[i] - arr2[i], 2);
		}
		return minSum;
	}
}
