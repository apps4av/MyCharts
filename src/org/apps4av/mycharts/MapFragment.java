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

import org.apps4av.mycharts.R;
import org.apps4av.mycharts.gps.GpsInterface;
import org.apps4av.mycharts.gps.GpsParams;

import android.app.AlertDialog;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

/**
 * A fragment that shows the map
 */
public class MapFragment extends FragmentWrapper {
    /**
     * The view of this fragment that has the map on it
     *
     */
    private MapView mMapView;

	private ImageButton mLoadButton;
	private ProgressBar mProgressLoading;
	private String mPath;
	
	

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
                mMapView.setGpsParams(new GpsParams(location));               
            }
        }

        @Override
        public void timeoutCallback(boolean timeout) {
            /*
             *  No GPS signal
             *  Tell location view to show GPS status
             */
            if(null == getService()) {
            }
            else if(timeout) {
            }
            else {
                /*
                 *  GPS kicking.
                 */
            }           
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
        
        /*
         * Set service in map view
         */
        mMapView = (MapView)(rootView.findViewById(R.id.fragment_map_plateview));
        mMapView.setService(getService());
        
        
        mProgressLoading = (ProgressBar)rootView.findViewById(R.id.fragment_map_progress_bar);
       
        mLoadButton = (ImageButton)rootView.findViewById(R.id.fragment_map_button_load);
        mLoadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*
				 * Show loading. Image ready callback with hide it
				 * Add dialog to select a file
				 */
				
		        mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		        
		        updateDirectory();
			}
        });

        /**
         * Set image callback for showing image is loaded
         */
        getService().setImageCallback(new ImageCallback() {

			@Override
			public void imageReady() {
				mProgressLoading.setVisibility(View.INVISIBLE);
				mMapView.invalidate();
			}
        	
        });
        
        getService().registerGpsListener(mGpsInfc);
        
        return rootView;
    }

    
    
    /**
     * 
     */
    private void updateDirectory() {
        ArrayList<DirectoryItem> info = Helper.loadFileList(mPath);
		final DirectoryAdapter adapter = new DirectoryAdapter(getActivity(), info);
        ListView list = new ListView(getActivity());
		list.setAdapter(adapter);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
 
		// set title
		alertDialogBuilder.setTitle(getString(R.string.choosemap));
		// set dialog message
		alertDialogBuilder.setView(list);

		// create alert dialog
		final AlertDialog alertDialog = alertDialogBuilder.create();

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				/**
				 * Clicked on directory, go into it
				 */
				DirectoryItem item = (DirectoryItem)arg0.getAdapter().getItem(pos);
				if(item.isDir()) {
					mPath += "/" + item.getName();
					alertDialog.dismiss();
					
					/*
					 * Recurse  into folders
					 */
					updateDirectory();
				}
				else {
					/*
					 * File chosen, load it
					 */
					alertDialog.dismiss();
					mProgressLoading.setVisibility(View.VISIBLE);
					getService().loadBitmap(mPath + "/" + item.getName());
				}
			}
		});

		// show it
		alertDialog.show();

    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getService().unregisterGpsListener(mGpsInfc);
    }

}