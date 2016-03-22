package com.example.activity;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.activity.R;
import com.example.util.ButtonImageArrayAdapter;
import com.example.util.ObtainStepData;
import com.example.util.ObtainRssCoords;
import com.example.util.ObtainWasproCoords;
import com.example.util.ObtainRssData;
import com.example.util.FileOperation;
import com.example.util.SocketClient;
import com.example.util.IndoorMapArrayAdapter;

public class MainActivity extends ListActivity implements OnClickListener {
	/*
	 *  main activity of the APP
	 */
	
// Step variables
	SensorManager Sm;
	Sensor Accelerometer;
	SensorEventListener sListener;
	ObtainStepData obtainStepData;
	EditText editText_accThreshold;
	TextView textView_showRecordAccTime, textView_countStep, textView_stepLength, 
			 textView_degree, textView_coordinate;
	float[] accValues      = new float[3];
	long lastClickTime     = 0;
	String accFile         = "/1Acc/acc.txt";
	String accMaFile       = "/1Acc/accMa.txt";
	StringBuilder sbAcc    = new StringBuilder();
	DecimalFormat decimalF = new DecimalFormat("#.0");
	boolean isRecord = false, isRecordAcc = false;
	int maLength     = 5, stepState       = 0, stepCount    = 0;
	long lastUpdate  = 0, zeroTime        = 0, accNum       = 0;
	float accModule  = 0, maResult        = 0, accThreshold = 0.65f, maMin = 0;
	

// RSS variables
	WifiManager wifiMg;
	ProgressBar myProBar;
	ObtainRssData obtainRssData;
	int acquireSignalTotalNum     = 20;
	long acquireTimeInterval_ms   = 1000;
	Timer timerShow               = null;
	TimerTask taskShow            = null;
	OnTouchListener touchSelectRp = null;
	static float[] touchCoords    = {150, 1480};
	String rssFile                = "/1Rss/rss.txt";
	static ArrayList<Map.Entry<float[], float[]>> rssAndCoords = new ArrayList<Map.Entry<float[], float[]>>();
	
	
// Time variables


	final long updateItemMilliTime              = 40;
	final long backSpaceTimeIntervalMilliSecond = 1000;
	
	
// button image and color
	
	final int myBlack = android.R.color.black;
	final int myGreen = R.color.my_green;
	
	final int wifiBlackDrawable = R.drawable.wifi_50_black;
	final int footprintsBlackDrawable = R.drawable.footprints_50_black;
	final int configurationBlackDrawable = R.drawable.configuration_50_black;
	final int showBlackDrawable = R.drawable.location_50_black;
	
	final int wifiGreenDrawable = R.drawable.wifi_50_mygreen;
	final int footprintsGreenDrawable = R.drawable.footprints_50_mygreen;
	final int configurationGreenDrawable = R.drawable.configuration_50_mygreen;
	final int showGreenDrawable = R.drawable.location_50_mygreen;
	
// listview text and image
	
	final static String[] wifiKey = {"RssRpSelect", "RssObtainAndSave", "RssOpenFile", "RssClearFile"};
	final static int[] wifiValue  = {R.drawable.select_rp_blue, R.drawable.document_save_color, 
			                         R.drawable.key_color, R.drawable.document_clear_color};
	
	final static String[] stepKey = {"StepObtain", "StepRecord", "StepStop", "StepSave", "StepCorrect", 
		                             "StepOpenFile"};
	final static int[] stepValue  = {R.drawable.plus_color, R.drawable.clock_color, R.drawable.cancel_color, 
			                         R.drawable.document_color, R.drawable.correct, R.drawable.key_color};
	
	final static String[] confKey = {"RssInfo", "StepInfo", "SocketInfo"};
	final static int[] confValue  = {R.drawable.edit_color_wifi_4, R.drawable.edit_color_step_4, 
					                 R.drawable.edit_color_network_5};
	
	final static String[] showKey = {"WiFiPositioningShow", "StepPositioningShow", "WaSproShow"};
	final static int[] showValue  = {R.drawable.show_edit_color_custom_wifi_2, R.drawable.show_edit_color_custom_step_2, 
			                         R.drawable.show_edit_color_custom_was_2};
	
	static final List<Map.Entry<String, Integer>> wifiListAdapter = createAdapterList(wifiKey, wifiValue);
	static final List<Map.Entry<String, Integer>> stepListAdapter = createAdapterList(stepKey, stepValue);
	static final List<Map.Entry<String, Integer>> confListAdapter = createAdapterList(confKey, confValue);
	static final List<Map.Entry<String, Integer>> showListAdapter = createAdapterList(showKey, showValue);
	
