/*
Copyright (c) 2012, Apps4Av Inc. (apps4av.com) 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.chartsack.charts;


import com.chartsack.charts.gps.GpsParams;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


/**
 * 
 * @author zkhan
 * 
 * User sees the map on this view
 *
 */
public class MapView extends MappingView implements OnTouchListener {

	
	private double mLonTopLeft;
	private double mLatTopLeft;
	private double mDx;
	private double mDy;
	private GpsParams mGpsParams;
	
    /**
     * 
     * @param context
     */
	public MapView(Context context) {
		super(context);
		setOnTouchListener(this);
	}

    /**
     * 
     * @param context
     */
    public MapView(Context context, AttributeSet set) {
        super(context, set);
		setOnTouchListener(this);
    }
    
    /**
     * 
     * @param context
     */
    public MapView(Context context, AttributeSet set, int arg) {
        super(context, set, arg);
		setOnTouchListener(this);
    }

	/* (non-Javadoc)
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        return super.onTouch(view, e);
    }

    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    public void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
    	/*
    	 * Draw our location
    	 */
    	if(null == mGpsParams || null == getService()) {
    		return;
    	}
        double lon = mGpsParams.getLongitude();
        double lat = mGpsParams.getLatitude();
        double pixx = (lon - mLonTopLeft) * mDx;
        double pixy = (lat - mLatTopLeft) * mDy;

        /*
         * Draw a circle on current location
         */
        getPaint().setColor(Color.BLUE);
        canvas.drawCircle(
        		getService().getPan().getMoveX() - (float)pixx, 
        		getService().getPan().getMoveY() - (float)pixy, 
                12,
                getPaint());
        getPaint().setColor(Color.RED);
        canvas.drawCircle(
        		getService().getPan().getMoveX() - (float)pixx, 
        		getService().getPan().getMoveY() - (float)pixy, 
                16,
                getPaint());
    	
    }
    
    /**
     * Set geo coordinates 
     * @param data
     */
    public boolean setCoordinates(String data) {
    	String tokens[] = data.split(",");
    	if(tokens.length != 4) {
    		return false;
    	}
    	
    	try {
	    	mDx = Double.parseDouble(tokens[0]);
	    	mDy = Double.parseDouble(tokens[1]);
	    	mLonTopLeft = Double.parseDouble(tokens[2]);
	    	mLatTopLeft = Double.parseDouble(tokens[3]);
    	}
    	catch(Exception e) {
    		return false;
    	};
    	return true;
    }
    
    /**
     * 
     * @param g
     */
    public void setGpsParams(GpsParams g) {
    	mGpsParams = g;
    }
    

}
