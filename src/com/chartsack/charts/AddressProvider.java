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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 
 * @author zkhan
 *
 */
public class AddressProvider extends ContentProvider {
     // fields for my content provider
     static final String PROVIDER_NAME = "com.chartsack.charts.addrinfo";
     static final String URL = "content://" + PROVIDER_NAME + "/address";
     static final Uri CONTENT_URI = Uri.parse(URL);
       
     // fields for the database
     static final String ID = "id";
     static final String ADDRESS = "address";
     static final String LONGITUDE = "longitude";
     static final String LATITUDE = "latitude";
     
     DBHelper dbHelper;
     
     // database declarations
     private SQLiteDatabase mSqliteDatabase;
     static final String DATABASE_NAME = "dbAddress";
     static final String TABLE_NAME = "address";
     static final int DATABASE_VERSION = 1;
     static final String CREATE_TABLE = 
             " CREATE TABLE " + TABLE_NAME +
             " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
             " " + ADDRESS + " TEXT NOT NULL, " +
             " " + LATITUDE + " DOUBLE NOT NULL, " +
             " " + LONGITUDE + " DOUBLE NOT NULL);";
     
     // integer values used in content URI
     static final int ADDRESS_ALL = 1;
     static final int ADDRESS_SINGLE = 2;
     
     static final UriMatcher uriMatcher;
     static {
         uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
         uriMatcher.addURI(PROVIDER_NAME, "address", ADDRESS_ALL);
         uriMatcher.addURI(PROVIDER_NAME, "address/#", ADDRESS_SINGLE);
     }
 
     // class that creates and manages the provider's database 
     private static class DBHelper extends SQLiteOpenHelper {

         public DBHelper(Context context) {
             super(context, DATABASE_NAME, null, DATABASE_VERSION);
         }

         @Override
         public void onCreate(SQLiteDatabase db) {
             db.execSQL(CREATE_TABLE);
         }

         @Override
         public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
             db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
             onCreate(db);
         }
    }
   
    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);
        // permissions to be writable
        mSqliteDatabase = dbHelper.getWritableDatabase();

        if(mSqliteDatabase == null) {
            return false;
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // the TABLE_NAME to query on
        queryBuilder.setTables(TABLE_NAME);
          
        switch(uriMatcher.match(uri)) {
            // maps all database column names
            case ADDRESS_ALL:
                break;
            case ADDRESS_SINGLE:
                queryBuilder.appendWhere(ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == "") {
            // No sorting-> sort on names by default
            sortOrder = ADDRESS;
        }
        Cursor cursor = queryBuilder.query(mSqliteDatabase, projection, selection, 
                selectionArgs, null, null, sortOrder);
        /** 
         * register to watch a content URI for changes
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long row = mSqliteDatabase.insert(TABLE_NAME, "", values);
          
        // If record is added successfully
        if(row > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            
            return newUri;
        }
        throw new SQLException("Fail to add a new record into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
          
        switch (uriMatcher.match(uri)){
            case ADDRESS_ALL:
                count = mSqliteDatabase.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case ADDRESS_SINGLE:
                count = mSqliteDatabase.update(TABLE_NAME, values, ID + 
                        " = " + uri.getLastPathSegment() + 
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                        selection + ')' : ""), selectionArgs);
                break;
            default: 
                throw new IllegalArgumentException("Unsupported URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch(uriMatcher.match(uri)) {
            case ADDRESS_ALL:
                count = mSqliteDatabase.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case ADDRESS_SINGLE:
                String id = uri.getLastPathSegment();   //gets the id
                count = mSqliteDatabase.delete(TABLE_NAME, ID +  " = " + id + 
                      (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default: 
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
          
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
          // Get all records 
          case ADDRESS_ALL:
             return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.chartsack.charts.addrinfo.address";
          // Get a particular records 
          case ADDRESS_SINGLE:
             return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.chartsack.charts.addrinfo.address";
          default:
              throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

}
