package com.example.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Set extends Activity{
	private String str,rate;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set);
		EditText frameRate=(EditText) findViewById(R.id.frame_rate);
	     rate=frameRate.getText().toString();
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		String[]  resolution= new String[]
				{ "CIF (352 x 288)","SQCIF (128 x 98)", "QCIF (176 x 144)", "QVGA (320 x 240)",  "HVGA (480 x 320)",
				            "VGA (640 x 480)","4CIF (704 x 576)","SVGA (800 x 600)","720P (1280 x 720)","16CIF (1408 x 1152)","1080P (1920 x 1080)" };
				ArrayAdapter<String> aaAdapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, resolution);
				spinner.setAdapter(aaAdapter);	
				spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO 自动生成的方法存根
					     str=parent.getItemAtPosition(position).toString();
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO 自动生成的方法存根					
					}						
				});
	}
	public void onBackPressed(){
		Intent intent=new Intent();
		Bundle bundle=new Bundle();
		bundle.putString("result",rate);
		bundle.putString("resolution",str);
		intent.putExtras(bundle);
		this.setResult(1,intent);
		this.finish();
	}
 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
