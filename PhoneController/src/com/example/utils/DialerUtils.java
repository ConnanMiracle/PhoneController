/* Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, imshello Telecom.
*
* Contact: Mamadou Diop <diopmamadou(at)imshello(dot)org>
*	
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package com.example.utils;



import com.example.controller.R;
import com.example.controller.R.id;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DialerUtils {
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
	
	public static void setDialerTextButton(View view, String num, String text, int tag, View.OnClickListener listener){
		view.setTag(tag);
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
}