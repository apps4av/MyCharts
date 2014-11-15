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

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * A dialog that processes list to get addresses 
 * @author zkhan
 *
 */
public class ObserverAlertDialogBuilder extends AlertDialog.Builder implements Observer {

	private EditText mText;
	private ListView mList;
	private List<Address> mAList;
	private Context mContext;
	private AddressToGps mAddressResolver;
	
	private static final int SEARCH_MIN_LENGTH = 4;

	private Methods mMethods;
	
	/**
	 * 
	 * @author zkhan
	 *
	 */
	public interface Methods {
		public void onItemSelected(Address a);
	}
	
	/**
	 * 
	 * @param context
	 */
    protected ObserverAlertDialogBuilder(Context context, Methods process, String message) {
    	
		super(context);
		
		mText = new EditText(context);
		mList = new ListView(context);
		
		mContext = context;
		
		mMethods = process;
		
		mAddressResolver = new AddressToGps();
		mAddressResolver.addObserver(this);
		
		/*
		 * Make a layout for a dialog that asks for tagging
		 */
		LinearLayout layout = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(params);
		mText.setLayoutParams(params);
		mList.setLayoutParams(params);
		layout.addView(mText);
		layout.addView(mList);

		/*
		 * Add listener for item selected
		 */
		
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        if(null != mAList) {
			        Util.hideKeyboard(mText);
			        mMethods.onItemSelected(mAList.get(position));
		        }
		    }
        });

		/*
		 * Add listener for search text
		 */
		mText.addTextChangedListener(new TextWatcher() {
     	    @Override
     	    public void afterTextChanged(Editable arg0) {
     	    }
     	    @Override
     	    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
     	    }
     	    @Override
     	    public void onTextChanged(CharSequence s, int start, int before, int after) {
     		    if(s.length() > SEARCH_MIN_LENGTH) {
     		    	mAddressResolver.get(mContext, s.toString());
     		    }
     		    else {
     		    	mList.setAdapter(null);
     		    }
     	    }
         });

		// set title, message of dialog
		setTitle(mContext.getString(R.string.find));
		setMessage(message);
		// set dialog message
		setView(layout);
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof List<?>) {
			
			 /*
			  * Save the list of addresses
			  */
			 mAList = (List<Address>)data;
			 final ArrayList<String> alist = new ArrayList<String>();
			 for(Address a : mAList) {
				 alist.add(a.getAddressLine(0));
			 }
			 /*
			  * Present in the dialog
			  */
			 final ArrayAdapter adapter = new ArrayAdapter(mContext,
					 android.R.layout.simple_list_item_1, alist);
			 mList.setAdapter(adapter);
			 if(mText.toString().length() > SEARCH_MIN_LENGTH) {
     		    	mList.setVisibility(View.VISIBLE);
			 }
		}		
	}

}
