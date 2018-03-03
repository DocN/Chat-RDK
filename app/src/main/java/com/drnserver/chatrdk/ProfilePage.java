package com.drnserver.chatrdk;

        import android.*;
        import android.Manifest;
        import android.location.Address;
        import android.location.Geocoder;
        import android.location.Location;
        import android.os.Handler;
        import android.support.v4.app.ActivityCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.SeekBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.drnserver.chatrdk.service.GPStracker;
        import com.drnserver.chatrdk.service.LocationInfo;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.io.IOException;
        import java.util.List;
        import java.util.Locale;


public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfilePage";
    //back controller vars
    private boolean backExit;
    private boolean runningBackHold;

    private TextView profileName;
    private TextView userID;
    private FirebaseAuth mAuth;

    /* ALEX This is a list that contains address information*/
    List<Address> address;
    /* ALEX This is the view displaying address */
    private TextView userAddress;
    /*ALEX location slider bar */
    private SeekBar locationBar;
    /*ALEX location preference distance */
    private TextView userPrefDist;

    /* ALEX These are database references we will use to push */
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference myRefLocationZ = myRef.child("locationZ");

    /* ALEX the current user */
    FirebaseUser user;

    /* The distance we will grab from the progress bar*/
    int distance;

    /*This is the old progress bar value from the users last sign in */
    int oldDist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.removeTopBar();
        setContentView(R.layout.activity_mainframe);
        //store local auth reference
        mAuth = loginActivity.mAuth;
        //declare objects in view
        profileName = (TextView) findViewById(R.id.profileName);
        userAddress = findViewById(R.id.userLocationInfo);
        userID = (TextView) findViewById(R.id.tempIDCheck);
        locationBar = findViewById(R.id.seekBar2);
         //user initialized
        user = mAuth.getCurrentUser();

        /*ALEX: Gets the old user data for the progress bar and sets the text and bar*/
        myRefLocationZ.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationInfo thisLocInfo = dataSnapshot.getValue(LocationInfo.class);
                oldDist = thisLocInfo.getDist();
                System.out.println("The old value is " + oldDist);
                locationBar.setProgress(oldDist);
                userPrefDist.setText(oldDist + " km");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /* ALEX: Set the value of the distance on the progress changed*/
        locationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int distance = seekBar.getProgress();
                myRefLocationZ.child(user.getUid()).child("dist").setValue(distance);
                userPrefDist.setText(distance + " km");
}
        });



        this.initUserProfile();

        /** ALEX: Location Generation **/

        //Request permissions from the phone
        ActivityCompat.requestPermissions(ProfilePage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        // Create a GPStracker object
        GPStracker g = new GPStracker(getApplicationContext());
        // Create a location object, which we get from our GPS tracker
        Location l = g.getLocation();
        if(l != null) {
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                address = geoCoder.getFromLocation(lat, lon, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // sets the text to the current city location
            userAddress.setText("Your current location: " + address.get(0).getLocality());
            // sets your location at the time of opening this activity to firebase
            myRefLocationZ.child(user.getUid()).child("lat").setValue(lat);
            myRefLocationZ.child(user.getUid()).child("lon").setValue(lon);
        }
    }



    private void initUserProfile() {
        profileName.setText(mAuth.getCurrentUser().getEmail());
        userID.setText(mAuth.getCurrentUser().getUid());
    }

    private void removeTopBar() {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }


}


