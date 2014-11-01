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

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;

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
     * 
     */
    private Bitmap mBitmap = null;
    
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
    
    private ExifInterface mData;
    
    /**
     * Android does not free memory for a bitmap. Have to call this explicitly
     * especially for large bitmaps
     */
    public void recycle() {
        if(null != mBitmap) {
            mBitmap.recycle();
        }
        mBitmap = null;
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
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;        
            opt.inBitmap = mBitmap;
            opt.inSampleSize = sampleSize;
            mBitmap = mDecoder.decodeRegion(rect, opt);
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
        }
        catch(OutOfMemoryError e){
        }


        mName = name;
        try {
            mData = new ExifInterface(name);
        }
        catch(Exception e){
        }
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
        return mBitmap;
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
    
    /**
     * 
     * @return
     */
    public String getData() {
    	return mData.getAttribute("Apps4Av");
    }
    
    /**
     * 
     * @return
     */
    public void setData(String data) {
    	mData.setAttribute("Apps4Av", data);
    }
}
