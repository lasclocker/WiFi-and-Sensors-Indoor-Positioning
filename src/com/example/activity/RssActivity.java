package com.example.activity;

import com.example.activity.R;
import com.example.util.ObtainRssData;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;

public class RssActivity extends Activity {
	/*
	 * the editable configure of RSS information.
	 */

	private EditText etAcquireSignalTotalNum;
	private EditText etAcquireTimeInterval_ms;
	private EditText etSSID;
	private static int acquireSignalTotalNum = 5;
	private static long acquireTimeInterval_ms = 1000;
	private static String ssid = "lab524_1,lab524_2,lab524_3";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rss_activity);
		
		etAcquireSignalTotalNum = (EditText) findViewById(R.id.editText_acquireSignalTotalNum);
		etAcquireSignalTotalNum.setText(Integer.toString(ObtainRssData.getAcquireSignalTotalNum()));
		etAcquireSignalTotalNum.addTextChangedListener(new GenericTextWatcher(etAcquireSignalTotalNum));
		
		etAcquireTimeInterval_ms = (EditText) findViewById(R.id.editText_acquireTimeInterval_ms);
		etAcquireTimeInterval_ms.setText(Long.toString(ObtainRssData.getAcquireTimeInterval_ms()));
		etAcquireTimeInterval_ms.addTextChangedListener(new GenericTextWatcher(etAcquireTimeInterval_ms));
		
		etSSID = (EditText) findViewById(R.id.editText_SSID);
		etSSID.setText(ObtainRssData.getSSID());
		etSSID.addTextChangedListener(new GenericTextWatcher(etSSID));
		
	}
	
	public static int getAcquireSignalTotalNum() {
		return acquireSignalTotalNum;
	}
	
	public static long getAcquireTimeInterval_ms() {
		return acquireTimeInterval_ms;
	}
	
	public static String getSSID() {
		return ssid;
	}
	
	private class GenericTextWatcher implements TextWatcher {
		/*
		 * custom defined EditText
		 */
		
		private View view;
	    private GenericTextWatcher(View view) {
	        this.view = view;
	    }

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}

		@Override
		public void afterTextChanged(Editable s) {
			String text = s.toString();
			if (! text.isEmpty()) {
//				System.out.println("after text changed " + new String(s.toString()));
				switch (view.getId()) {
				case R.id.editText_acquireSignalTotalNum:
					acquireSignalTotalNum = Integer.parseInt(text);
					break;
				case R.id.editText_acquireTimeInterval_ms:
					acquireTimeInterval_ms = Long.parseLong(text);
				case R.id.editText_SSID:
					ssid = text;
				default:
					break;
				}
				
			}
		}
		
		
	}
}
