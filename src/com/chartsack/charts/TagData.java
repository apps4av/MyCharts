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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * 
 * @author zkhan
 *
 */
public class TagData {

	private Context mContext;
	
	/**
	 * 
	 * @param ctx
	 */
	public TagData(Context ctx) {
		mContext = ctx;
	}

	/**
	 * 
	 * @param name
	 * @param tag
	 */
	public void addTag(String name, String tag) {
		
		ContentValues values = new ContentValues();
		values.put(DataProvider.NAME,  name);
		values.put(DataProvider.DATA,  tag);
		
		mContext.getContentResolver().insert(DataProvider.CONTENT_URI, values);
	}
	
	/**
	 * 
	 * @param name
	 */
	public void deleteTag(String name) {
		String args[] = new String[1];
		args[0] = name;
		mContext.getContentResolver().delete(DataProvider.CONTENT_URI, DataProvider.NAME + " = ?", args);
	}

	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public String getTag(String name) {
		String args[] = new String[1];
		args[0] = name;
		Cursor c = mContext.getContentResolver().query(DataProvider.CONTENT_URI, null, DataProvider.NAME + " = ?", args, null);
		if(c != null && c.getCount() != 0) {
			if(c.moveToFirst()) {
				String data = c.getString(c.getColumnIndex(DataProvider.DATA));
				c.close();
				return data;
			}
		}
		return null;
	}
}
