package org.ei.opensrp.core.db.repository;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Maimoona on 1/5/2017.
 */
public class CEContentProvider extends ContentProvider {
    // used for the UriMacher
    private static final int ALL = 1;
    private static final int ID = 2;
    private static final int COUNT = 3;
    private static final int JOIN = 4;

    public static final String AUTHORITY = "org.ei.opensrp.provider.cedb";

    public static final Uri CONTENT_URI(String addressType, String group){
        return Uri.parse("content://" + AUTHORITY + "/select?address="+addressType+"&group="+group);
    }

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // Request register table for all data; table name
        sURIMatcher.addURI(AUTHORITY, "/select", ALL);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = null;
        Log.v(getClass().getName(), "Register Content Provider reached with type"+sURIMatcher.match(uri));
        Log.v(getClass().getName(), "with query "+uri.getQuery()+" "+uri.getPath()+" "+uri.getAuthority());
        switch (sURIMatcher.match(uri)){
            case ALL:
                c = RegisterRepository.queryCE(uri.getQueryParameter("address"), selection, uri.getQueryParameter("group"), sortOrder);
            break;
        }
        // make sure that potential listeners are getting notified
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
