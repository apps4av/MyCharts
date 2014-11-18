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

import com.chartsack.charts.touch.MultiTouchController;
import com.chartsack.charts.touch.MultiTouchController.MultiTouchObjectCanvas;
import com.chartsack.charts.touch.MultiTouchController.PointInfo;
import com.chartsack.charts.touch.MultiTouchController.PositionAndScale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ZoomControls;


/**
 * 
 * A class that draws and pans
 * 
 * @author zkhan
 *
 */
public class MappingView extends View implements MultiTouchObjectCanvas<Object>  {

    private MultiTouchController<Object> mMultiTouchC;
    private PointInfo                    mCurrTouchPoint;
    private Paint                        mPaint;
    private StorageService               mService;
    private ZoomControls 				 mZoomControls;
    private ImageButton 				 mTopButton;

    /**
     * 
     * @param context
     */
	public MappingView(Context context) {
		super(context);
		setup(context);
	}

    /**
     * 
     * @param context
     */
    public MappingView(Context context, AttributeSet set) {
        super(context, set);
        setup(context);
    }

    
    /**
     * 
     * @param context
     */
    public MappingView(Context context, AttributeSet set, int arg) {
        super(context, set, arg);
        setup(context);
    }
    
    /**
     * 
     * @param context
     */
    private void setup(Context context) {
        mService = null;
        mMultiTouchC = new MultiTouchController<Object>(this);
        mCurrTouchPoint = new PointInfo();
        setBackgroundColor(Color.BLACK);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }


    /**
     * 
     */
    private void startAnimation(boolean start) {
        if(null == mZoomControls) {
        	return;
        }
    	mZoomControls.setVisibility(View.VISIBLE);
    	// Show if animation is started
        if(start) {
        	return;
        }
        // Hide zoom controls after 5 seconds
        mHandler.removeMessages(0, null);
        mHandler.sendEmptyMessageDelayed(0, 5000);
    }
    
    /**
     * 
     */
    Handler mHandler = new Handler() {
    	@Override
   	    public void handleMessage(Message msg) {
            // Hide after timeout if animation stopped
            mZoomControls.setVisibility(View.INVISIBLE);    		
    	}
    };
    
    /**
     * Add zoom control for all map views
     */
    public void setZoomControls(ZoomControls zc) {
    	mZoomControls = zc;
    	
    	/*
    	 * Zoom IN
    	 */
        mZoomControls.setOnZoomInClickListener(new OnClickListener() {
			public void onClick(View v) {
				float s = getService().getScale().getScaleFactor();
				// Want to scale at the center
				float x = getService().getPan().getMoveX() + getWidth() / 2;
				float y = getService().getPan().getMoveY() + getHeight() / 2;
				getService().getPan().setMove(x * Scale.SCALE_STEP, y * Scale.SCALE_STEP);
				getService().getScale().zoomIn();
		    	startAnimation(false);
				getService().loadBitmap(null); 	
			}
		});
        
        /*
         *  Zoom OUT
         */
        mZoomControls.setOnZoomOutClickListener(new OnClickListener() {
			public void onClick(View v) {
				getService().getScale().zoomOut();
		    	startAnimation(false);
				float s = getService().getScale().getScaleFactor();
				float x = getService().getPan().getMoveX() + getWidth() / 2;
				float y = getService().getPan().getMoveY() + getHeight() / 2;
				getService().getPan().setMove(x / Scale.SCALE_STEP, y / Scale.SCALE_STEP);
				getService().loadBitmap(null); 	
			}
		});
    }
    
