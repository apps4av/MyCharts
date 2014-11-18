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

import android.graphics.Rect;

public class Bounds {

	private int mCoords[];
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @param scale
	 */
	public Bounds(Pan pan, Scale scale, int width, int height) {
		int s = scale.getScaleFactor();
		mCoords = new int[6];
		// Describe what is shown on screen with its center
		    	
    	// find center of screen
    	int x = (int)(-pan.getMoveX() + width / 2) * s;
        int y = (int)(-pan.getMoveY() + height / 2) * s;
        
        // find top coord
        int x0 = x - (width / 2 * s);
        int y0 = y - (height / 2 * s);
        
        // find bottom coord
        int x1 = x + (width / 2 * s);
        int y1 = y + (height / 2 * s);
        
        mCoords[0] = x0;
        mCoords[1] = y0;
        mCoords[2] = x1;
        mCoords[3] = y1;
        mCoords[4] = x;
        mCoords[5] = y;
	}

	/**
	 * 
	 * @return
	 */
	public Rect getRect() {
		return new Rect(mCoords[0], mCoords[1], mCoords[2], mCoords[3]);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCenterX() {
		return -mCoords[4];
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCenterY() {
		return -mCoords[5];
	}
}
