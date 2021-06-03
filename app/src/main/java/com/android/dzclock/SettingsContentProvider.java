package com.android.dzclock;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsContentProvider extends ContentProvider {
    public static final String DIALCOLOUR="DIALCOLOUR";
    public static final String HANDCOLOUR="HANDCOLOUR";
    public static final String TEXTCOLOUR="TEXTCOLOUR";
    public static final String SHOWTEXT="SHOWTEXT";
    public static final String SIZE="SIZE";

    public SettingsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        /*
        if("doze".equals(selection)) {


            try {
                FileOutputStream f = new FileOutputStream("/sys/class/drm/card0-DSI-1/doze_brightness");
                f.write("1".getBytes());
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

         */
        String[] columns = new String[] { "_id", SIZE, DIALCOLOUR,TEXTCOLOUR,HANDCOLOUR,SHOWTEXT};
        int size=getContext().getSharedPreferences("DzClock", Context.MODE_PRIVATE).getInt(SIZE,400);
        int dialcolour=getContext().getSharedPreferences("DzClock", Context.MODE_PRIVATE).getInt(DIALCOLOUR,-1);
        int textcolour=getContext().getSharedPreferences("DzClock", Context.MODE_PRIVATE).getInt(TEXTCOLOUR,-1);
        int handcolour=getContext().getSharedPreferences("DzClock", Context.MODE_PRIVATE).getInt(HANDCOLOUR,-1);
        int showtext=getContext().getSharedPreferences("DzClock", Context.MODE_PRIVATE).getInt(SHOWTEXT,1);
        MatrixCursor matrixCursor= new MatrixCursor(columns);
        matrixCursor.addRow(new Object[] { 1, size , dialcolour, textcolour, handcolour,showtext});
        return matrixCursor;

        // TODO: Implement this to handle query requests from clients.
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}