package skylerlovecraft.geocamera;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Skyler on 11/1/17.
 */

public class PhotoProvider extends ContentProvider {
    private static String LOGTAG = "PhotoProvider:";
    // Database Name for SQLITE DB
    private static final String DBNAME = "PhotoDB";
    // Authority is the package name
    private static final String AUTHORITY = "skylerlovecraft.geocamera.PhotoProvider";
    //TABLE_NAME is defined as ToDoList
    private static final String TABLE_NAME = "Photos";
    //Create a CONTENT_URI for use by other classes
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/"+TABLE_NAME);

    //Column names for the photos Table
    public static final String TABLE_COL_ID = "_ID";
    public static final String TABLE_COL_LATITUDE= "LATITUDE";
    public static final String TABLE_COL_LONGITUDE = "LONGITUDE";
    public static final String TABLE_COL_FILENAME = "FILENAME";
    public static final String TABLE_COL_FILEPATH= "FILEPATH";
    public static final String TABLE_COL_TIMESTAMP = "TITLE";



    //Table create string based on column names
    private static final String SQL_CREATE_MAIN = "CREATE TABLE " +
                                // The colTumns in the table
            TABLE_NAME+ " "     + // Table's name
            "(" +
            TABLE_COL_ID + " INTEGER PRIMARY KEY," +
            TABLE_COL_LATITUDE  + " DOUBLE," +
            TABLE_COL_LONGITUDE + " DOUBLE," +
            TABLE_COL_FILENAME  + " TEXT,"    +
            TABLE_COL_FILEPATH  + " TEXT,"   +
            TABLE_COL_TIMESTAMP + " TEXT)";

    //URI Matcher object to facilitate switch cases between URIs
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MainDatabaseHelper mOpenHelper;

    public PhotoProvider() {
        sUriMatcher.addURI(AUTHORITY,TABLE_NAME,1); //Match to the authority and the table name, assign 1
        sUriMatcher.addURI(AUTHORITY,TABLE_NAME+"/#",2);//Match to the authority and the table name, and an ID, assign 2
    }
    @Override
    public boolean onCreate() {
        /*
         * Creates a new helper object. This method always returns quickly.
         * Notice that the database itself isn't created or opened
         * until SQLiteOpenHelper.getWritableDatabase is called
         */
        mOpenHelper = new MainDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable Bundle queryArgs, @Nullable CancellationSignal cancellationSignal) {
        return super.query(uri, projection, queryArgs, cancellationSignal);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)){
            case 1:
                //Allow update based on multiple selections
                break;
            case 2:
                //Allow updates based on a single ID
                String id = uri.getPathSegments().get(1);
                selection = TABLE_COL_ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ?
                                "AND (" + selection + ")" : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        //Perform updates and return the number that were updated
        int updateCount = mOpenHelper.getWritableDatabase().update(TABLE_NAME,values,
                selection,selectionArgs);
        try {
            //Sleep for 3 seconds
            Thread.sleep(3000);
        }
        catch (java.lang.InterruptedException myEx){
            Log.e(LOGTAG,myEx.toString());
        }
        //Notify the context
        getContext().getContentResolver().notifyChange(uri,null);
        //Return the number of rows updated
        return updateCount;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)){
            //Match on URI with ID
            case 2:
                String id = uri.getPathSegments().get(1);
                selection = TABLE_COL_ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? "AND (" + selection  + ")" : "");
                break;
            default:
                //Else, error is thrown
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        //Delete from the database, return integer value for how many rows are deleted
        int deleteCount = mOpenHelper.getWritableDatabase().delete(
                TABLE_NAME,selection,selectionArgs);
        try {
            //Sleep for 3 seconds to emulate network latency
            Thread.sleep(3000);
        }
        catch (java.lang.InterruptedException myEx){
            Log.e(LOGTAG,myEx.toString());
        }
        //Notify calling context
        getContext().getContentResolver().notifyChange(uri,null);
        //Return number of rows deleted (if any)
        return deleteCount;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder, @Nullable CancellationSignal cancellationSignal) {
//Use an SQLiteQueryBuilder object to create the query
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        //Set the table to be queried
        queryBuilder.setTables(TABLE_NAME);

        //Match on either the URI with or without an ID
        switch (sUriMatcher.match(uri)){
            case 1:
                //If no ID, and no sort order, specify the sort order as by ID Ascending
                if(TextUtils.isEmpty(sortOrder)) sortOrder="_ID ASC";
                break;
            case 2:
                //Otherwise, set the selection of the URI to the ID
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            default:
                Log.e(LOGTAG, "URI not recognized " + uri);
        }
        //Query the database based on the columns to be returned, the selection criteria and
        // arguments, and the sort order
        Cursor cursor = queryBuilder.query(mOpenHelper.getWritableDatabase(),projection,selection,
                selectionArgs,null,null,sortOrder);
        try {
            //Sleep for 3 seconds
            Thread.sleep(3000);
        }
        catch (java.lang.InterruptedException myEx){
            Log.e(LOGTAG,myEx.toString());
        }
        //Return the cursor object
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (sUriMatcher.match(uri)){
            //Match against a URI with just the table name
            case 1:
                break;
            default:
                //Otherwise, error is thrown
                Log.e(LOGTAG, "URI not recognized " + uri);
        }
        //Insert into the table, return the id of the inserted row
        long id = mOpenHelper.getWritableDatabase().insert(TABLE_NAME,null,values);
        try {
            //Sleep for 3 seconds to emulate network latency
            Thread.sleep(3000);
        }
        catch (java.lang.InterruptedException myEx){
            Log.e(LOGTAG,myEx.toString());
        }
        //Notify context of change
        getContext().getContentResolver().notifyChange(uri,null);
        //Return the URI with the ID at the end
        return Uri.parse(CONTENT_URI+"/" + id);    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    //Class for creating an instance of a SQLiteOpenHelper
    //Performs creation of the SQLite Database if none exists
    protected static final class MainDatabaseHelper extends SQLiteOpenHelper {
        /*
         * Instantiates an open helper for the provider's SQLite data repository
         * Do not do database creation and upgrade here.
         */
        MainDatabaseHelper(Context context) {
            super(context, DBNAME, null, 1);
        }

        /*
         * Creates the data repository. This is called when the provider attempts to open the
         * repository and SQLite reports that it doesn't exist.
         */
        public void onCreate(SQLiteDatabase db) {

            // Creates the main table
            db.execSQL(SQL_CREATE_MAIN);
        }

        public void onUpgrade(SQLiteDatabase db, int int1, int int2){
            db.execSQL("DROP TABLE IF EXISTS ToDoList");
            onCreate(db);

        }
    }

}
