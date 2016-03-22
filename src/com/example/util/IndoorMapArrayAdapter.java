package com.example.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.activity.MainActivity;
import com.example.activity.R;

public class IndoorMapArrayAdapter extends ArrayAdapter<String> {
	/*
	 * custom defined ArrayAdapter for "String". when the indoor map is shown, call this class.
	 */
	TextView textView;
	String textVal;
	Paint paint;
	LayoutInflater inflater;
	final Context context;
	final String[] values;
	static Canvas canvas;
	static ImageView imageView;
	static int[] newCoords            = new int[2];
	static int[] curTouchCoords       = {0, 0};
	static float[] myCoords           = new float[2];
	static boolean isRecordTrajectory = false;
	Bitmap backgroundBitmap, bitmapToDrawInTheCenter, resultBitmap;
	
	
	public IndoorMapArrayAdapter(Context context, String[] values) {
		super(context, R.layout.show_image_list1, values);
		this.context = context;
		this.values = values;
		
		backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.back_indoor_540_960);
	    bitmapToDrawInTheCenter = BitmapFactory.decodeResource(context.getResources(), R.drawable.location_24_red);
	    bitmapToDrawInTheCenter = getResizedBitmap(bitmapToDrawInTheCenter, 50, 24);
	    
	    resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),backgroundBitmap.getHeight(), backgroundBitmap.getConfig());
	    canvas = new Canvas(resultBitmap);
	    paint = new Paint();
	    paint.setColor(Color.BLUE);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(7);
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.show_image_list1, parent, false);
		}
		textView = (TextView) convertView.findViewById(R.id.label);
		imageView = (ImageView) convertView.findViewById(R.id.logo);
		textVal = values[position];
		textView.setText(textVal);
		
	    canvas.drawBitmap(backgroundBitmap, new Matrix(), null);
	    myCoords = MainActivity.getCoords();
	    curTouchCoords = convertTouchCoordinates(myCoords);
	    if (isRecordTrajectory) {
	    	recordTrajectory();
		}
	    canvas.drawBitmap(bitmapToDrawInTheCenter, curTouchCoords[0], curTouchCoords[1], paint);
	    imageView.setImageBitmap(resultBitmap);
		return convertView;
	}
	
	private void recordTrajectory() {
		/*
		 * draw the line of trajectory.
		 */
		ArrayList<CoordPoint> tmpPoints = ObtainStepData.getPoints(); 
		for (int i = 0; i < tmpPoints.size()-1; i++) {
			CoordPoint startPoint = tmpPoints.get(i);
			startPoint = convertTouchCoordinates(startPoint);
			CoordPoint endPoint = tmpPoints.get(i+1);
			endPoint = convertTouchCoordinates(endPoint);
			canvas.drawLine(startPoint.px, startPoint.py, endPoint.px, endPoint.py, paint);
		}
	}


	public static String getOriginalCurTouchCoords() {
		/*
		 * get original current coordinate.
		 */
		return "" + myCoords[0] + "," + myCoords[1];
	}
	
	public static int[] convertTouchCoordinates(float[] coors) {
		/*
		 * float[] : convert coordinate to fit on the screen of mobile.
		 */
		newCoords[0] = (int)(coors[0] * ((float) canvas.getWidth() / imageView.getRight()));
		newCoords[1] = (int)(coors[1] * ((float) canvas.getHeight() / imageView.getBottom()));
		return newCoords;
	}
	
	public CoordPoint convertTouchCoordinates(CoordPoint coors) {
		/*
		 * CoordPoint : convert coordinate to fit on the screen of mobile.
		 */
		float xtmp = coors.px * ((float) canvas.getWidth() / imageView.getRight());
		float ytmp = coors.py * ((float) canvas.getHeight() / imageView.getBottom());
		return new CoordPoint(xtmp, ytmp);
	}
	
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		/*
		 * reset the size of Bitmap to another new size: newHeight, newWidth.
		 */
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    Matrix matrix = new Matrix(); 
	    matrix.postScale(scaleWidth, scaleHeight); 
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}
	
	public static void setIsRecordTrajectory(boolean is) {
		/*
		 * decide whether it is recording trajectory or not.
		 */
		isRecordTrajectory = is;
	}

	public static float getCanvasHeight() {
		/*
		 * get the height of canvas.
		 */
		return canvas.getHeight();
	}
	
	public static float getCanvasWidth() {
		/*
		 * get the width of canvas.
		 */
		return canvas.getWidth();
	}
	
}
