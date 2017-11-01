package skylerlovecraft.geocamera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;
    ImageButton btnSave;
    Button btnNewPicture;
    ImageView imageView;
    EditText editText;
    String fileName = "";
    String filePath;
    double latitude, longitude;
    boolean readyToSave = false;
    String timeStamp;

    private LocationManager locationManager;
    private LocationListener locationListener;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isStoragePermissionGranted();


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_camera, container, false);
        btnNewPicture = (Button) view.findViewById(R.id.btnNewPicture);
        btnNewPicture.setOnClickListener(this);
        btnSave = (ImageButton) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        editText = (EditText) view.findViewById(R.id.editText);
        editText.setText("");
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnNewPicture:
                dispatchTakePictureIntent();
                editText.setText(timeStamp);
                readyToSave = true;
                break;
            case R.id.btnSave:


                ((MainActivity)getActivity()).enableMainViewComponents();
                ((MainActivity)getActivity()).addNewMapMarker(latitude, longitude, fileName, filePath, timeStamp);
                ((MainActivity)getActivity()).disableCameraFragment();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private static final String LOGTAG = "MainActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;
    private ImageView mImageView;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final String CAMERA_FP_AUTHORITY = "skylerlovecraft.geocamera.fileprovider";


    /**
     * dispatchTakePictureIntent() -- Start the camera Intent
     *
     */
    public void dispatchTakePictureIntent() {
        //Create an Intent to use the default Camera Application
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                System.out.println("in catch block");
                Log.e(LOGTAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //Use the FileProvider defined in the Manifest as the authority for sharing across the Intent
                //Provides a content:// URI instead of a file:// URI which throws an error post API 24
                Uri photoURI = FileProvider.getUriForFile(getContext(), CAMERA_FP_AUTHORITY, photoFile);
                //Put the content:// URI as the output location for the photo
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //Start the Camera Application for a result*/
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                timeStamp = new SimpleDateFormat("yyyy-mm-dd_HH:mm", Locale.getDefault()).format(new Date());
            }
        }
    }


    /**
     * Create a file to place the Image taken by the photo
     * Associate with a Timestamp so that the filename will be unique
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        fileName = imageFileName;
        //Use ExternalStoragePublicDirectory so that it is accessible for the MediaScanner
        //Associate the directory with your application by adding an additional subdirectory
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"geocamera");
        if(!storageDir.exists()){
            storageDir.mkdir();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        filePath = mCurrentPhotoPath;
        Log.d(LOGTAG,"Storage Directory: " + storageDir.getAbsolutePath());
        return image;
    }

    /**
     * Boolean function to check if permissions are granted
     * If not, create an activity to request permissions
     * @return
     */
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOGTAG,"Permission is granted");
                return true;
            } else {

                Log.v(LOGTAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOGTAG,"Permission is granted");
            return true;
        }
    }


    /**
     * galleryAddPic()
     * This function puts the photo from mCurrentPhotoPath and allows the Media Gallery to access it
     * Photo must be in publicly accessible location
     */
    private void galleryAddPic() {
        //Two ways to use the MediaScanner
        //Use the MediaScannerConnection API
        //Has callback to see if it works. URI returns false if does not work
//        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{mCurrentPhotoPath}, null, new MediaScannerConnection.OnScanCompletedListener() {
//            @Override
//            public void onScanCompleted(String path, Uri uri) {
//                Log.d(LOGTAG,path);
//                Log.d(LOGTAG,uri.toString());
//            }
//        });

        //Fire off an Intent to use the MediaScanner
        //Place the URI of the file as the data
        File f = new File(mCurrentPhotoPath);
        Intent myMediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        myMediaIntent.setData(Uri.fromFile(f));
        getContext().sendBroadcast(myMediaIntent);
    }

    /**
     * onActivityResult callback fires after startActivityForResult finishes the new activity
     * @param requestCode -- Int associated with the activity request for switching activity
     * @param resultCode -- Result value from the startedActivity
     * @param data -- Return Values
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the Request is to take a photo and it returned OK
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Extras does not contain bitmap if Image is saved to a file
            //Bundle extras = data.getExtras();
            //Create a BitmapFactoryOptions object to get Bitmap from a file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            //Load the Bitmap from the Image file created by the camera
            Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath,options);
            //Set the ImageView to the bitmap
            imageView.setImageBitmap(imageBitmap);
            //Add the photo to the gallery
            galleryAddPic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(LOGTAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

}
