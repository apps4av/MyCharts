/*
Copyright (c) 2012, Apps4Av Inc. (apps4av.com) 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, LONGITUDE, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.chartsack.charts;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ZoomButton;


/**
 * 
 * @author zkhan
 * 
 * A zoom view that looks like Android'd built in zoom control, but makes it simpler
 *
 */
public class ZoomButtons extends LinearLayout implements OnClickListener {

	private ZoomButton mZoomInButton;
	private ZoomButton mZoomOutButton;
	private ZoomClick mCallback;
    
	/**
	 * 
	 */
	private void setup(Context context) {
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.zoom, this, true);
        mZoomInButton = (ZoomButton)view.findViewById(R.id.zoom_in);
        mZoomOutButton =(ZoomButton)view.findViewById(R.id.zoom_out);
        mZoomInButton.setOnClickListener(this);
        mZoomOutButton.setOnClickListener(this);
	}
	

	/**
     * 
     * @param context
     */
    public ZoomButtons(Context context) {
        super(context);
        setup(context);
    }

    /**
     * 
     * @param context
     */
    public ZoomButtons(Context context, AttributeSet set) {
        super(context, set);
        setup(context);
    }
    
    /**
     * 
     * @param context
     */
    public ZoomButtons(Context context, AttributeSet set, int arg) {
        super(context, set, arg);
        setup(context);
    }
    
    /**
     * 
     */
    public void setZoomClickListerner(ZoomClick zc) {
    	mCallback = zc;
    }

	@Override
	public void onClick(View view) {
		if(mCallback == null) {
			
		}
		else if(view == mZoomInButton) {
			mCallback.onZoom(true);
		}
		else if(view == mZoomOutButton) {
			mCallback.onZoom(false);
		}
	}

	
    /**
     * Make a custom callback
     */
	public interface ZoomClick {
		public void onZoom(boolean in);
	}

}

