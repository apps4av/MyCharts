/*
Copyright (c) 2012, Apps4Av Inc. (apps4av.com) 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.apps4av.mycharts;

import org.apps4av.mycharts.touch.MultiTouchController;
import org.apps4av.mycharts.touch.MultiTouchController.MultiTouchObjectCanvas;
import org.apps4av.mycharts.touch.MultiTouchController.PointInfo;
import org.apps4av.mycharts.touch.MultiTouchController.PositionAndScale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
public class MapView extends View implements MultiTouchObjectCanvas<Object>, OnTouchListener {
	

    private MultiTouchController<Object> mMultiTouchC;
    private PointInfo                    mCurrTouchPoint;
    private Paint                        mPaint;
    private StorageService               mService;
    private int                          mWidth;
    private int                          mHeight;
    
    /**
     * 
     * @param context
     */
    private void  setup(Context context) {
        setOnTouchListener(this);
        mService = null;
        mMultiTouchC = new MultiTouchController<Object>(this);
        mCurrTouchPoint = new PointInfo();
        setBackgroundColor(Color.BLACK);
        mPaint = new Paint();
    }
    
    /**
     * 
     * @param context
     */
	public MapView(Context context) {
		super(context);
		setup(context);
	}

    /**
     * 
     * @param context
     */
    public MapView(Context context, AttributeSet set) {
        super(context, set);
        setup(context);
    }

    
    /**
     * 
     * @param context
     */
    public MapView(Context context, AttributeSet set, int arg) {
        super(context, set, arg);
        setup(context);
    }

    /* (non-Javadoc)
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        
    	if(mService == null) {
    		return false;
    	}
    	
        if(e.getAction() == MotionEvent.ACTION_DOWN) {
        	/*
        	 * Start the drag
        	 */
            mService.getPan().startDrag();
        }
        else if(e.getAction() == MotionEvent.ACTION_UP) {
        	/*
        	 * Decode region of the image when we lift finger
        	 */
          	mService.loadBitmap(null);
        }
        /*
         * This slows downs panning when zoomed in
         */
        return mMultiTouchC.onTouchEvent(e);
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
                    newObjPosAndScale.getYOff(),
                    -(mService.getBitmapHolder().getWidth() - mWidth),
                    -(mService.getBitmapHolder().getHeight() - mHeight),
                    0,
                    0);
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
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    public void onDraw(Canvas canvas) {
    	if(mService == null || mService.getBitmapHolder() == null || mService.getBitmapHolder().getBitmap() == null) {
    		return;
    	}
        /*
         * Draw the bitmap
         */
        mService.getBitmapHolder().getTransform().setTranslate(mService.getPan().getDragX(), mService.getPan().getDragY());

        canvas.drawBitmap(mService.getBitmapHolder().getBitmap(), mService.getBitmapHolder().getTransform(), mPaint);
    }
    
    /**
     * 
     */
    public void setService(StorageService service) {
    	mService = service;
    }
 

	/**
	 * This for finding width / height of this view
	 */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	mWidth = r - l;
    	mHeight = b - t;
        super.onLayout(changed, l, t, r, b);
    }
}

