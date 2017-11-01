package skylerlovecraft.geocamera;


import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Vector;


public class MainActivity extends AppCompatActivity {


    // Create a new Fragment to be placed in the activity layout
    CameraFragment cameraFragment;
    gMapFragment mMapFragment;
    ConstraintLayout constraintLayout;
    Button btnNewPicture, btnViewMap;
    Vector<Photograph> vct;
    public MainActivity() {
        mMapFragment = new gMapFragment();
        cameraFragment = new CameraFragment();
        vct = new Vector<Photograph>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, cameraFragment).commit();
            }
        });

        findViewById(R.id.btnViewMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disable the contents of the main activity
                disableMainViewComponents();
                mMapFragment.setArguments(getIntent().getExtras());

                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mMapFragment).commit();

            }
        });
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


    public void addNewMapMarker(double latitude, double longitude, String filename, String filePath, String timestamp)
    {
        //TODO: add map marker
        vct.add(new Photograph(latitude,longitude,filename,filePath,timestamp));

    }

}