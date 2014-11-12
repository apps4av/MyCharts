/*
Copyright (c) 2012, Apps4Av Inc. (apps4av.com) 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.chartsack.charts;

import java.util.Observable;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
/**
*
* @author zkhan
*
*/
public class AddressToGps extends Observable {
	AddressTask mAddressTask;
	/**
	* Start a task to get resolution
	* @param ctx
	* @param address
	* @return
	*/
	public Location get(Context ctx, String address) {
		if(null != mAddressTask) {
			mAddressTask.cancel(true);
		}
		mAddressTask = new AddressTask();
		mAddressTask.execute(ctx, address);
		return null;
	}
	
	/**
	* @author zkhan
	*
	*/
	private class AddressTask extends AsyncTask<Object, String, Object> {
		/* (non-Javadoc)
		* @see android.os.AsyncTask#doInBackground(Params[])
		*/
		@Override
		protected Object doInBackground(Object... vals) {
			Thread.currentThread().setName("Address");
			return Util.getGeoPoint((String)vals[1]);
		}
		
		/* (non-Javadoc)
		* @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		*/
		@Override
		protected void onPostExecute(Object arg) {
			/*
			* Set list view as return address list
			*/
			AddressToGps.this.setChanged();
			AddressToGps.this.notifyObservers(arg);
		}
	}
}
