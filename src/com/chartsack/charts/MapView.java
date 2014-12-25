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

    Multivariate mLonInterp;
    Multivariate mLatInterp;
    Multivariate mXInterp;
    Multivariate mYInterp;

    
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

    /**
     * Get point of interest at lon/lat
     * @return
     */
    private double[] getOffset(double lon, double lat) {
    	
    	/*
    	 * Get x y coordinates from interpolation of lon/lat
    	 */
    	double coords[] = new double[4];
    	Projection p = getService().getGeotagData();
        double scale = getService().getScale().getScaleFactor();
        
    	if(p != null && p.isValid()) {
            coords[0] = getService().getGeotagData().getX(lon, lat) / scale;
            coords[1] = getService().getGeotagData().getY(lon, lat) / scale;    		
    	}
    	else {
    		coords[0] = coords[1] = 0;
    	}
        
        coords[2] = 0;
        coords[3] = 0;
        
        return coords;
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
        
        double coords[] = getOffset(getService().getGpsParams().getLongitude(),
                getService().getGpsParams().getLatitude());

        /*
         * Draw a circle on current location
         */
        getPaint().setStrokeWidth(4);
        getPaint().setColor(Color.BLUE);
        canvas.drawCircle(
                getService().getPan().getMoveX() - (float)coords[0], 
                getService().getPan().getMoveY() - (float)coords[1], 
                16,
                getPaint());
        getPaint().setColor(Color.RED);
        canvas.drawCircle(
                getService().getPan().getMoveX() - (float)coords[0], 
                getService().getPan().getMoveY() - (float)coords[1], 
                12,
                getPaint());
        getPaint().setColor(Color.GREEN);
        canvas.drawLine(
                getService().getPan().getMoveX() - (float)coords[0],
                getService().getPan().getMoveY() - (float)coords[1] - 24,
                getService().getPan().getMoveX() - (float)coords[0],
                getService().getPan().getMoveY() - (float)coords[1] + 24,
                getPaint());
        canvas.drawLine(
                getService().getPan().getMoveX() - (float)coords[0] - 24,
                getService().getPan().getMoveY() - (float)coords[1],
                getService().getPan().getMoveX() - (float)coords[0] + 24,
                getService().getPan().getMoveY() - (float)coords[1],
                getPaint());
        getPaint().setTextSize(20);
        /*
         * Edge tape
         */
        /*
        EdgeDistanceTape.draw(canvas, getPaint(), getService().getScale(), Helper.findPixelsPerMile(coords[3]),
                (int)(getService().getPan().getMoveX() - (float)coords[0]),
                (int)(getService().getPan().getMoveY() - (float)coords[1]), 
                0, getWidth(), getHeight());
		*/
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

        double coords[] = getOffset(lon, lat);
        
        boolean ret = getService().getPan().setMove(
                (float)coords[0] + getWidth() / 2,
                (float)coords[1] + getHeight() / 2);
        getService().loadBitmap(null);
        
        return ret;
    }
    

}

