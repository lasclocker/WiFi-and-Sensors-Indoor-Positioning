package com.example.util;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.TextView;

import com.example.activity.MainActivity;
import com.example.activity.StepActivity;

public class ObtainStepData implements SensorEventListener {
	/*
	 * obtain step information of pedestrian.
	 */

	SensorManager Sm;
	Context context;
	Sensor Accelerometer, Magnetometer;
	TextView textView_showRecordAccTime, textView_countStep, textView_stepLength, 
	         textView_degree, textView_coordinate;
	int maLength = 5, stepState = 0, stepCount = 0;
	long lastUpdate = 0, zeroTime = 0, accNum = 0, lastTimeAcc, curTimeAcc, lastTimeMag, curTimeMag;
	boolean isRecordAcc = false, isObtainAccView = false, isObtainDegreeView = false;
	float[] accValues = new float[3];
	float[] magValues = new float[3];
	float[] values = new float[3];
	float[] R = new float[9];
	float[] I = new float[9];
	float accModule = 0, maResult = 0;
	float maxVal = 0f, minVal = 0f, stepLength = 0f;
	static int stepObtainDelaySec = 0;
	static float accThreshold = 0.65f, co_k_wein = 45f, alpha = 0.25f;
	int degreeDisplay, sensorCounter;
	float offset, degree;
	StringBuilder sbAcc = new StringBuilder();
	StringBuilder sbAccMA = new StringBuilder();
	DecimalFormat decimalF = new DecimalFormat("#.00");
	final static int[] initPoint = {137, 642};
	static float[] curCoordsOfStep = {137, 642};
	static StringBuilder stepMessage = new StringBuilder();
	static ArrayList<CoordPoint> points = new ArrayList<CoordPoint>();
	
	
	
	public ObtainStepData(Context context,TextView textView_countStep, TextView textView_showRecordAccTime,
			             TextView textView_stepLength, TextView textView_degree, TextView textView_coordinate) {
		this.context = context;
		this.textView_countStep = textView_countStep;
		this.textView_showRecordAccTime = textView_showRecordAccTime;
		this.textView_stepLength = textView_stepLength;
		this.textView_degree = textView_degree;
		this.textView_coordinate = textView_coordinate;
		loadSystemService();
	}
	
	public void loadSystemService() {
		/*
		 * load sensor system service.
		 */
		Sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		Accelerometer = Sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Magnetometer = Sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}
	
