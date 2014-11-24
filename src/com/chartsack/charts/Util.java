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


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chartsack.charts.gps.Gps;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Util {

    /**
     * A way to convert arraylist to hashmap with duplicate remove
     * @param hash
     * @return
     */
    private static ArrayList<Address> hashMapToList(HashMap<String, Address> hash) {
        ArrayList<Address> addr =
                new ArrayList<Address>(hash.values());
        return addr;
    }

    /**
     * Routine that uses web service for geo location to find a list of addresses
     * @param address
     * @return
     */
    public static List<Address> getGeoPoint(Context ctx, String address) {
        
        int LIMIT = 5;

        // Send a address search string like 105 wood
        HashMap<String, Address> hash = new HashMap<String, Address>();
        
        /*
         * See if it is GPS coordinates
         */
        String regex = "[+-]?\\d+\\.?\\d+\\s*,\\s*[+-]?\\d+\\.?\\d+";
        Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = ptn.matcher(address);
        if(matcher.find()) {
            String tokens[] = address.replaceAll("\\s+", "").split(",");
            if(tokens.length == 2) {
                try {
                    /*
                     * This does look like a GPS coordinate. Add verbatim to top.
                     */
                    double lat = Double.parseDouble(tokens[0]);
                    double lon = Double.parseDouble(tokens[1]);
                    Address a = new Address(Locale.getDefault());
                    a.setLongitude(lon);
                    a.setLatitude(lat);
                    a.setAddressLine(0, address);
                    hash.put(address, a);                   
                }
                catch (Exception e) {
                    /*
                     * Number format exception
                     */
                }
            }
        }       
        
        String addr;
        try {
            addr =  URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            return hashMapToList(hash);
        }

        /*
         * Find from content provider
         */
        Address adds[] = (new AddressData(ctx)).getAddress(address);
        if(null != adds) {
            for(int count = 0; count < adds.length; count++) {
                hash.put(adds[count].getAddressLine(0), adds[count]);
            }
        }
        
        /*
         * Add current
         */
        Location l = Gps.getLastLocation(ctx);
        if(null != l) {
	        Address here = new Address(Locale.getDefault());
	        here.setAddressLine(0, ctx.getString(R.string.current_position));
	        here.setLatitude(l.getLatitude());
	        here.setLongitude(l.getLongitude());
	        hash.put(here.getAddressLine(0), here);
        }
        
        /*
         * Find from INET
         */
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT) 
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 3000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        // geolocation service query
        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + addr + "&ka&sensor=false");
        HttpClient client = new DefaultHttpClient(httpParameters);
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();
    
        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
            return hashMapToList(hash);
        }
    
        // it returns in JSON, parse
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
            // Dont return too many results
            for(int i = 0; i < LIMIT; i++) {
                double lon = 0;
                double lat = 0;
                String formatted = null;
                try {
        
                    lon = ((JSONArray)jsonObject.get("results")).getJSONObject(i)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");
        
                    lat = ((JSONArray)jsonObject.get("results")).getJSONObject(i)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");
        
                    formatted = ((JSONArray)jsonObject.get("results")).getJSONObject(i)
                            .getString("formatted_address");
        
                } catch (JSONException e) {
                    return hashMapToList(hash);
                }
                
                // got a suggestion
                if(formatted != null) {
                    // set first line as suggestion, also populate its coordinates
                    Address a = new Address(Locale.getDefault());
                    a.setLongitude(lon);
                    a.setLatitude(lat);
                    a.setAddressLine(0, formatted);
                    hash.put(formatted, a);
                }
            }
        } catch (JSONException e) {
        }
    
        return hashMapToList(hash);
    }

    
    /**
    * Hide the soft keyboard on a text
    */
    public static void hideKeyboard(EditText text) {
        InputMethodManager imm = (InputMethodManager)text.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
    }

    /**
     *  Converts 1 dip (device independent pixel) into its equivalent physical pixels
     */
    public static float getDpiToPix(Context ctx) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, 
                ctx.getResources().getDisplayMetrics());
    }
    
    /**
     * 
     * @param lonlat
     */
    public static double truncGeo(double lonlat) {
        lonlat *= 10000;
        lonlat = Math.round(lonlat);
        lonlat /= 10000;
        return lonlat;
    }

    /**
     * 
     * @param lon
     * @return
     */
    public static boolean isLongitudeSane(double lon) {
        return (lon < 180) && (lon > -180);
    }
    
    /**
     * 
     * @param lat
     * @return
     */
    public static boolean isLatitudeSane(double lat) {
        return (lat > -90) && (lat < 90); 
    }

}
