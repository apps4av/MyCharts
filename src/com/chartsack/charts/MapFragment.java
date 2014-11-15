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
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import com.chartsack.charts.R;
import com.chartsack.charts.gps.GpsInterface;
import com.chartsack.charts.gps.GpsParams;

import android.location.Address;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * A fragment that shows the map
 */
public class MapFragment extends FragmentWrapper implements Observer, SimpleAsyncTask.Methods {
    /**
     * The view of this fragment that has the map on it
     *
     */
    private MapView mMapView;

	private ImageButton mCenterButton;
	
	private AddressToGps mAddressResolver;
	
	private List<Address> mAList;
	
	private ListView mList;

	private EditText mText;
	
	private static final int SEARCH_MIN_LENGTH = 4;

	
    public MapFragment() {
    }

    /*
     * Gets GPS location
     */
    
    private GpsInterface mGpsInfc = new GpsInterface() {

        @Override
        public void statusCallback(GpsStatus gpsStatus) {
        }

        @Override
        public void locationCallback(Location location) {
            if(location != null) {

                /*
                 * Called by GPS. Update everything driven by GPS.
                 */
                mMapView.postInvalidate();               
            }
        }

        @Override
        public void timeoutCallback(boolean timeout) {
        }

        @Override
        public void enabledCallback(boolean enabled) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container,
                false);

        mList = (ListView)rootView.findViewById(R.id.fragment_map_list);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        if(null != mAList) {
			        Util.hideKeyboard(mText);
		        	/*
		        	 * Get address, center on it
		        	 */
		        	Address a = mAList.get(position);
		        	mText.setText("");
		        	SimpleAsyncTask task = new SimpleAsyncTask(MapFragment.this);
		        	task.run(a);
		        }
	        }
        });

        
        mText = (EditText)rootView.findViewById(R.id.fragment_map_address);
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

        mCenterButton = (ImageButton)rootView.findViewById(R.id.fragment_map_button_center);
        mCenterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*
				 * center on current position
				 */
				if(getService() == null) {
					return;
				}

				GpsParams param = getService().getGpsParams();
				Address a = new Address(Locale.getDefault());
		        a.setLongitude(param.getLongitude());
		        a.setLatitude(param.getLatitude());
		        
		        centerOnLocation(a);
			}
        });

        mAddressResolver = new AddressToGps();
        mAddressResolver.addObserver(this);        
        
        /*
         * Set service in map view
         */
        mMapView = (MapView)(rootView.findViewById(R.id.fragment_map_plateview));
        mMapView.setService(getService());
        
        return rootView;
    }

    /**
     * 
     */
    @Override
    public void onResume() {
       super.onResume();
       mMapView.setCoordinates(getService().getGeotagData());
       mMapView.postInvalidate();
    }
    
    /**
     * 
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    	getService().unregisterGpsListener(mGpsInfc);
    }

    /**
     * 
     */
    @Override
    public void setService(StorageService service) {
    	
    	super.setService(service);
    	
        getService().registerGpsListener(mGpsInfc);
        
        /**
         * Set image callback for showing image is loaded
         */
        getService().setImageCallback(new ImageCallback() {

			@Override
			public void imageReady() {
				mMapView.invalidate();
			}
        });
    }

	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
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

	/**
	 * 
	 * @param a
	 */
	private void centerOnLocation(Address a) {
		if(!mMapView.centerOnChart(a.getLongitude(), a.getLatitude())) {
			showHelp(getString(R.string.not_on_chart));
		}
	}
	

	/**
	 * 
	 */
	@Override
	public Object background(Object... vals) {
    	/*
    	 * Save address for offline search
    	 */
		AddressData ad = new AddressData(getActivity());
		ad.deleteAddress((Address)vals[0]);
		ad.addAddress((Address)vals[0]);
		return vals[0];
	}

	/**
	 * 
	 */
	@Override
	public void ui(Object ret) {
		/*
		 * Update chart in handler
		 */
		centerOnLocation((Address)ret);
	}
}