	Button but_wiFiPositioning, but_stepPositioning, but_positioningConfigure, but_positioningShow;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		but_wiFiPositioning = (Button) findViewById(R.id.but_wiFiPositioning);
		but_stepPositioning = (Button) findViewById(R.id.but_stepPositioning);
		but_positioningConfigure = (Button) findViewById(R.id.but_positioningConfigure);
		but_positioningShow = (Button) findViewById(R.id.but_positioningShow);
		
		but_wiFiPositioning.setOnClickListener(this);
		but_stepPositioning.setOnClickListener(this);
		but_positioningConfigure.setOnClickListener(this);
		but_positioningShow.setOnClickListener(this);
	
		// custom define ButtonImageArrayAdapter
		setListAdapter(new ButtonImageArrayAdapter(this, wifiListAdapter)); 
		
// Step
		textView_showRecordAccTime = (TextView) findViewById(R.id.textView_showRecordAccTime);
		textView_countStep         = (TextView) findViewById(R.id.textView_countStep);
		textView_stepLength        = (TextView) findViewById(R.id.textView_stepLength);
		textView_degree        = (TextView) findViewById(R.id.textView_degree);
		textView_coordinate        = (TextView) findViewById(R.id.textView_coordinate);
		
		obtainStepData = new ObtainStepData(MainActivity.this, 
				textView_countStep, textView_showRecordAccTime,
				textView_stepLength, textView_degree, textView_coordinate);
		
// RSS
		wifiMg = (WifiManager)getSystemService(WIFI_SERVICE);
		obtainRssData = new ObtainRssData(wifiMg, MainActivity.this);
		myProBar = (ProgressBar) findViewById(R.id.progressBar_obtainRss);
		
