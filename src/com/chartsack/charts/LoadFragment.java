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

import com.chartsack.charts.R;
import com.chartsack.charts.gps.GpsInterface;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * A fragment that shows the map
 */
public class LoadFragment extends FragmentWrapper {

    private Button mLoadButton;
    private ProgressBar mProgressLoading;
    private String mPath;
    private TextView mTextChart;
    private TextView mTextGeo;

    public LoadFragment() {
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
            }
        }

        @Override
        public void timeoutCallback(boolean timeout) {
            if(timeout) {
            }
        }

        @Override
        public void enabledCallback(boolean enabled) {
            if(!enabled) {
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_load, container,
                false);
        
        
        mProgressLoading = (ProgressBar)rootView.findViewById(R.id.fragment_load_progress_bar);
        mTextChart = (TextView)rootView.findViewById(R.id.fragment_load_text_status_value);
        mTextGeo = (TextView)rootView.findViewById(R.id.fragment_load_text_geostatus_value);
       
        mLoadButton = (Button)rootView.findViewById(R.id.fragment_load_button_load);
        mLoadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /*
                 * Add dialog to select a file from Download folder
                 */
                
                mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                
                updateDirectory();
            }
        });


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
        alertDialogBuilder.setTitle(getString(R.string.choose_map));
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

                    getService().setChartName(item.getName());
                    String name = mPath + "/" + item.getName();

                    getService().loadBitmap(name);
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
    

    /**
     * 
     */
    @Override
    public void onResume() {
        super.onResume();
        loadTagData();
    }

    /**
     * 
     */
    private void loadTagData() {
        if(null == getService().getChartName()) {
            mTextChart.setText("");
            mTextGeo.setText("");
            return;
        }
        else {
            mTextChart.setText(getService().getChartName());                        
        }
        TagData provider = new TagData(getActivity());
        String data = provider.getTag(getService().getChartName());
        Projection p = new Projection(data);
        if(null == data || (!p.isValid())) {
            showHelp(getString(R.string.map_help_tag));
            mTextGeo.setText(getActivity().getString(R.string.no));
        }
        else {
        	getService().setGeotagData(p);
            mTextGeo.setText(getActivity().getString(R.string.yes));                        
        }   
    }
    
    /**
     * 
     */
    @Override
    public void setService(StorageService service) {
        
        super.setService(service);
        
        getService().registerGpsListener(mGpsInfc);
        
        super.setService(service);
        
        /**
         * Set image callback for showing image is loaded
         */
        getService().setImageCallback(new ImageCallback() {

            @Override
            public void imageReady() {
                if(mProgressLoading.getVisibility() == View.VISIBLE) {
                    /*
                     * Loaded. Get geotag data
                     */
                    loadTagData();
                }
                /*
                 * Hide progress
                 */
                mProgressLoading.setVisibility(View.INVISIBLE);
            }
        });

    }
}