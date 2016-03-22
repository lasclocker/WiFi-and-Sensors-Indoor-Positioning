package com.example.util;

import java.util.List;
import java.util.Map.Entry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.activity.R;

public class ButtonImageArrayAdapter extends ArrayAdapter<Entry<String, Integer>> {
	/*
	 * custom defined ArrayAdapter for "Entry<String, Integer>" instead of "String".
	 */
	private final Context context;
	private TextView textView;
	private ImageView imageView;
	private LayoutInflater inflater;
	private Entry<String, Integer> entry;
	private List<Entry<String, Integer>> values;
	

	public ButtonImageArrayAdapter(Context context, List<Entry<String, Integer>> values) {
		super(context, R.layout.step_image_list1, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.step_image_list1, parent, false);
		}
		textView = (TextView) convertView.findViewById(R.id.label);
		imageView = (ImageView) convertView.findViewById(R.id.logo);
		entry = values.get(position);
		textView.setText(entry.getKey());
		imageView.setImageResource(entry.getValue());
		return convertView;
	}
}
