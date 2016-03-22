package com.example.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.example.activity.R;
import com.example.util.ObtainStepData;

public class StepActivity extends Activity {
	/*
	 * the editable configure of Step information.
	 */

	private EditText etStepThreshold;
	private EditText etStepObtainDelaySec;
	private EditText etCo_K_wein;
	private static float accThreshold = 0.65f;
	private static int stepObtainDelaySec = 0;
	private static float co_k_wein = 45f;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.step_activity);

		etStepThreshold = (EditText) findViewById(R.id.editText_accThreshold);
		etStepThreshold.setText(Float.toString(ObtainStepData.getAccThreshold()));
		etStepThreshold.addTextChangedListener(new GenericTextWatcher(etStepThreshold));
		
		etStepObtainDelaySec = (EditText) findViewById(R.id.editText_stepObtainDelaySec);
		etStepObtainDelaySec.setText(Integer.toString(ObtainStepData.getStepObtainDelaySec()));
		etStepObtainDelaySec.addTextChangedListener(new GenericTextWatcher(etStepObtainDelaySec));

		etCo_K_wein = (EditText) findViewById(R.id.editText_co_k_wein);
		etCo_K_wein.setText(Float.toString(ObtainStepData.getCo_k_wein()));
		etCo_K_wein.addTextChangedListener(new GenericTextWatcher(etCo_K_wein));
		
	}
	
	public static float getAccThreshold() {
		return accThreshold;
	}
	
	public static int getStepObtainDelaySec() {
		return stepObtainDelaySec;
	}
	
	public static float getCo_k_wein() {
		return co_k_wein;
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
				case R.id.editText_accThreshold:
					accThreshold = Float.parseFloat(text);
					break;
				case R.id.editText_stepObtainDelaySec:
					stepObtainDelaySec = Integer.parseInt(text);
				case R.id.editText_co_k_wein:
					co_k_wein = Float.parseFloat(text);
				default:
					break;
				}
				
			}
		}
		
		
	}
}
