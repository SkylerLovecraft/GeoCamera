package skylerlovecraft.geocamera;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Vector;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    // Create a new Fragment to be placed in the activity layout
    CameraFragment cameraFragment;
    gMapFragment mMapFragment;
    Button btnNewPicture, btnViewMap;
    Vector<Photograph> vct;
    LocationManager locationManager;
    GoogleApiClient mGoogleApiClient;
    double latitude;
    double longitude;
    public MainActivity() {
        mMapFragment = new gMapFragment();
        cameraFragment = new CameraFragment();
        vct = new Vector<>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //populateVector();

    }

    /*public void populateVector(){
        //Set the projection for the columns to be returned
        vct = new Vector<>();
        int id;
        double lat, longt;
        String filen, filep, times;
        String[] projection = {
                PhotoProvider.TABLE_COL_ID,
                PhotoProvider.TABLE_COL_LATITUDE,
                PhotoProvider.TABLE_COL_LONGITUDE,
                PhotoProvider.TABLE_COL_FILENAME,
                PhotoProvider.TABLE_COL_FILEPATH,
                PhotoProvider.TABLE_COL_TIMESTAMP,
        };
        //Perform a query to get all rows in the DB
        Cursor myCursor = this.getContentResolver().query(PhotoProvider.CONTENT_URI,projection,null,null,"_ID ASC");
            //Create a toast message which states the number of rows currently in the database
        System.out.println("getcount:" + myCursor.getCount());
        for (int i = 0; i < myCursor.getCount(); ++i){
                myCursor.moveToPosition(i);
                id = myCursor.getInt(0);
                lat = myCursor.getDouble(1);
                longt = myCursor.getDouble(2);
                filen = myCursor.getString(3);
                filep = myCursor.getString(4);
                times = myCursor.getString(5);

                vct.add(new Photograph(lat, longt, filen, filep, times));

        }
        myCursor.close();

    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // / Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
        }
        setContentView(R.layout.activity_main);
        initializeViewComponents();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


    }
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        setLastLocation();
    }


    public void initializeViewComponents() {
        btnNewPicture = findViewById(R.id.btnNewPicture);
        btnViewMap = findViewById(R.id.btnViewMap);
        btnNewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                //disable everything on the main activity
                disableMainViewComponents();
                cameraFragment.setArguments(getIntent().getExtras());

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, cameraFragment).addToBackStack("CameraFragment").commit();
            }
        });

        findViewById(R.id.btnViewMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disable the contents of the main activity
                disableMainViewComponents();
                mMapFragment.setArguments(getIntent().getExtras());

                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mMapFragment).addToBackStack("gMapFragment").commit();

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }
    public void disableMainViewComponents() {
        btnNewPicture.setVisibility(View.INVISIBLE);
        btnNewPicture.setClickable(false);
        btnViewMap.setVisibility(View.INVISIBLE);
        btnViewMap.setClickable(false);

    }

    public void enableMainViewComponents() {
        btnNewPicture.setVisibility(View.VISIBLE);
        btnNewPicture.setClickable(true);
        btnViewMap.setVisibility(View.VISIBLE);
        btnViewMap.setClickable(true);
    }

    public void disableCameraFragment() {
        getSupportFragmentManager().beginTransaction()
                .remove(cameraFragment).commit();
    }

    public void disableMapFragment() {
        getSupportFragmentManager().beginTransaction().remove(mMapFragment).commit();
    }

    public void addNewPhotograph(double latitude, double longitude, String filename, String filePath, String timestamp)
    {
        //TODO: add map marker
        vct.add(new Photograph(latitude,longitude,filename,filePath,timestamp));
        //Create a ContentValues object
      /*  ContentValues myCV = new ContentValues();
        //Put key_value pairs based on the column names, and the values
        myCV.put(PhotoProvider.TABLE_COL_LATITUDE,latitude);
        myCV.put(PhotoProvider.TABLE_COL_LONGITUDE,longitude);
        myCV.put(PhotoProvider.TABLE_COL_FILENAME, filename);
        myCV.put(PhotoProvider.TABLE_COL_FILEPATH, filePath);
        myCV.put(PhotoProvider.TABLE_COL_TIMESTAMP, timestamp);
        //Perform the insert function using the ContentProvider
        getContentResolver().insert(PhotoProvider.CONTENT_URI,myCV);
        try{
            //Sleep 1 second
            Thread.sleep(1000);
        }
        catch(java.lang.InterruptedException myEx){
            Log.e("Error","Interrupted");
        }
        //Set the projection for the columns to be returned
        String[] projection = {
                PhotoProvider.TABLE_COL_ID,
                PhotoProvider.TABLE_COL_LATITUDE,
                PhotoProvider.TABLE_COL_LONGITUDE,
                PhotoProvider.TABLE_COL_FILENAME,
                PhotoProvider.TABLE_COL_FILEPATH,
                PhotoProvider.TABLE_COL_TIMESTAMP,
        };
        //Perform a query to get all rows in the DB
        Cursor myCursor = getContentResolver().query(PhotoProvider.CONTENT_URI,projection,null,null,null);
        //Create a toast message which states the number of rows currently in the database
        Toast.makeText(getApplicationContext(),Integer.toString(myCursor.getCount()),Toast.LENGTH_LONG).show();
    */
    }


    public Vector<Photograph> getVct(){
        return this.vct;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void setLastLocation(){

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        return;
    }else{
        // Write you code here if permission already given.
    }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    }

    public double getLat(){
        return this.latitude;
    }
    public double getLong() {
        return this.longitude;
    }

}