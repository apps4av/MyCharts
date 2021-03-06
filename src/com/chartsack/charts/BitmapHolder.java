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

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * @author zkhan
 * This class hides all details of handling a bitmap
 */
public class BitmapHolder {
    
    /**
     * 
     */
    private BitmapRegionDecoder mDecoder;
    
    /**
     * This bitmap is the work bitmap
     */
    private Bitmap mBitmap = null;
    
    /*
     * Front one serves as double buffer.
     */
    private Bitmap mBitmapFront = null;
    
    /**
     * 
     */
    private String mName = null;

    /**
     * Transform for scale/translate
     */
    private Matrix mTransform = new Matrix();

    private int mWidth = 0;
    private int mHeight = 0;
    
    /**
     * Android does not free memory for a bitmap. Have to call this explicitly
     * especially for large bitmaps
     */
    public void recycle() {
        if(null != mBitmap) {
            mBitmap.recycle();
        }
        if(null != mBitmapFront) {
            mBitmapFront.recycle();
        }
        mBitmap = null;
        mBitmapFront = null;
        mName = null;
        mWidth = 0;
        mHeight = 0;
    }

    /**
     * 
     * @param rect
     */
    public void decodeRegion(Rect rect, int sampleSize) {
        
        if(null != mDecoder && mName != null && mBitmap != null) {
            mBitmap.eraseColor(0);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;        
            opt.inBitmap = mBitmap;
            opt.inSampleSize = sampleSize;
            try {
                mBitmap = mDecoder.decodeRegion(rect, opt);
            }
            catch(Exception e) {
                /*
                 * Out of region exception
                 */
            }
        }
    }
    
    /**
     * @param name
     * Get bitmap from a diagram / plate file
     */
    public BitmapHolder(String name, int width, int height) {
        mName = null;
        if(!(new File(name).exists())) {
            return;
        }
                
        /*
         * Bitmap dims without decoding
         */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(name, options);
        mWidth = options.outWidth;
        mHeight = options.outHeight;

        /*
         * User region decoder. This takes some time to load the picture, do in BG tasks
         */
        try {
            mDecoder = BitmapRegionDecoder.newInstance(name, true); 
        }
        catch(Exception e){
            return;
        }
        
        Bitmap.Config conf = Bitmap.Config.RGB_565;
        try {
            mBitmap = Bitmap.createBitmap(width, height, conf);
            mBitmap.setDensity(Bitmap.DENSITY_NONE);
            mBitmapFront = Bitmap.createBitmap(width, height, conf);
            mBitmapFront.setDensity(Bitmap.DENSITY_NONE);
        }
        catch(Exception e){
        }


        mName = name;
    }
    
    /**
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     * @return
     */
    public Bitmap getBitmap() {
        return mBitmapFront;
    }

    /**
     * 
     */
    public void moveToFront() {
    	if(null == mBitmapFront || null == mBitmap) {
    		return;
    	}
        /*
         * Draw from working to front
         */
        Canvas canvas = new Canvas(mBitmapFront);
        canvas.drawBitmap(mBitmap, 0, 0, new Paint());
    }
    
    /**
     * 
     * @return
     */
    public Matrix getTransform() {
        return mTransform;
    }

    
    /**
     * @return
     */
    public int getWidth() {
        return mWidth;
    }
    
    /**
     * @return
     */
    public int getHeight() {
        return mHeight;
    }
    
}