	public void obtainStepSetting() {
		/*
		 * before start listening sensor of Accelerometer and Magnetometer, we set and obtain some parameters.
		 */
		setObtainAccView(true);
		setObtainDegreeView(true);
		stepObtainDelaySec = StepActivity.getStepObtainDelaySec();
		accThreshold = StepActivity.getAccThreshold();
		co_k_wein = StepActivity.getCo_k_wein();
		textView_countStep.setVisibility(View.VISIBLE);
		textView_stepLength.setVisibility(View.VISIBLE);
		textView_degree.setVisibility(View.VISIBLE);
		textView_coordinate.setVisibility(View.VISIBLE);
	}
	
	
	public void obtainStep() {
		/*
		 * start listening sensor of Accelerometer and Magnetometer.
		 */
		lastTimeAcc = System.currentTimeMillis();
		lastTimeMag = System.currentTimeMillis();
		Sm.registerListener(this, Accelerometer,SensorManager.SENSOR_DELAY_GAME);
		Sm.registerListener(this, Magnetometer,SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		/*
		 * if Accelerometer or Magnetometer changes, do as followings.
		 */
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			curTimeAcc = System.currentTimeMillis();
			if (curTimeAcc - lastTimeAcc > 40) {
				getStepAccInfo(event.values.clone());
				lastTimeAcc = curTimeAcc;
			}
		} 
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			curTimeMag = System.currentTimeMillis();
			if (curTimeMag - lastTimeMag > 40) {
				getAzimuthDegree(event.values.clone());
				lastTimeMag = curTimeMag;
			}
		}
	}

	private void getStepAccInfo(float[] accClone) {
		/*
		 * step information computing algorithm.
		 */
		accValues = accClone;
		accModule = (float) (Math.sqrt(Math.pow(accValues[0], 2) + Math.pow(accValues[1], 2) + Math.pow(accValues[2], 2)) - 9.794);
		maResult = MovingAverage.movingAverage(accModule, maLength);
		if (stepState == 0 && maResult > accThreshold) {
			stepState = 1;
		} 
		if (stepState == 1 && maResult > maxVal) { //find peak
			maxVal = maResult;
		} 
		if (stepState == 1 && maResult <= 0) {
			stepState = 2;
		} 
		if (stepState == 2 && maResult < minVal) { //find bottom
			minVal = maResult;
		} 
		if (stepState == 2 && maResult >= 0) {
			stepCount ++;
			getStepLengthAndCoordinate();
			recordTrajectory(curCoordsOfStep.clone());
			StepMessageToSocket();
			maxVal = minVal = stepState = 0;
		}
		if (isObtainAccView) stepViewShow();
		if (isRecordAcc) recordStepInfo();
	}

	private void StepMessageToSocket() {
		/*
		 * the message to send to the socket.
		 */
		stepMessage.append(stepCount + " " + decimalF.format(stepLength) + ",");
	}

	private void recordStepInfo() {
		/*
		 * the recording information to show.
		 */
		textView_showRecordAccTime.setText("Record Time : " + decimalF.format(0.04*accNum)); // show record time
		sbAcc.append(accValues[0])
		.append(" ")
		.append(accValues[1])
		.append(" ")
		.append(accValues[2])
		.append(" ")
		.append(decimalF.format(0.04 * accNum)) // 0.04*accNum
		.append("\n");
		sbAccMA.append(maResult + " ");
		accNum++;
	}

	private void stepViewShow() {
		/*
		 * show some step information.
		 */
		textView_countStep.setText("Step Count : " + stepCount);
		textView_stepLength.setText("Step Length : " + decimalF.format(stepLength) + " cm");
		textView_coordinate.setText("Coordinate : " + "X: " + decimalF.format(curCoordsOfStep[0]) + " Y: " 
		                                            + decimalF.format(curCoordsOfStep[1]));
	}

	private void getStepLengthAndCoordinate() {
		/*
		 * compute step length and coordinate of pedestrian.
		 */
		stepLength = (float)(co_k_wein * Math.pow(maxVal - minVal,1.0/4));
		double delta_x = Math.cos(Math.toRadians(degreeDisplay)) * stepLength;
		double delta_y = Math.sin(Math.toRadians(degreeDisplay)) * stepLength;
		curCoordsOfStep[0] += delta_x;
		curCoordsOfStep[1] += delta_y;
	}
	
	private void recordTrajectory(float[] clone) {
		/*
		 * add the coordinate points of pedestrian.
		 */
		points.add(new CoordPoint(clone[0], clone[1]));
	}
	
	
	public static void initPoints() {
		/*
		 * add the initial position coordinate of pedestrian.
		 */
		points.add(new CoordPoint(initPoint[0], initPoint[1]));
	}

	private void getAzimuthDegree(float[] MagClone) {
		/*
		 * get the azimuth degree of the pedestrian.
		 */
		magValues = lowPassFilter(MagClone, magValues);
		if (accValues == null || magValues == null) return;
		boolean sucess = SensorManager.getRotationMatrix(R, I, accValues, magValues);
		if (sucess) {
			SensorManager.getOrientation(R, values);
			degree = (int)(Math.toDegrees(values[0]) + 360) % 360; // translate into (0, 360).
			degree = ((int)(degree + 2)) / 5 * 5; // the value of degree is multiples of 5.
			if (offset == 0) {
				degreeDisplay = (int) degree;
			} else {
				degreeDisplay = roomDirection(degree, offset); // user-defined room direction.
			}
			if (isObtainDegreeView) stepDegreeViewShow();
		}
	}

	private void stepDegreeViewShow() {
		/*
		 * show the azimuth degree.
		 */
		textView_degree.setText(" Angle : " + degreeDisplay + " degree");
	}

	private int roomDirection(float myDegree, float myOffset) {
		/*
		 * define room direction as 270 degree.
		 */
		int tmp = (int)(myDegree - myOffset);
		if(tmp < 0) tmp += 360;
    	else if(tmp >= 360) tmp -= 360;
		return tmp;
	}

	protected float[] lowPassFilter(float[] input, float[] output) {
		/*
		 * low pass filter algorithm implement.
		 */
	    if (output == null) return input;     
	    for (int i = 0; i < input.length; i++) {
	        output[i] = output[i] + alpha * (input[i] - output[i]);
	    }
	    return output;
	}

	public void recordStep() {
		/*
		 * start record step information.
		 */
		textView_showRecordAccTime.setVisibility(View.VISIBLE);
		setRecordAcc(true);
	}
	
	public void stopStep() {
		/*
		 * stop listening for sensor and recording step information. 
		 */
		setRecordAcc(false);
		setObtainAccView(false);
		setObtainDegreeView(false);
		Sm.unregisterListener(this);
	}
	
	public String getSbAcc() {
		/*
		 * get the original acceleration information.
		 */
		if (sbAcc.length() != 0) {
			String tmp = sbAcc.toString();
			sbAcc.delete(0, sbAcc.length());
			MainActivity.displayToast("Save ok...", context);
			return tmp;
		} else {
			MainActivity.displayToast("Please open Step Recording...", context);
			return "";
		}
	}
	
	public String getSbAccMA() {
		/*
		 * get the acceleration information after moving average.
		 */
		if (sbAccMA.length() != 0) {
			StringBuilder sbMaTmp = sbAccMA.delete(sbAccMA.length() - 1, sbAccMA.length());
			String tmp = sbMaTmp.toString();
			sbAccMA.delete(0, sbAccMA.length());
			return tmp;
		} else {
			return "";
		}
	}
	
	public void correctStep() {
		/*
		 * initialize and correct the step parameters.
		 */
		offset = degree - 270;
		curCoordsOfStep[0] = 137;
		curCoordsOfStep[1] = 642;
		stepCount = 0;
		accNum = 0;
		stepLength = 0;
	}
	
	public void stepViewGone() {
		/*
		 * set the view to gone.
		 */
		textView_countStep.setVisibility(View.GONE);
		textView_stepLength.setVisibility(View.GONE);
		textView_showRecordAccTime.setVisibility(View.GONE);
		textView_degree.setVisibility(View.GONE);
		textView_coordinate.setVisibility(View.GONE);
	}
	
	public static String removeLastChar(String str) {
		/*
		 * remove the last char of a string.
		 */
		if (str != null && str.length() > 0 && str.charAt(str.length()-1) == ',') {
			str = str.substring(0, str.length()-1);
		}
		return str;
	}
	
	public static String getStepMessage() {
		/*
		 * get the step message which will send to the socket client.
		 */
		String tmp = stepMessage.toString();
		stepMessage.setLength(0);
		tmp = removeLastChar(tmp);
		return SocketClient.sendSucess() ? tmp : SocketClient.getToSendMessage() + tmp;
	}
	
	public void setRecordAcc(boolean isRecordAcc) {
		/*
		 * whether record acceleration or not.
		 */
		this.isRecordAcc = isRecordAcc;
	}
	
	public void setObtainAccView(boolean isObtainAccView) {
		/*
		 * whether obtain acceleration view or not.
		 */
		this.isObtainAccView = isObtainAccView;
	}
	
	public void setObtainDegreeView(boolean isObtainDegreeView) {
		/*
		 * whether obtain the azimuth degree or not.
		 */
		this.isObtainDegreeView = isObtainDegreeView;
	}
	
	public static void setCurCoordsOfStep(float[] coords) {
		/*
		 * set the current coordinate of the pedestrian.
		 */
		curCoordsOfStep = coords.clone();
	}
	
	public static float getAccThreshold() {
		/*
		 * get the acceleration threshold of the pedestrian.
		 */
		return accThreshold;
	}
	
	public static int getStepObtainDelaySec() {
		/*
		 * get the delay seconds of obtaining step information.
		 */
		return stepObtainDelaySec;
	}
	
	public static float getCo_k_wein() {
		/*
		 * get the coefficient K of weinberg model.
		 */
		return co_k_wein;
	}
	
	public static float[] getCurCoordsOfStep() {
		/*
		 * get the current coordinate of pedestrian step.
		 */
		return curCoordsOfStep;
	}
	
	public static ArrayList<CoordPoint> getPoints() {
		/*
		 * get coordinate point.
		 */
		return points;
	}
	
	public static void clearPoints() {
		/*
		 * clear coordinate point.
		 */
		points.clear();
	}
	
	
}