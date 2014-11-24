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

import android.app.AlertDialog;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * A fragment that shows the map
 */
public class TagFragment extends FragmentWrapper implements ObserverAlertDialogBuilder.Methods {
    /**
     * The view of this fragment that has the map on it
     *
     */
    private TagView mTagView;

    private ImageButton mFindButton;
    private Address mNotifyAddress0;
    private Address mNotifyAddress1;
    private AlertDialog mDialogSearch;


    public TagFragment() {
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tag, container,
                false);
        
                
        mFindButton = (ImageButton)rootView.findViewById(R.id.fragment_tag_button_find);
        mFindButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /*
                 * Make dialog
                 */
                ObserverAlertDialogBuilder alertDialogBuilder = new ObserverAlertDialogBuilder(getActivity(),
                        TagFragment.this, getString(R.string.search));
                 
                // create alert dialog
                mDialogSearch = alertDialogBuilder.create();

                // show it
                mDialogSearch.show();
            }
        });


        /*
         * Set service in map view
         */
        mTagView = (TagView)(rootView.findViewById(R.id.fragment_tag_plateview));
        mTagView.setService(getService());
        mTagView.setZoomControls((ZoomButtons)rootView.findViewById(R.id.fragment_tag_zoom));
        mTagView.setHomeControls((ImageButton)rootView.findViewById(R.id.fragment_tag_button_top));
        

        /*
         * New search
         */
        mNotifyAddress0 = mNotifyAddress1 = null;   
        
        showHelp(getString(R.string.tag_help_start));
        
        return rootView;
    }

    /**
     * Calculate tagging info and store in image.
     */
    private boolean calculateTag() {
        double x0, x1, y0, y1, lon0, lon1, lat0, lat1;
        String tokens0[];
        String tokens1[];
        lon0 = mNotifyAddress0.getLongitude();
        lat0 = mNotifyAddress0.getLatitude();
        lon1 = mNotifyAddress1.getLongitude();
        lat1 = mNotifyAddress1.getLatitude();
        tokens0 = mNotifyAddress0.getFeatureName().split(",");
        tokens1 = mNotifyAddress1.getFeatureName().split(",");
        
        try {
            x0 = Double.parseDouble(tokens0[0]);
            y0 = Double.parseDouble(tokens0[1]);
            x1 = Double.parseDouble(tokens1[0]);
            y1 = Double.parseDouble(tokens1[1]);
        }
        catch(Exception e) {
            return false;
        }
        
        double diffx = x0 - x1;
        double diffy = y0 - y1;
        double difflon = lon0 - lon1;
        double difflat = lat0 - lat1;
        if(difflon == 0 || difflat == 0 || diffx == 0 || diffy == 0) {
            return false;
        }
        
        double dx = diffx / difflon; 
        double dy = diffy / difflat;

        if(dx == 0 || dy == 0) {
            return false;
        }
        
        double lonTopLeft = lon0 - x0 / dx;
        double latTopLeft = lat0 - y0 / dy;
        
        /**
         * Save data if sane
         */
        if(Util.isLongitudeSane(lonTopLeft) && Util.isLatitudeSane(latTopLeft)) {
            /*
             * Save in image dx + "," + dy + "," + lonTopLeft + "," + latTopLeft 
             */
            String tag =  dx + "," + dy + "," + lonTopLeft + "," + latTopLeft;
            TagData provider = new TagData(getActivity());
            // file name not full path
            String names[] = getService().getBitmapHolder().getName().split("/");
            String name = names[names.length - 1];
            // delete old tag
            provider.deleteTag(name);
            provider.addTag(name, tag);

            // update in service
            getService().setGeotagData(tag);
            return true;
        }

        return false;
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

    /**
     * 
     */
    @Override
    public void onItemSelected(Address a) {
        try {
            mDialogSearch.dismiss();
            /*
             * Get two points
             */
            String data = getService().getBounds().getCenterX() + "," + getService().getBounds().getCenterY();

            if(null == mNotifyAddress0) {
                mNotifyAddress0 = a;
                mNotifyAddress0.setFeatureName(data);
                showHelp(getString(R.string.tag_help_point1));
            }
            else if(null == mNotifyAddress1) {
                mNotifyAddress1 = a;
                mNotifyAddress1.setFeatureName(data);
            }
            /*
             * Now two points done, calculate and store data
             */
            if(calculateTag()) {
                showHelp(getString(R.string.tag_help_end));
            }
            else {
                showHelp(getString(R.string.tag_help_fail));                
            }
        }
        catch (Exception e) {
        }
    }
}