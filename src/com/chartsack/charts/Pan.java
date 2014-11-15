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


/**
 * 
 * @author zkhan
 *
 */
public class Pan {

    /**
     * Current x mMovement pan
     */
    private float                      mMoveX;
    /**
     * Current y mMovement pan
     */
    private float                      mMoveY;

    /**
     * Current x mMovement pan
     */
    private float                      mMoveXLast;
    /**
     * Current y mMovement pan
     */
    private float                      mMoveYLast;

    /**
     * 
     */
    private float                      mDragX;
    private float                      mDragY;

    /**
     * 
     */
    public Pan() {
        mMoveX = 0;
        mMoveY = 0;
        mDragX = 0;
        mDragY = 0;
        mMoveXLast = 0;
        mMoveYLast = 0;
    }

    
    /**
     * 
     * @param x
     * @param y
     * @return
     */
    public boolean setMove(float x, float y, float minx, float miny, float maxx, float maxy) {
        boolean ret = true;
        /*
         * Limit pan to map size
         */
        if(x > maxx) {
        	ret = false;
            x = maxx;
        }
        if(y > maxy) {
        	ret = false;
            y = maxy;
        }
        if(x < minx) {
        	ret = false;
            x = minx;
        }
        if(y < miny) {
        	ret = false;
            y = miny;
        }
        mMoveX = x;
        mMoveY = y;
        
        mDragX = mMoveX - mMoveXLast; 
        mDragY = mMoveY - mMoveYLast;
        return ret;
    }

    /**
     * 
     */
    public void endDrag() {
       mDragX = mDragY = mMoveXLast = mMoveYLast = 0; 
    }

    /**
     * 
     */
    public void startDrag() {
        mMoveXLast = mMoveX; 
        mMoveYLast = mMoveY; 
    }

    /**
     * 
     * @return
     */
    public float getMoveX() {
        return mMoveX;
    }

    /**
     * 
     * @return
     */
    public float getMoveY() {
        return mMoveY;
    }

    /**
     * 
     * @return
     */
    public float getDragX() {
        return mDragX;
    }

    /**
     * 
     * @return
     */
    public float getDragY() {
        return mDragY;
    }

}
