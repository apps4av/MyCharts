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
    	if(null == getService()) {
    		return;
    	}
    	
    	double data[] = getService().getGeotagData();
    	double dx = data[0];
    	double dy = data[1];
    	double lonTopLeft = data[2];
    	double latTopLeft = data[3];

        double lon = getService().getGpsParams().getLongitude();
        double lat = getService().getGpsParams().getLatitude();
        double pixx = (lon - lonTopLeft) * dx;
        double pixy = (lat - latTopLeft) * dy;

        /*
         * Draw a circle on current location
         */
        getPaint().setStrokeWidth(4);
        getPaint().setColor(Color.BLUE);
        canvas.drawCircle(
        		getService().getPan().getMoveX() - (float)pixx, 
        		getService().getPan().getMoveY() - (float)pixy, 
                16,
                getPaint());
        getPaint().setColor(Color.RED);
        canvas.drawCircle(
        		getService().getPan().getMoveX() - (float)pixx, 
        		getService().getPan().getMoveY() - (float)pixy, 
                12,
                getPaint());
        getPaint().setColor(Color.GREEN);
        canvas.drawLine(
        		getService().getPan().getMoveX() - (float)pixx,
        		getService().getPan().getMoveY() - (float)pixy - 24,
        		getService().getPan().getMoveX() - (float)pixx,
        		getService().getPan().getMoveY() - (float)pixy + 24,
        		getPaint());
        canvas.drawLine(
        		getService().getPan().getMoveX() - (float)pixx - 24,
        		getService().getPan().getMoveY() - (float)pixy,
        		getService().getPan().getMoveX() - (float)pixx + 24,
        		getService().getPan().getMoveY() - (float)pixy,
        		getPaint());
        getPaint().setTextSize(20);
        /*
         * Edge tape
         */
      	EdgeDistanceTape.draw(canvas, getPaint(), Helper.findPixelsPerMile(dy),
      			(int)(getService().getPan().getMoveX() - (float)pixx),
      			(int)(getService().getPan().getMoveY() - (float)pixy), 
      			0, getWidth(), getHeight());

    }
    
    /**
     * 
     * @return
     */
	public boolean centerOnChart(double lon, double lat) {
		/*
		 * Change pan to center on GPS location
		 */
    	if(null == getService() || null == getService().getBitmapHolder()) {
    		return false;
    	}
    	double data[] = getService().getGeotagData();
    	double dx = data[0];
    	double dy = data[1];
    	double lonTopLeft = data[2];
    	double latTopLeft = data[3];

        double pixx = (lon - lonTopLeft) * dx;
        double pixy = (lat - latTopLeft) * dy;

        boolean ret = getService().getPan().setMove(
        		(float)pixx + getWidth() / 2,
        		(float)pixy + getHeight() / 2);
      	getService().loadBitmap(null);
      	
		return ret;
	}
    

}

