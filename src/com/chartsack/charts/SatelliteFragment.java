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


import com.chartsack.charts.R;

import com.chartsack.charts.gps.GpsInterface;

import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment that shows the map
 */
public class SatelliteFragment extends FragmentWrapper {
    /**
     * The view of this fragment that has the map on it
     *
     */
    private SatelliteView mSatelliteView;

    public SatelliteFragment() {
    }

    /*
     * Gets GPS location
     */
    
    private GpsInterface mGpsInfc = new GpsInterface() {

        @Override
        public void statusCallback(GpsStatus gpsStatus) {
            mSatelliteView.updateGpsStatus(gpsStatus); 
        }

        @Override
        public void locationCallback(Location location) {
            if(location != null) {

                /*
                 * Called by GPS. Update everything driven by GPS.
                 */
                mSatelliteView.updateLocation(location);               
            }
        }

        @Override
        public void timeoutCallback(boolean timeout) {
            if(timeout) {
                mSatelliteView.updateGpsStatus(null);
            }
        }

        @Override
        public void enabledCallback(boolean enabled) {
            if(!enabled) {
                mSatelliteView.updateGpsStatus(null);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_satellite, container,
                false);
        
        /*
         * Set service in map view
         */
        mSatelliteView = (SatelliteView)(rootView.findViewById(R.id.fragment_satellite_view));
        
        return rootView;
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
    public void setService(StorageService service) {
        
        super.setService(service);
        
        getService().registerGpsListener(mGpsInfc);
    }
}