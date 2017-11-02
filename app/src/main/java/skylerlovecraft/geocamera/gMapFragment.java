package skylerlovecraft.geocamera;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Vector;

import skylerlovecraft.geocamera.R;

import static com.google.android.gms.wearable.DataMap.TAG;

public class gMapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.InfoWindowAdapter{
    private View rootView;
    GoogleMap myMap;
    MapView mMapView;
    ImageButton btnReturn;
    Vector<Photograph> vct;

    Photograph currentPhoto;
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
    vct = ((MainActivity)getActivity()).getVct();
    
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.fragment_g_map, container, false);
            MapsInitializer.initialize(this.getActivity());
            mMapView = (MapView) rootView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);
        }
        catch (InflateException e){
            Log.e(TAG, "Inflate exception");
        }
        btnReturn = (ImageButton) rootView.findViewById(R.id.imageButton);
        btnReturn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        ((MainActivity)getActivity()).enableMainViewComponents();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState); mMapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        {
            ((MainActivity)getActivity()).mGoogleApiClient.connect();
            myMap = googleMap;
            myMap.setInfoWindowAdapter(this);
            myMap.setBuildingsEnabled(false);
            ((MainActivity)getActivity()).setLastLocation();
            LatLng latLng = new LatLng(((MainActivity)getActivity()).getLat(), ((MainActivity)getActivity()).getLong());
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
            addMarkers();

        }

    }
    public void addMarkers(){
        Photograph photo;
        //((MainActivity)getActivity()).populateVector();
        vct = ((MainActivity)getActivity()).getVct();
        for(int i = 0; i < vct.size(); ++i)
        {
            System.out.println("in add markers");
            photo = vct.get(i);
            currentPhoto = photo;
            //TODO: add the markers from the vector
            myMap.addMarker(new MarkerOptions()
                    .position(new LatLng(photo.latitude,photo.longitude))
                    .title(photo.timestamp)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        }
    }

    private void closeMap(){
        ((MainActivity)getActivity()).disableMapFragment();
        ((MainActivity)getActivity()).enableMainViewComponents();

    }

    @Override
    public void onClick(View v) {
        closeMap();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
        //return prepareInfoView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        //return null;
        return prepareInfoView(marker);

    }

    private View prepareInfoView(Marker marker){
        //prepare InfoView programmatically
        LinearLayout infoView = new LinearLayout(getContext());
       // LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
         //       LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(500, 750);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        ImageView infoImageView = new ImageView(getContext());
        //Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        //Create a BitmapFactoryOptions object to get Bitmap from a file
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //Load the Bitmap from the Image file created by the camera
        Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhoto.filePath,options);

        try {
            ExifInterface exif = new ExifInterface(currentPhoto.filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {

        }
        //Set the ImageView to the bitmap
        infoImageView.setImageBitmap(imageBitmap);
        infoView.addView(infoImageView);

        TextView subInfoLat = new TextView(getContext());
        subInfoLat.setText("Lat: " + marker.getPosition().latitude);
        TextView subInfoLnt = new TextView(getContext());
        subInfoLnt.setText("Lnt: " + marker.getPosition().longitude);
        infoView.addView(subInfoLat);
        infoView.addView(subInfoLnt);

        return infoView;
    }
}