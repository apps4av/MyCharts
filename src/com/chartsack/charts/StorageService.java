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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.chartsack.charts.gps.Gps;
import com.chartsack.charts.gps.GpsInterface;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.view.Display;
import android.view.WindowManager;

import com.chartsack.charts.gps.GpsParams;


/**
 * @author zkhan
 * Main storage service. It stores all states so when activity dies,
 * we dont start from no state.

 */
public class StorageService extends Service implements SimpleAsyncTask.Methods {

    /*
     * A bitmap
     */
    private BitmapHolder mBitmap;                            
        
    /**
     * Local binding as this runs in same thread
     */
    private final IBinder binder = new LocalBinder();

    AsyncTask<String, Void, Boolean> mDecodeTask;

    private Pan                      mPan;
    private Scale                    mScale;
    private int                      mWidth;
    private int                      mHeight;
    private ImageCallback			 mICallback;
    private GpsParams				mGpsParams;
    private String                  mChart;
    private double                  mGeoData[];
    private SimpleAsyncTask         mLoadTask;

    private boolean mIsGpsOn;
    
    private int mCounter;

    /**
     * For performing periodic activities.
     */
    private Timer mTimer;
    

    /*
     * A list of GPS listeners
     */
    private LinkedList<GpsInterface> mGpsCallbacks;

    /**
     * GPS
     */
    private Gps mGps;

    /**
     * @author zkhan
     *
     */
    public class LocalBinder extends Binder {
        /**
         * @return
         */
        public StorageService getService() {
            return StorageService.this;
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }
    
    /* (non-Javadoc)
     * @see android.app.Service#onUnbind(android.content.Intent)
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
          
        super.onCreate();
        
        mBitmap = null;
        
        mPan = new Pan();
        mScale = new Scale();
        mGeoData = new double[4];
        
        Location l = Gps.getLastLocation(getApplicationContext());
        mGpsParams = new GpsParams(l);

        /*
         * Get width / height
         */
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;
        mHeight = size.y;
        
        mGpsCallbacks = new LinkedList<GpsInterface>();
        mTimer = new Timer();
        TimerTask gpsTime = new UpdateTask();
        mIsGpsOn = false;

        /*
         * Monitor TFR every hour.
         */
        mTimer.scheduleAtFixedRate(gpsTime, 0, 60 * 1000);

        /*
         * Start GPS, and call all activities registered to listen to GPS
         */
        GpsInterface intf = new GpsInterface() {

            /**
             * 
             * @return
             */
            private LinkedList<GpsInterface> extracted() {
                return (LinkedList<GpsInterface>)mGpsCallbacks.clone();
            }

            /*
             * (non-Javadoc)
             * @see com.ds.avare.GpsInterface#statusCallback(android.location.GpsStatus)
             */            
            @Override
            public void statusCallback(GpsStatus gpsStatus) {
                LinkedList<GpsInterface> list = extracted();
                Iterator<GpsInterface> it = list.iterator();
                while (it.hasNext()) {
                    GpsInterface infc = it.next();
                    infc.statusCallback(gpsStatus);
                }
            }

            /*
             * (non-Javadoc)
             * @see com.ds.avare.GpsInterface#locationCallback(android.location.Location)
             */
            @Override
            public void locationCallback(Location location) {                
                LinkedList<GpsInterface> list = extracted();
                Iterator<GpsInterface> it = list.iterator();
                mGpsParams = new GpsParams(location);
                while (it.hasNext()) {
                    GpsInterface infc = it.next();
                    infc.locationCallback(location);
                }
                
                /*
                 * Update the service objects with location
                 */
                if(null != location) {
                    if(!location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                        /*
                         * Getting location from somewhere other than built in GPS.
                         * Update timeout so we do not timeout on GPS timer.
                         */
                        mGps.updateTimeout();
                    }
                }
            }

            /*
             * (non-Javadoc)
             * @see com.ds.avare.GpsInterface#timeoutCallback(boolean)
             */
            @Override
            public void timeoutCallback(boolean timeout) {
                LinkedList<GpsInterface> list = extracted();
                Iterator<GpsInterface> it = list.iterator();
                while (it.hasNext()) {
                    GpsInterface infc = it.next();
                    infc.timeoutCallback(timeout);
                }                
            }

            @Override
            public void enabledCallback(boolean enabled) {
                LinkedList<GpsInterface> list = extracted();
                Iterator<GpsInterface> it = list.iterator();
                while (it.hasNext()) {
                    GpsInterface infc = it.next();
                    infc.enabledCallback(enabled);
                }
                if(enabled) {
                    if(!mGpsCallbacks.isEmpty()) {
                        mGps.start();
                    }
                }
            }
        };
        mGps = new Gps(this, intf);

    }
        
