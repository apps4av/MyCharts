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

import java.util.LinkedList;

import android.location.Address;

/**
 * A projection coordinate system for lon/lat/x/y
 * @author zkhan
 *
 */
public class Projection {

	private Multivariate mInterpX;
	private Multivariate mInterpY;
	private Multivariate mInterpLon;
	private Multivariate mInterpLat;
	private LinkedList<ProjectionPoint> mPoints;
	private String mTag;
	private boolean mValid;
	
	public static final int POINTS = 3;

	/**
	 * Four projections are need to convert between x/y and lon/lat
	 */
	public Projection() {
		mPoints = new LinkedList<ProjectionPoint>();
		mInterpX = null;
		mInterpY = null;
		mInterpLon = null;
		mInterpLat = null;
		mTag = "";
		mValid = false;
	}

	/**
	 * This makes projection from db tag text
	 * @param tag
	 */
	public Projection(String tag) {
		
		mPoints = new LinkedList<ProjectionPoint>();
		mInterpX = null;
		mInterpY = null;
		mInterpLon = null;
		mInterpLat = null;
		mTag = "";
		mValid = false;

		/**
		 * 
		 */
		if(null == tag) {
			return;
		}
		
        String tokens[] = tag.split(",");
        if(tokens.length != 12) {
            return;
        }
        
        try {
        	//x0, y0, lon0, lat0
        	//x1, y1, lon1, lat1
        	//x2, y2, lon2, lat2
        	addPoint(
        			Double.parseDouble(tokens[0]),
        			Double.parseDouble(tokens[1]),
        			Double.parseDouble(tokens[2]),
        			Double.parseDouble(tokens[3]));
        	addPoint(
        			Double.parseDouble(tokens[4]),
        			Double.parseDouble(tokens[5]),
        			Double.parseDouble(tokens[6]),
        			Double.parseDouble(tokens[7]));
        	addPoint(
        			Double.parseDouble(tokens[8]),
        			Double.parseDouble(tokens[9]),
        			Double.parseDouble(tokens[10]),
        			Double.parseDouble(tokens[11]));
        }
        catch(Exception e) {
        };
	}

	
	/**
	 * This makes projection from address points
	 * @param tag
	 */
	public Projection(Address address0, Address address1, Address address2) {
		
		mPoints = new LinkedList<ProjectionPoint>();
		mInterpX = null;
		mInterpY = null;
		mInterpLon = null;
		mInterpLat = null;
		mTag = "";
		mValid = false;

		/**
		 * Address's feature name must be x,y format
		 */
		if(null == address0 || null == address1 || null == address2) {
			return;
		}

        double x0, x1, x2, y0, y1, y2, lon0, lon1, lon2, lat0, lat1, lat2;
        String tokens0[];
        String tokens1[];
        String tokens2[];
        lon0 = address0.getLongitude();
        lat0 = address0.getLatitude();
        lon1 = address1.getLongitude();
        lat1 = address1.getLatitude();
        lon2 = address2.getLongitude();
        lat2 = address2.getLatitude();
        tokens0 = address0.getFeatureName().split(",");
        tokens1 = address1.getFeatureName().split(",");
        tokens2 = address2.getFeatureName().split(",");
        
        // get all data entered by user together for processing
        try {
            x0 = Double.parseDouble(tokens0[0]);
            y0 = Double.parseDouble(tokens0[1]);
            x1 = Double.parseDouble(tokens1[0]);
            y1 = Double.parseDouble(tokens1[1]);
            x2 = Double.parseDouble(tokens2[0]);
            y2 = Double.parseDouble(tokens2[1]);
        }
        catch(Exception e) {
        	return;
        }
        addPoint(x0, y0, lon0, lat0);
        addPoint(x1, y1, lon1, lat1);
        addPoint(x2, y2, lon2, lat2);
	}

	/**
	 * Add a point to the list of points required for projection
	 * @param x
	 * @param y
	 * @param lon
	 * @param lat
	 */
	public void addPoint(double x, double y, double lon, double lat) {
		mPoints.add(new ProjectionPoint(x, y, lon, lat));
		if(mPoints.size() == POINTS) {
			ProjectionPoint p0 = mPoints.get(0);
			ProjectionPoint p1 = mPoints.get(1);
			ProjectionPoint p2 = mPoints.get(2);
			// calculate 4 interpolations
			mInterpLon = new Multivariate(p0.mX, p0.mY, p0.mLon, p1.mX, p1.mY, p1.mLon, p2.mX, p2.mY, p2.mLon);
			mInterpLat = new Multivariate(p0.mX, p0.mY, p0.mLat, p1.mX, p1.mY, p1.mLat, p2.mX, p2.mY, p2.mLat);
			mInterpX   = new Multivariate(p0.mLon, p0.mLat, p0.mX, p1.mLon, p1.mLat, p1.mX, p2.mLon, p2.mLat, p2.mX);
			mInterpY   = new Multivariate(p0.mLon, p0.mLat, p0.mY, p1.mLon, p1.mLat, p1.mY, p2.mLon, p2.mLat, p2.mY);
			// make a db tag
			mTag =   
            		p0.mX + "," + p0.mY + "," + p0.mLon + "," + p0.mLat + "," +
            		p1.mX + "," + p1.mY + "," + p1.mLon + "," + p1.mLat + "," +
            		p2.mX + "," + p2.mY + "," + p2.mLon + "," + p2.mLat;
			
			
	        double lonTopLeft = getLon(0, 0);
	        double latTopLeft = getLat(0, 0);
	        
	        /**
	         * Save data if sane
	         */
	        if(Util.isLongitudeSane(lonTopLeft) && Util.isLatitudeSane(latTopLeft)) {
	        	mValid = true;
	        }
		}
	}

	/**
	 * Get x coordinate from lon, lat
	 * @param lon
	 * @param lat
	 * @return
	 */
	public double getX(double lon, double lat) {
		return mInterpX.interpolate(lon, lat);
	}

	/**
	 * Get y coordinate from lon, lat
	 * @param lon
	 * @param lat
	 * @return
	 */
	public double getY(double lon, double lat) {
		return mInterpY.interpolate(lon, lat);
	}

	/**
	 * Get lon from x, y
	 * @param lon
	 * @param lat
	 * @return
	 */
	public double getLon(double x, double y) {
		return mInterpLon.interpolate(x, y);
	}

	/**
	 * Get a tag to store in db
	 * @return
	 */
	public String getTag() {
		return mTag;
	}

	/**
	 * Get lat from x, y
	 * @param lon
	 * @param lat
	 * @return
	 */
	public double getLat(double x, double y) {
		return mInterpLat.interpolate(x, y);
	}

	/**
	 * If this projection is valid
	 * @return
	 */
	public boolean isValid() {
		return mValid;
	}
	
	/**
	 * Just to store a point for geo projection
	 * @author zkhan
	 *
	 */
	private class ProjectionPoint {
		public double mX;
		public double mY;
		public double mLat;
		public double mLon;
		
		/**
		 * 
		 * @param x
		 * @param y
		 * @param lon
		 * @param lat
		 */
		public ProjectionPoint(double x, double y, double lon, double lat) {
			mX = x;
			mY = y;
			mLon = lon;
			mLat = lat;
		}
	}
}
