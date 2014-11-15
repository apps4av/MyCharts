package com.chartsack.charts;

/*
Copyright (c) 2012, Apps4Av Inc. (apps4av.com) 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, LONGITUDE, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


import android.os.AsyncTask;

/**
 * A generic asynctask class with cancel
 * @author zkhan
 *
 */
public class SimpleAsyncTask {

	/**
	 * 
	 * @author zkhan
	 *
	 */
	public interface Methods {
        public Object background(Object... vals);
        public void ui(Object ret);
    }
	

	private static AsyncTask<Object, Object, Object> mTask = null;
	private Methods mMethods;
	
	/**
	 * 
	 * @param methods
	 */
	public SimpleAsyncTask(Methods methods) {
		
		if(mTask != null) {
            if(mTask.getStatus() != AsyncTask.Status.FINISHED) {
                mTask.cancel(true);
            }
        }
		
		mMethods = methods;
		
	}

	/**
	 * 
	 * @param params
	 */
	public void run(Object... params) {
	    mTask = new AsyncTask<Object, Object, Object>() {

	        @Override
	        protected Object doInBackground(Object... vals) {
	        	return mMethods.background(vals);
	        }
	        
            @Override
            protected void onPostExecute(Object result) {
            	mMethods.ui(result);
            }
	    };
	    mTask.execute(params);		
	}
}
