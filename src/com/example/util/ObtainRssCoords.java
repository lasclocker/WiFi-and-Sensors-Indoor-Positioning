package com.example.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class ObtainRssCoords {
	/*
	 * read all offline RSS data, and obtain current only one coordinate or K coordinates.  
	 */

	static float[] onLineCoords = {0, 0};
	static TreeMap<Float, float[]> minDistanceAndCoordinate = new TreeMap<Float, float[]>();

	public static void readOffLineRssData(String path, ArrayList<Map.Entry<float[], float[]>> rssAndCoords) throws IOException {
		/*
		 * read all RSS data from file, then store them to "rssAndCoords".
		 */
		String line = null;
		File file = new File(FileOperation.getAbsolutePath() + path);
		BufferedReader bufferR = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		while ((line = bufferR.readLine()) != null) {
			String[] rssDataAndPointCoords = line.split(" ");
			float[] rssData = MyArrayUtils.StrArrayToFloat(rssDataAndPointCoords[0].split(","));   // value
			float[] pointCoords = MyArrayUtils.StrArrayToFloat(rssDataAndPointCoords[1].split(",")); // key
			rssAndCoords.add(new AbstractMap.SimpleEntry<float[], float[]>(pointCoords, rssData));
		}
		bufferR.close();
	}
	

	public static float[] obtainCurCoordsOfRss(ArrayList<Map.Entry<float[], float[]>> rssAndCoords) {
		/*
		 * NN Algorithm. obtain the nearest coordinate which is close to the pedestrian.  
		 */
		float minNN = Float.MAX_VALUE;
		float[] onLineRss = MyArrayUtils.StrArrayToFloat(ObtainRssData.getOnLineRssAndCoordinateData().split(","));
		for(int i = 0; i < rssAndCoords.size(); i++) {
			float[] myCoords = rssAndCoords.get(i).getKey();
			float[] myRssData = rssAndCoords.get(i).getValue();
			float minSum = MyArrayUtils.getEuclideanDistance(onLineRss, myRssData);
			if (minSum < minNN) {minNN = minSum; onLineCoords = myCoords.clone();}
		}
		return onLineCoords;
	}

	public static TreeMap<Float, float[]> obtainKnumCurCoordsOfRss(ArrayList<Entry<float[], float[]>> rssAndCoords, int kNum) {
		/*
		 * KNN Algorithm. obtain the nearest K coordinates which is close to the pedestrian.  
		 */
		minDistanceAndCoordinate.clear();
		float[] onLineRss = MyArrayUtils.StrArrayToFloat(ObtainRssData.getOnLineRssAndCoordinateData().split(","));
		for(int i = 0; i < rssAndCoords.size(); i++) {
			float[] myCoords = rssAndCoords.get(i).getKey();
			float[] myRssData = rssAndCoords.get(i).getValue();
			float minDistance = MyArrayUtils.getEuclideanDistance(onLineRss, myRssData);
			minDistanceAndCoordinate.put(minDistance, myCoords.clone());
			if (minDistanceAndCoordinate.size() > kNum) minDistanceAndCoordinate.remove(minDistanceAndCoordinate.lastKey());
		}
		return minDistanceAndCoordinate;
	}
}
