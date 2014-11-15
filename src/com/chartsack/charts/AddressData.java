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

import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;

/**
 * 
 * @author zkhan
 *
 */
public class AddressData {

	private Context mContext;
	
	/**
	 * 
	 * @param ctx
	 */
	public AddressData(Context ctx) {
		mContext = ctx;
	}

	/**
	 * 
	 * @param name
	 * @param tag
	 */
	public void addAddress(Address a) {
		
		ContentValues values = new ContentValues();
		values.put(AddressProvider.ADDRESS, a.getAddressLine(0));
		values.put(AddressProvider.LONGITUDE, a.getLongitude());
		values.put(AddressProvider.LATITUDE, a.getLatitude());
		
		mContext.getContentResolver().insert(AddressProvider.CONTENT_URI, values);
	}
	
	/**
	 * 
	 * @param name
	 */
	public void deleteAddress(Address a) {
		String args[] = new String[1];
		args[0] = a.getAddressLine(0);
		mContext.getContentResolver().delete(AddressProvider.CONTENT_URI, AddressProvider.ADDRESS + " = ?", args);
	}

	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Address[] getAddress(String address) {
		Address adds[] = null;
		String args[] = new String[1];
		args[0] = "%" + address + "%";
		Cursor c = mContext.getContentResolver().query(AddressProvider.CONTENT_URI, null, AddressProvider.ADDRESS + " LIKE ?", args, null);
		if(c != null && c.getCount() != 0) {
			adds = new Address[c.getCount()];
			if(c.moveToFirst()) {
				int count = 0;
				do {
				
					/*
					 * Find addresses
					 */
					String data = c.getString(c.getColumnIndex(AddressProvider.ADDRESS));
					double lon = c.getDouble(c.getColumnIndex(AddressProvider.LONGITUDE));
					double lat = c.getDouble(c.getColumnIndex(AddressProvider.LATITUDE));
					Address a = new Address(Locale.getDefault());
					a.setLongitude(lon);
					a.setLatitude(lat);
					a.setAddressLine(0, data);
					adds[count++] = a;
				}
				while(c.moveToNext());
				c.close();
			}
		}
		return adds;
	}
}