    /**
     * Set home button for all map views
     */
    public void setHomeControls(ImageButton ib) {
    	mTopButton = ib;
        mTopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getService().resetMap();
			}
        });
    }
    
    
    /**
     * 
     * @param view
     * @param e
     * @return
     */
    public boolean onTouch(View view, MotionEvent e) {

    
		if(mService == null) {
			return false;
		}
		
	    if(e.getAction() == MotionEvent.ACTION_DOWN) {
	    	/*
	    	 * Start the drag
	    	 */
	        mService.getPan().startDrag();
	        /*
	         * Show the zoom button
	         */
	        startAnimation(true);
	    }
	    else if(e.getAction() == MotionEvent.ACTION_UP) {
	    	/*
	    	 * Decode region of the image when we lift finger
	    	 */
	        startAnimation(false);	    	
	      	mService.loadBitmap(null);
	    }
	    /*
	     * This slows downs panning when zoomed in
	     */
	    return mMultiTouchC.onTouchEvent(e);
    }


    /* (non-Javadoc)
     * @see com.ds.avare.MultiTouchController.MultiTouchObjectCanvas#selectObject(java.lang.Object, com.ds.avare.MultiTouchController.PointInfo)
     */
    public void selectObject(Object obj, PointInfo touchPoint) {
        touchPointChanged(touchPoint);
    }

    /* (non-Javadoc)
     * @see com.ds.avare.MultiTouchController.MultiTouchObjectCanvas#setPositionAndScale(java.lang.Object, com.ds.avare.MultiTouchController.PositionAndScale, com.ds.avare.MultiTouchController.PointInfo)
     */
    public boolean setPositionAndScale(Object obj,PositionAndScale newObjPosAndScale, PointInfo touchPoint) {
    	
    	if(mService == null || mService.getBitmapHolder() == null || mService.getBitmapHolder().getBitmap() == null) {
    		return false;
    	}
    	
        touchPointChanged(touchPoint);
        if(false == mCurrTouchPoint.isMultiTouch()) {
            /*
             * Multi-touch is zoom, single touch is pan
             */
            mService.getPan().setMove(
                    newObjPosAndScale.getXOff(), 
                    newObjPosAndScale.getYOff());
        }

        invalidate();
        return true;
    }

    /**
     * @param touchPoint
     */
    private void touchPointChanged(PointInfo touchPoint) {
        mCurrTouchPoint.set(touchPoint);
        invalidate();
    }

    
    /* (non-Javadoc)
     * @see com.ds.avare.MultiTouchController.MultiTouchObjectCanvas#getDraggableObjectAtPoint(com.ds.avare.MultiTouchController.PointInfo)
     */
    public Object getDraggableObjectAtPoint(PointInfo pt) {
    	return this;
    }

    /* (non-Javadoc)
     * @see com.ds.avare.MultiTouchController.MultiTouchObjectCanvas#getPositionAndScale(java.lang.Object, com.ds.avare.MultiTouchController.PositionAndScale)
     */
    public void getPositionAndScale(Object obj, PositionAndScale objPosAndScaleOut) {
    	if(null == mService) {
    		return;
    	}
        objPosAndScaleOut.set(
                mService.getPan().getMoveX(), mService.getPan().getMoveY(), true,
                1, false, 0, 0, false, 0);
    }

    /**
     * 
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(null != getService()) {
	        // This will serve as map dimensions. Match with layout of map view
	        // Note this will take effect when a new map is loaded
	    	getService().setHeight(getHeight());
	    	getService().setWidth(getWidth());
        }
    }
    
    /**
     * 
     */
    public void onDraw(Canvas canvas) {
    	if(mService == null || mService.getBitmapHolder() == null || mService.getBitmapHolder().getBitmap() == null) {
    		return;
    	}
        /*
         * Draw the bitmap
         */
        mService.getBitmapHolder().getTransform().setTranslate(mService.getPan().getDragX(), mService.getPan().getDragY());

        canvas.drawBitmap(mService.getBitmapHolder().getBitmap(), mService.getBitmapHolder().getTransform(), mPaint);
        
    	/*
    	 * The cross in the middle
    	 */
    	getPaint().setColor(Color.RED);
    	getPaint().setStyle(Style.STROKE);
    	getPaint().setStrokeWidth(2);
        canvas.drawLine(getWidth() / 4, getHeight() / 2, (getWidth() * 3) / 4, getHeight() / 2, getPaint());
        canvas.drawLine(getWidth() / 2, getHeight() / 4, getWidth() / 2, (getHeight() * 3) / 4, getPaint());
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 4, getPaint());

    }
    
    
    /**
     * 
     */
    public void setService(StorageService service) {
    	mService = service;
    } 

    /**
     * 
     */
    public StorageService getService() {
    	return mService;
    } 

    /**
     * 
     */
    public Paint getPaint() {
    	return mPaint;
    }
    
}