		try {
			// read all offline RSS data
			ObtainRssCoords.readOffLineRssData(rssFile, rssAndCoords);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		// set screen keep on always
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	private static List<Map.Entry<String, Integer>> createAdapterList(String[] key, int[] value) {
		/*
		 * create map, key: listview text, value: listview image.
		 */
		List<Map.Entry<String, Integer>> resList = new ArrayList<Map.Entry<String,Integer>>();
		for (int i = 0; i < key.length; i++) {
			resList.add(new AbstractMap.SimpleEntry<String, Integer>(key[i], value[i]));
		}
		return Collections.unmodifiableList(resList);
	}

	@Override
	public void onClick(View v) {
		/*
		 * listen when button is clicked.
		 */
		switch (v.getId()) {
			case R.id.but_wiFiPositioning:
				cancelListViewTouchListener();
				initButtonWiFiPositioning();
				break;
			case R.id.but_stepPositioning:
				cancelListViewTouchListener(); 
				initButtonStepPositioning();
				break;
			case R.id.but_positioningConfigure:
				cancelListViewTouchListener(); 
				initButtonPositioningConfigure();
				break;
			case R.id.but_positioningShow:
				cancelListViewTouchListener(); 
				initButtonPositioningShow();
				break;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		/*
		 * listen when list item is clicked.
		 */
		if (getListAdapter().getItem(position) instanceof String) {
			// when the item is instance of String class, return empty.
			return;
		}
		@SuppressWarnings("unchecked")
		// convert the item to SimpleEntry
		SimpleEntry<String, Integer> data = (AbstractMap.SimpleEntry<String, Integer>) getListAdapter().getItem(position);
		
		switch (data.getKey()) {
		case "RssInfo":
			Intent intent_AtyRssConf = new Intent(this, RssActivity.class);
			startActivity(intent_AtyRssConf);
			break;
		case "StepInfo":
			Intent intent_AtyStepConf = new Intent(this, StepActivity.class);
			startActivity(intent_AtyStepConf);
			break;
		case "SocketInfo":
			Intent intent_AtySocketConf = new Intent(this, SocketActivity.class);
			startActivity(intent_AtySocketConf);
			break;
		case "RssRpSelect":
			if (touchSelectRp == null) {
				touchSelectRp = new OnTouchListener() {
					@SuppressLint("ClickableViewAccessibility")
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						touchCoords[0] = (int)event.getX();
						touchCoords[1] = (int)event.getY();
						return false;
					}
				};
			}
			getListView().setOnTouchListener(touchSelectRp);
			positioningShow_button("SampleFloorPlan");
			rpSelectTaskSchedule(updateItemMilliTime);
			break;
		case "StepObtain":
			obtainStepData.obtainStepSetting();
			obtainStepData.obtainStep();
			break;
		case "RssObtainAndSave":
			openWifi();
			obtainRssData.obtainRssData(myProBar, obtainRssData, rssFile);
			break;
		case "StepRecord":
			obtainStepData.recordStep();
			break;
		case "StepStop":
			obtainStepData.stopStep();
			break;
		case "StepSave":
			try {
				FileOperation.saveToFile(obtainStepData.getSbAcc(), accFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				FileOperation.saveToFile(obtainStepData.getSbAccMA(), accMaFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "StepCorrect":
			obtainStepData.correctStep();
			obtainStepData.stepViewGone();
			break;
		case "StepOpenFile":
			FileOperation.openFile(MainActivity.this, accFile);
			break;
		case "RssOpenFile":
			FileOperation.openFile(MainActivity.this, rssFile);
			break;
		case "RssClearFile":
			try {
				FileOperation.clearFile(rssFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			displayToast("clear ok", getApplicationContext());
			break;
		case "WiFiPositioningShow":
			openWifi();
			positioningShow_button("WiFiFloorPlan");
			wifiShowTaskSchedule(updateItemMilliTime);
			break;
		case "StepPositioningShow":
			positioningShow_button("StepFloorPlan");
			stepShowTaskSchedule(updateItemMilliTime);
			break;
		case "WaSproShow":
			openWifi();
			ObtainWasproCoords.correctWaspro();
			positioningShow_button("WaSproShow");
			wasproShowTaskSchedule(updateItemMilliTime);
			break;
		}
	}
	
	private void cancelListViewTouchListener() {
		/*
		 * cancel selectRP touch listener. 
		 */
		if (touchSelectRp != null) {
			getListView().setOnTouchListener(null);
			touchSelectRp = null;
		}
	}
	
	public void wasproShowTaskSchedule(long milliTime) {
		/*
		 * WaSproShow positioning task schedule.
		 */
		timerShow = new Timer();
		taskShow = new TimerTask() {
			
			@Override
			public void run() {
				touchCoords = ObtainWasproCoords.getCurCoordsOfWaspro(rssAndCoords);
				updateItemAtPosition(0, getListView());
			}
		};
		timerShow.schedule(taskShow, 0, milliTime);
	}
	
	public void wifiShowTaskSchedule(long milliTime) {
		/*
		 * WiFiPositioningShow task schedule.
		 */
		timerShow = new Timer();
		taskShow = new TimerTask() {
			
			@Override
			public void run() {
				touchCoords = ObtainRssCoords.obtainCurCoordsOfRss(rssAndCoords);
				updateItemAtPosition(0, getListView());
			}
		};
		timerShow.schedule(taskShow, 0, milliTime);
	}
	
	public void stepShowTaskSchedule(long milliTime) {
		/*
		 * StepPositioningShow task schedule.
		 */
		timerShow = new Timer();
		taskShow = new TimerTask() {
			
			@Override
			public void run() {
				touchCoords = ObtainStepData.getCurCoordsOfStep();
				updateItemAtPosition(0, getListView());
			}
		};
		timerShow.schedule(taskShow, 0, milliTime);
	}
	
	
	public void rpSelectTaskSchedule(long milliTime) {
		/*
		 * RssRpSelect task schedule.
		 */
		timerShow = new Timer();
		taskShow = new TimerTask() {
			
			@Override
			public void run() {
				updateItemAtPosition(0, getListView());
			}
		};
		timerShow.schedule(taskShow, 0, milliTime);
	}
	
	public void positioningShow_button(String name) {
		/*
		 * when indoor map is shown, GUI is shown as followings.
		 */
		but_wiFiPositioning.setTextColor(getResources().getColor(android.R.color.black));
		but_stepPositioning.setTextColor(getResources().getColor(android.R.color.black));
		but_positioningConfigure.setTextColor(getResources().getColor(android.R.color.black));
		but_positioningShow.setTextColor(getResources().getColor(android.R.color.black));
		
		but_wiFiPositioning.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.wifi_50_black, 0, 0);
		but_stepPositioning.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.footprints_50_black, 0, 0);
		but_positioningConfigure.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.configuration_50_black, 0, 0);
		but_positioningShow.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.location_50_black, 0, 0);
		
		setListAdapter(new IndoorMapArrayAdapter(this, new String[]{name}));
		
	}
	
	public static float[] getCoords() {
		/*
		 * get current coordinate.
		 */
		return touchCoords;
	}
	
	
	public void checkAndCancelTask(Timer tmShow, TimerTask tsShow) {
		/*
		 * cancel Timer and TimerTask.
		 */
		if(tmShow != null && tsShow != null) {
			tmShow.cancel();
			tsShow.cancel();
			tmShow = null;
			tsShow = null;
		}
	}
	
	private void updateItemAtPosition(final int position, final ListView mListView) {
		/*
		 * force update the getView method in ButtonImageArrayAdapter.java.
		 */
	    int visiblePosition = mListView.getFirstVisiblePosition();
	    final View view = mListView.getChildAt(position - visiblePosition);
	    runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mListView.getAdapter().getView(position, view, mListView);
			}
		});
	}
	
	
	private void initButtonPositioningShow() {
		/*
		 * initial GUI of Show button.
		 */
		checkAndCancelTask(timerShow, taskShow);
		setButtonColor(myBlack, myBlack, myBlack, myGreen);
		setButtonDrawable(wifiBlackDrawable, footprintsBlackDrawable, configurationBlackDrawable, showGreenDrawable);
		
		setListAdapter(new ButtonImageArrayAdapter(this, showListAdapter));
	}
	
	private void initButtonPositioningConfigure() {
		/*
		 * initial GUI of Configure button.
		 */
		checkAndCancelTask(timerShow, taskShow);
		setButtonColor(myBlack, myBlack, myGreen, myBlack);
		setButtonDrawable(wifiBlackDrawable, footprintsBlackDrawable, configurationGreenDrawable, showBlackDrawable);
		
		setListAdapter(new ButtonImageArrayAdapter(this, confListAdapter));
	}
	private void initButtonStepPositioning() {
		/*
		 * initial GUI of Step button.
		 */
		checkAndCancelTask(timerShow, taskShow);
		setButtonColor(myBlack, myGreen, myBlack, myBlack);
		setButtonDrawable(wifiBlackDrawable, footprintsGreenDrawable, configurationBlackDrawable, showBlackDrawable);
		
		setListAdapter(new ButtonImageArrayAdapter(this, stepListAdapter));
	}
	private void initButtonWiFiPositioning() {
		/*
		 * initial GUI of WiFi button.
		 */
		checkAndCancelTask(timerShow, taskShow);
		setButtonColor(myGreen, myBlack, myBlack, myBlack);
		setButtonDrawable(wifiGreenDrawable, footprintsBlackDrawable, configurationBlackDrawable, showBlackDrawable);
		
		setListAdapter(new ButtonImageArrayAdapter(this, wifiListAdapter));
	}
	
	private void setButtonDrawable(int draw1, int draw2, int draw3, int draw4) {
		/*
		 * set the image of button.
		 */
		but_wiFiPositioning.setCompoundDrawablesWithIntrinsicBounds(0, draw1, 0, 0);
		but_stepPositioning.setCompoundDrawablesWithIntrinsicBounds(0, draw2, 0, 0);
		but_positioningConfigure.setCompoundDrawablesWithIntrinsicBounds(0, draw3, 0, 0);
		but_positioningShow.setCompoundDrawablesWithIntrinsicBounds(0, draw4, 0, 0);
	}
	
	private void setButtonColor(int color1, int color2, int color3, int color4) {
		/*
		 * set the color of button.
		 */
		but_wiFiPositioning.setTextColor(getResources().getColor(color1));
		but_stepPositioning.setTextColor(getResources().getColor(color2));
		but_positioningConfigure.setTextColor(getResources().getColor(color3));
		but_positioningShow.setTextColor(getResources().getColor(color4));
	}
	
	
	@Override
	public void onBackPressed() {
		/*
		 * Backspace: when press back button, call this method.
		 */
		if (lastClickTime <= 0) {
			Toast.makeText(this, "再按一次后退键退出应用", Toast.LENGTH_SHORT).show();
			lastClickTime = System.currentTimeMillis();
		} else {
			long currentClickTime = System.currentTimeMillis();
			if (currentClickTime - lastClickTime < backSpaceTimeIntervalMilliSecond) {
				finish();
				// kill the process of the APP.
				android.os.Process.killProcess(android.os.Process.myPid());
			} else {
				Toast.makeText(this, "再按一次后退键退出应用", Toast.LENGTH_SHORT).show();
				lastClickTime = currentClickTime;
			}
		}
	}
	
	
	public static void displayToast(String str, Context context){
		/*
		 * make a toast. 
		 */
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
	
	public void openWifi() {
        if(!wifiMg.isWifiEnabled()) {  
        	wifiMg.setWifiEnabled(true); 
        }  
    } 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * create menu.
		 */
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * listen when menu item is selected.
		 */
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			case R.id.action_search:
				return true;
			case R.id.action_plus:
				return true;
			case R.id.start_step:
				obtainStepData.obtainStepSetting();
				ObtainStepData.initPoints();
				return true;
			case R.id.correct_step:
				obtainStepData.correctStep();
				IndoorMapArrayAdapter.setIsRecordTrajectory(false);
				ObtainStepData.clearPoints();
				ObtainStepData.initPoints();
				ObtainWasproCoords.correctWaspro();
				return true;
			case R.id.stop_step:
				obtainStepData.stopStep();
				return true;
			case R.id.show_step:
				IndoorMapArrayAdapter.setIsRecordTrajectory(true);
				return true;
			case R.id.link_network:
				SocketClient.connectServer(MainActivity.this);
				return true;
			case R.id.broken_link_network:
				SocketClient.disconnectServer();
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		/*
		 * when the APP is destroyed, clear the flag of "keep screen on".
		 */
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onDestroy();
	}
	
}