    /* (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
    	
    	try {
    		mBitmap.recycle();
    		mBitmap = null;
    	}
    	catch(Exception e) {
    		
    	}

        if(mTimer != null) {
            mTimer.cancel();
        }
        if(mGps != null) {
            mGps.stop();
        }

        System.gc();

        super.onDestroy();
        
        System.runFinalizersOnExit(true);
        System.exit(0);
    }


    /**
     * 
     * @param gps
     */
    public void registerGpsListener(GpsInterface gps) {
        /*
         * If first listener, start GPS
         */
        mGps.start();
        synchronized(this) {
            mIsGpsOn = true;
        }
        synchronized(mGpsCallbacks) {
            mGpsCallbacks.add(gps);
        }
    }

    /**
     * 
     * @param gps
     */
    public void unregisterGpsListener(GpsInterface gps) {
        
        boolean isempty = false;
        
        synchronized(mGpsCallbacks) {
            mGpsCallbacks.remove(gps);
            isempty = mGpsCallbacks.isEmpty();
        }
        
        /*
         * If no listener, relinquish GPS control
         */
        if(isempty) {
            synchronized(this) {
                mCounter = 0;
                mIsGpsOn = false;                
            }            
        }
    }

    /**
     * 
     */
    public void loadBitmap(String file) {
    	mLoadTask = new SimpleAsyncTask(this);
    	mLoadTask.run(file);
    }

    	
    
    /**
     * 
     * @return
     */
    public BitmapHolder getBitmapHolder() {
        return mBitmap;
    }

    /**
     * 
     * @return
     */
    public Pan getPan() {
    	return mPan;
    }
    
    /**
     * 
     * @return
     */
    public Scale getScale() {
    	return mScale;
    }

    /**
     * 
     * @return
     */
    public GpsParams getGpsParams() {
    	return mGpsParams;
    }

    /**
     * 
     * @return
     */
    public int getWidth() {
    	return mWidth;
    }

    /**
     * 
     * @return
     */
    public int getHeight() {
    	return mHeight;
    }

    /**
     * 
     * @param cb
     */
    public void setImageCallback(ImageCallback cb) {
    	mICallback = cb;
    }
    
    /**
     * @author zkhan
     *
     */
    private class UpdateTask extends TimerTask {
        
        /* (non-Javadoc)
         * @see java.util.TimerTask#run()
         */
        public void run() {

            /*
             * Stop the GPS delayed by 1 to 2 minutes if no other activity is registered 
             * to it for 1 to 2 minutes.
             */
            synchronized(this) {
                mCounter++;
                if((!mIsGpsOn) && (mGps != null) && (mCounter >= 2)) {
                    mGps.stop();
                }
            }

        }
    }

    /**
     * 
     * @return
     */
    public String getChartName() {
    	return mChart;
    }

    /**
     * 
     * @param name
     */
    public void setChartName(String name) {
    	mChart = name;
    }

    /**
     * 
     * @param name
     */
    public boolean setGeotagData(String name) {
    	
    	String tokens[] = name.split(",");
    	if(tokens.length != 4) {
    		return false;
    	}
    	
    	try {
    		mGeoData[0] = Double.parseDouble(tokens[0]);
    		mGeoData[1] = Double.parseDouble(tokens[1]);
    		mGeoData[2] = Double.parseDouble(tokens[2]);
    		mGeoData[3] = Double.parseDouble(tokens[3]);
    	}
    	catch(Exception e) {
    		return false;
    	};
    	return true;
    }

    /**
     * 
     * @return
     */
    public double[] getGeotagData() {
    	return mGeoData;
    }

    /**
     * This loads bitmap in background
     */
	@Override
	public Object background(Object... vals) {
		
		String file = (String)vals[0];
		
        /*
         * Invalid. Nothing to do
         */
        if(file == null && getBitmapHolder() == null) {
        	return (Object)false;
        }
        
        if(file != null) {
        	/*
        	 * 
        	 */
        	if(getBitmapHolder() != null) {
        		getBitmapHolder().recycle();
        	}
            mBitmap = new BitmapHolder(file, getWidth(), getHeight());
        }
        
        if(getBitmapHolder() != null) {
            int x = (int)(-getPan().getMoveX());
            int y = (int)(-getPan().getMoveY()) ;
            int x1 = (int)((x + getWidth()));
            int y1 = (int)((y + getHeight()));
            Rect rect = new Rect(x, y, x1, y1);
            getBitmapHolder().decodeRegion(rect, 1);
        }
		return (Object)true;
	}

	/**
	 * This updates screen with newly loaded bitmap
	 */
	@Override
	public void ui(Object ret) {
        getPan().endDrag();
        if((boolean)ret) {
        	mBitmap.moveToFront();
	        if(mICallback != null) {
	        	mICallback.imageReady();
	        }
        }
	}

}