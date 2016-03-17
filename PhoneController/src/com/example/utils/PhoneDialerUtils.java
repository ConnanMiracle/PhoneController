package com.example.utils;

import java.util.HashMap;

import com.example.controller.R;
import com.example.controller.R.id;



import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
public class PhoneDialerUtils 
{
public static final int TAG_0 = 0;
public static final int TAG_1 = 1;
public static final int TAG_2 = 2;
public static final int TAG_3 = 3;
public static final int TAG_4 = 4;
public static final int TAG_5 = 5;
public static final int TAG_6 = 6;
public static final int TAG_7 = 7;
public static final int TAG_8 = 8;
public static final int TAG_9 = 9;
public static final int TAG_STAR = 10;
public static final int TAG_SHARP = 11;
public static final int TAG_VIDEO_CALL = 12;
public static final int TAG_DELETE = 13;
public static final int TAG_HIDE = 14;
public static final int TAG_SHOW = 15;
public static HashMap<Integer,String> dialerData = new HashMap<Integer,String>();	

public static void setDialerTextButton(View view, String num, String text, int tag, View.OnClickListener listener){
	view.setTag(tag);
	dialerData.put(tag, text);
	view.setOnClickListener(listener);
	((TextView)view.findViewById(R.id.dialer_text_num)).setText(num);
	((TextView)view.findViewById(R.id.dialer_text)).setText(text);
}

public static void setDialerTextButton(Activity parent, int viewId, String num, String text, int tag, View.OnClickListener listener){
	setDialerTextButton(parent.findViewById(viewId), num, text, tag, listener);
}
public static void setDialerTextButton(View parent, int viewId, String num, String text, int tag, View.OnClickListener listener){
	setDialerTextButton(parent.findViewById(viewId), num, text, tag, listener);
}
public static void setDialerImageButton(Activity parent, int viewId, int imageId, int tag, View.OnClickListener listener){
	View view = parent.findViewById(viewId);
	view.setTag(tag);
	view.setOnClickListener(listener);
	((ImageView)view.findViewById(R.id.dialer_button_image)).setImageResource(imageId);
}
public static void setDialerImageButton(View view, int tag, View.OnClickListener listener){
	view.setTag(tag);
	view.setOnClickListener(listener);
}
}
