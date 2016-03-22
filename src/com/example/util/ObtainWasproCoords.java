package com.example.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class ObtainWasproCoords {
	/*
	 * Waspro fusion Algorithm implementation.
	 */
	
	static int kNum = 8;
	static boolean firstLocation = true;
	static float[] tmpCoords = new float[2];
	static float[] lastTmpCoords = new float[2];
	static float[] fusionCoords = new float[2];
	static float[] lastFusionCoords = new float[2];
	static TreeMap<Float, float[]> tmpMap = new TreeMap<Float, float[]>();
	
	public static float[] getCurCoordsOfWaspro(ArrayList<Map.Entry<float[], float[]>> rssAndCoords) {
		/*
		 * Waspro fusion Algorithm:
		 * 1. the first location: only use WiFi RSS to obtain the coordinate of the pedestrian.
		 * 2. if not the first location, firstly use step information to obtain the next moment coordinate of the pedestrian,
		 *    we mark the coordinate as A.
		 *    secondly use WiFi RSS to obtain the nearest K coordinates, we mark the K coordinates as B.
		 *    thirdly select the coordinate from B which is nearest to A, we call the coordinate as C.
		 *    finally return C.
		 */
		if (firstLocation) {
			tmpCoords = ObtainRssCoords.obtainCurCoordsOfRss(rssAndCoords);
			ObtainStepData.setCurCoordsOfStep(tmpCoords);
			lastTmpCoords = tmpCoords.clone();
			fusionCoords = tmpCoords.clone();
			lastFusionCoords = tmpCoords.clone();
			firstLocation = false;
		} else {
			tmpCoords = ObtainStepData.getCurCoordsOfStep();
			if (!Arrays.equals(tmpCoords, lastTmpCoords)) {
				tmpMap = ObtainRssCoords.obtainKnumCurCoordsOfRss(rssAndCoords, kNum);
				fusionCoords = getFusionCoordinate(tmpCoords, tmpMap).clone();
				if (!Arrays.equals(fusionCoords, lastFusionCoords)) {
					ObtainStepData.setCurCoordsOfStep(fusionCoords);
					lastTmpCoords = fusionCoords.clone();
				} else {
					ObtainStepData.setCurCoordsOfStep(tmpCoords);
					lastTmpCoords = tmpCoords.clone();
				}
				lastFusionCoords = fusionCoords.clone();
			}
		}
		return fusionCoords;
	}
	
	private static float[] getFusionCoordinate(float[] tmpCoords2, TreeMap<Float, float[]> tmpMap2) {
		/*
		 *  select the coordinate from B which is nearest to A. 
		 *  see the method "getCurCoordsOfWaspro" to know what is the meaning of A and B.
		 */
		float[] res = new float[2];
		Float min = Float.MAX_VALUE;
		for (float[] coords : tmpMap2.values()) {
			float tmp = MyArrayUtils.getEuclideanDistance(tmpCoords2, coords);
			if (tmp < min) {min = tmp; res = coords.clone();}
		}
		return res;
	}

	public static void correctWaspro() {
		/*
		 * to ensure it is the first location.
		 */
		firstLocation = true;
	}
}
