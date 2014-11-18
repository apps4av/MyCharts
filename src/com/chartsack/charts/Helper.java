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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * 
 * @author zkhan
 *
 */
public class Helper {

    /**
     * 
     * @param root
     * @return
     */
    public static ArrayList<DirectoryItem> loadFileList(String root) {
        
        File path = new File(root);
        ArrayList<DirectoryItem> fileList = new ArrayList<DirectoryItem>();
        
        // Checks whether path exists
        if (path.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    // Filters based on whether the file is hidden or not and can be read
                    if(sel.isDirectory() && (!sel.isHidden()) && sel.canRead()) {
                        return true;
                    }
                    if(sel.isFile() && (!sel.isHidden()) && sel.canRead() && 
                            (
                            filename.toLowerCase().endsWith("jpeg") || 
                            filename.toLowerCase().endsWith("jpg")  ||
                            filename.toLowerCase().endsWith("gif")  ||
                            filename.toLowerCase().endsWith("bmp")  ||
                            filename.toLowerCase().endsWith("png")
                            )
                            ) {
                        return true;
                    }
                    return false;
                }
            };

            String[] fList = path.list(filter);
            /*
             * Add one up
             */
            fileList.add(new DirectoryItem(true, ".."));
            for (int i = 0; i < fList.length; i++) {
                File sel = new File(path, fList[i]);
                fileList.add(new DirectoryItem(sel.isDirectory(), fList[i]));
            }

        }

        return fileList;
    }
    
    
    /**
     * 
     * @param pixelPerLatitude
     * @return
     */
    public static int findPixelsPerMile(double pixelPerLatitude) { 
         return (int) Math.round(Math.abs(pixelPerLatitude / 69)); 
    }
}
