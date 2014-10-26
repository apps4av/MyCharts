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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Display;
import android.view.WindowManager;


/**
 * @author zkhan
 * Main storage service. It stores all states so when activity dies,
 * we dont start from no state.

 */
public class StorageService extends Service {

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
    private Thread                   mThread;
    private int                      mWidth;
    private int                      mHeight;
    ImageThread                 	 mImageThread;
    private ImageCallback			 mICallback;


    
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
        
        /*
         * Get width / height
         */
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        /*
         * Our image thread
         */
        mImageThread = new ImageThread();
        mThread = new Thread(mImageThread);
        mThread.start();
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
        
        System.gc();

        super.onDestroy();
        
        System.runFinalizersOnExit(true);
        System.exit(0);
    }

  
    /**
     * 
     */
    public void loadBitmap(String file) {
    	
    	mImageThread.file = file;
    	mThread.interrupt();
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
    public void setBitmap(BitmapHolder b) {
        mBitmap = b;
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
	 * 
	 * @author zkhan
	 *
	 */
	class ImageThread implements Runnable {

        public boolean running = true;
        private boolean runAgain = false;
        public String file = null;
        
        /**
         * Typical value clamper
         * @param input
         * @param min
         * @param max
         */
        private float clamp(float input, float min, float max) {
        	if(input < min) {
        		input = min;
        	}
        	if(input > max) {
        		input = max;
        	}
        	
        	return input;
        }
        
        public void run() {
        	
            while(running) {
                
            	/*
            	 * Sleep here till we are told to load another region, or a new file
            	 */
                if(!runAgain) {
                    try {
                        Thread.sleep(1000 * 3600);
                    }
                    catch(Exception e) {
                        
                    }
                }
                runAgain = false;
                
                /*
                 * Invalid. Nothing to do
                 */
                if(file == null && getBitmapHolder() == null) {
                	continue;
                }
                
                if(file != null) {
                	/*
                	 * 
                	 */
                	if(getBitmapHolder() != null) {
                		getBitmapHolder().recycle();
                	}
	                setBitmap(new BitmapHolder(file, getWidth(), getHeight()));
                }
                
                if(getBitmapHolder() != null) {
                    int x = (int)clamp(-(getPan().getMoveX()), 0, getBitmapHolder().getWidth());
                    int y = (int)clamp(-(getPan().getMoveY()), 0, getBitmapHolder().getHeight());
                    int x1 = (int)clamp(x + getWidth(), 0, getBitmapHolder().getWidth());
                    int y1 = (int)clamp(y + getHeight(), 0, getBitmapHolder().getHeight());
                    Rect rect = new Rect(x, y, x1, y1);

                    getBitmapHolder().decodeRegion(rect, 1);
                    
                    /*
                     * Post results in handler
                     */
                }
                Message m = mHandler.obtainMessage();
                mHandler.sendMessage(m);

        	}
        }
        
        /**
         * This leak warning is not an issue if we do not post delayed messages, which is true here.
         */
        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                /*
                 * Load new image region
                 */
                getPan().endDrag();
                if(mICallback != null) {
                	mICallback.imageReady();
                }            	
            }
        };
	}
}