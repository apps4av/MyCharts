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


import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apps4av.mycharts.R;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ListView;

/**
 * A fragment that shows the map
 */
public class TagFragment extends FragmentWrapper {
    /**
     * The view of this fragment that has the map on it
     *
     */
    private TagView mTagView;

	private Button mTagButton;
	private AddressToGps mAddressResolver;
	private Address mNotifyAddress;
	private AlertDialog mDialogSearch;

    public TagFragment() {
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tag, container,
                false);
        
        
        /*
         * Set service in map view
         */
        mTagView = (TagView)(rootView.findViewById(R.id.fragment_tag_plateview));
        mTagView.setService(getService());
        
        
        mTagButton = (Button)rootView.findViewById(R.id.fragment_tag_button_tag);
        mTagButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*
				 * Make dialog
				 */
				ObserverAlertDialogBuilder alertDialogBuilder = new ObserverAlertDialogBuilder(getActivity());
				 
				/*
				 * Make dialog the listener to address from internet
				 */
				mAddressResolver.addObserver(alertDialogBuilder);

				// create alert dialog
				mDialogSearch = alertDialogBuilder.create();

				// show it
				mDialogSearch.show();
			}
        });


        mAddressResolver = new AddressToGps();
           
        return rootView;
    }

    
    /**
     * 
     * @author zkhan
     *
     */
    private class ObserverAlertDialogBuilder extends AlertDialog.Builder implements Observer {

    	private EditText mText = new EditText(getActivity());
		private ListView mList = new ListView(getActivity());
		private List<Address> mAList;
		
		private static final int SEARCH_MIN_LENGTH = 4;

		/**
		 * 
		 * @param context
		 */
        protected ObserverAlertDialogBuilder(Context context) {
        	
			super(context);
			
			/*
			 * New search
			 */
			mNotifyAddress = null;
			
			/*
			 * Make a layout for a dialog that asks for tagging
			 */
			LinearLayout layout = new LinearLayout(getActivity());
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
				        mNotifyAddress = mAList.get(position);
				        Util.hideKeyboard(mText);
				        try {
				        	mDialogSearch.dismiss();
				        }
				        catch (Exception e) {
				        	
				        }
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
	     		    	mAddressResolver.get(getActivity(), s.toString());
	     		    }
	     		    else {
	     		    	mList.setAdapter(null);
	     		    }
	     	    }
	         });

			// set title
			setTitle(getString(R.string.search));
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
    			 final ArrayAdapter adapter = new ArrayAdapter(getActivity(),
    					 android.R.layout.simple_list_item_1, alist);
    			 mList.setAdapter(adapter);
    			 if(mText.toString().length() > SEARCH_MIN_LENGTH) {
	     		    	mList.setVisibility(View.VISIBLE);
    			 }
    		}		
    	}
    	
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 
     */
    @Override
    public void setService(StorageService service) {
    	
    	super.setService(service);
    	
        /**
         * Set image callback for showing image is loaded
         */
        getService().setImageCallback(new ImageCallback() {

			@Override
			public void imageReady() {
				mTagView.invalidate();
			}
        });
    }
}