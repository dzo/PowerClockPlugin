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
    public static final String BIGSIZE="BIGSIZE";

    public SettingsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String[] columns = new String[] { "_id", SIZE, DIALCOLOUR,TEXTCOLOUR,HANDCOLOUR,SHOWTEXT, BIGSIZE};
        int size=getContext().getSharedPreferences("DzClock", Context.MODE_PRIVATE).getInt(SIZE,400);
        int bigsize=getContext().getSharedPreferences("DzClock", Context.MODE_PRIVATE).getInt(BIGSIZE,600);
        int dialcolour=getContext().getSharedPreferences("DzClock", Context.MODE_PRIVATE).getInt(DIALCOLOUR,-1);
        int textcolour=getContext().getSharedPreferences("DzClock", Context.MODE_PRIVATE).getInt(TEXTCOLOUR,-1);
        int handcolour=getContext().getSharedPreferences("DzClock", Context.MODE_PRIVATE).getInt(HANDCOLOUR,-1);
        int showtext=getContext().getSharedPreferences("DzClock", Context.MODE_PRIVATE).getInt(SHOWTEXT,1);
        MatrixCursor matrixCursor= new MatrixCursor(columns);
        matrixCursor.addRow(new Object[] { 1, size , dialcolour, textcolour, handcolour,showtext,bigsize});
        return matrixCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}