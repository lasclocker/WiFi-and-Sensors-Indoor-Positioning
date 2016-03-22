package com.example.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.activity.MainActivity;
import com.example.activity.RssActivity;

public class ObtainRssData {
	/*
	 * obtain RSS data and position coordinate, including offline and online.
	 */
	
	static WifiManager wifiMg = null;
	static List<ScanResult> list = null;
	static StringBuilder record_data = new StringBuilder();
	static List<String> routeSSIDSet = new ArrayList<String>();
	static int[] ssidSet = new int[3];
	static int acquireSignalNum = 0;
	static int acquireSignalTotalNum = 5;
	static long acquireTimeInterval_ms = 1000;
	static String ssid = "lab524_1,lab524_2,lab524_3";
	
	Timer timer;
	TimerTask task;
	MainActivity mainAty;
	
	public ObtainRssData(WifiManager wifiManager, MainActivity mainAty) {
		ObtainRssData.wifiMg = wifiManager;
		this.mainAty = mainAty;
	}
	
	public void obtainRssData(final ProgressBar progressBar, final ObtainRssData obtainRssData, final String fileStr) {
		/*
		 * obtain the RSS data and the position coordinate of offline.
		 */
		getEditableParameters();
		progressBar.setMax(acquireSignalTotalNum);
		progressBar.setVisibility(View.VISIBLE);
		
		if (timer == null) {
			timer = new Timer();
			task = new TimerTask() {
				
				@Override
				public void run() {
					getOffLineRssAndCoordinateData();
					acquireSignalNum ++;
					progressBar.setProgress(acquireSignalNum);
					if (acquireSignalNum >= acquireSignalTotalNum) {
						if (timer != null) {
							cancelRssTask();
							saveRssToFile(progressBar, obtainRssData, fileStr);
						}
					}
				}
			};
			timer.schedule(task, 0, acquireTimeInterval_ms);
		}
	}
	
	private void getEditableParameters() {
		/*
		 * get the newest editable configure parameters.
		 */
		routeSSIDSet = myStrToList(RssActivity.getSSID());
		acquireSignalTotalNum = RssActivity.getAcquireSignalTotalNum();
		acquireTimeInterval_ms = RssActivity.getAcquireTimeInterval_ms();
	}

	private static List<String> myStrToList(String ssid2) {
		/*
		 * convert String to List.
		 */
		return Arrays.asList(ssid2.split(","));
	}

	private void cancelRssTask() {
		/*
		 * cancel RSS task schedule.
		 */
		task.cancel();
		timer.cancel();
		task = null;
		timer = null;
	}
	
	private void saveRssToFile(final ProgressBar progressBar, ObtainRssData obtainRssData, String fileStr) {
		/*
		 * save RSS data and position coordinate to file.
		 */
		mainAty.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				progressBar.setVisibility(View.GONE);
				Toast.makeText(mainAty, "saving", Toast.LENGTH_SHORT).show();
			}
		});
		
		try {
			FileOperation.appendToFile(record_data.toString(), fileStr); 
			acquireSignalNum = 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void getCommonRssAndCoordinateData() {
		/*
		 * get RSS data from WiFi. this method is common to online and offline. 
		 */
		record_data.delete(0, record_data.length());
		wifiMg.startScan();
		list = wifiMg.getScanResults();
		if (list != null) {
			for (ScanResult scanResult:list) {
				if (routeSSIDSet.contains(scanResult.SSID)) {
					int ssidIndex = routeSSIDSet.indexOf(scanResult.SSID);
					ssidSet[ssidIndex] = scanResult.level;
				}
			}
		}
		for (int i = 0; i < ssidSet.length; i++) {
			record_data.append(routeSSIDSet.get(i) + "," + ssidSet[i] + ",");
		}
		record_data.deleteCharAt(record_data.length()-1);
	}
	
	private void getOffLineRssAndCoordinateData() {
		/*
		 * get RSS data from WiFi. this method is only used to offline. 
		 */
		getCommonRssAndCoordinateData();
		record_data.append(" ").append(IndoorMapArrayAdapter.getOriginalCurTouchCoords() + "\n");
	}
	
	public static String getOnLineRssAndCoordinateData() {
		/*
		 * get RSS data from WiFi. this method is only used to online. 
		 */
		routeSSIDSet = myStrToList(RssActivity.getSSID());
		getCommonRssAndCoordinateData();
		return record_data.toString();
	}
	
	public static int getAcquireSignalTotalNum() {
		/*
		 * get parameter: acquireSignalTotalNum.
		 */
		return acquireSignalTotalNum;
	}
	
	public static long getAcquireTimeInterval_ms() {
		/*
		 * get parameter: acquireTimeInterval_ms.
		 */
		return acquireTimeInterval_ms;
	}
	
	public static String getSSID() {
		/*
		 * get parameter: ssid.
		 */
		return ssid;
	}
}
