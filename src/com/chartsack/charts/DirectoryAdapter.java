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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author zkhan
 *
 */
public class DirectoryAdapter extends ArrayAdapter<DirectoryItem> {

    private Context  mContext;
    private ArrayList<DirectoryItem> mInfo;
        
    /**
     * @param context
     * 
     */
    public DirectoryAdapter(Context context, ArrayList<DirectoryItem> info) {
        super(context, R.layout.file_list, info);
        mContext = context;
        mInfo = info;
    }

  
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = convertView;
        
        if(null == rowView) {
            rowView = inflater.inflate(R.layout.file_list, parent, false);
        }
        TextView textView = (TextView)rowView.findViewById(R.id.file_list_info);
        ImageView imgView = (ImageView)rowView.findViewById(R.id.file_list_icon);
        textView.setText(mInfo.get(position).getName());
        if(mInfo.get(position).isDir()) {
            imgView.setImageResource(R.drawable.directory_icon);
        }
        else {
            imgView.setImageResource(R.drawable.file_icon);
        }
        return rowView;
    }    
}
