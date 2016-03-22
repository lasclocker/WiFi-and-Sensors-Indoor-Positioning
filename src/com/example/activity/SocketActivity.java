package com.example.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.example.activity.R;
import com.example.util.SocketClient;

public class SocketActivity extends Activity {
	/*
	 * the editable configure of Socket information.
	 */

	private EditText etIP;
	private EditText etPort;
	private static String IP = "192.168.2.101";
	private static int port = 8080;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.socket_activity);
		
		etIP = (EditText) findViewById(R.id.etIP);
		etIP.setText(SocketClient.getSocketIP());
		etIP.addTextChangedListener(new GenericTextWatcher(etIP));
		
		etPort = (EditText) findViewById(R.id.etPort);
		etPort.setText(Integer.toString(SocketClient.getSocketPort()));
		etPort.addTextChangedListener(new GenericTextWatcher(etPort));
		
	}
	
	public static String getSocketIP() {
		return IP;
	}
	
	public static int getSocketPort() {
		return port;
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
				System.out.println("after text changed " + new String(s.toString()));
				switch (view.getId()) {
				case R.id.etIP:
					IP = text;
					break;
				case R.id.etPort:
					port = Integer.parseInt(text);
				default:
					break;
				}
				
			}
		}
		
		
	}
}
