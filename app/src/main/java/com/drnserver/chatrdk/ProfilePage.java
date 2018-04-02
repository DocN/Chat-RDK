package com.drnserver.chatrdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.drnserver.chatrdk.service.GPStracker;
import com.drnserver.chatrdk.service.LocationInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfilePage";
    //back controller vars
    private boolean backExit;
    private boolean runningBackHold;

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


    private TextView profileName;
    private TextView userID;
    private FirebaseAuth mAuth;
    private CircleImageView circleImageView;
    private DatabaseReference userSearchImageReference;
    private String imgURL;
    //steven: uploadImage
    private FloatingActionButton upLoadImage;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    //Steven: Firebase storage
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String authData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.removeTopBar();
        setContentView(R.layout.activity_mainframe);
        //store local auth reference
        mAuth = loginActivity.mAuth;
        //declare objects in view
        profileName = (TextView) findViewById(R.id.profileName);
        userID = (TextView) findViewById(R.id.tempIDCheck);
        userAddress = findViewById(R.id.userLocationInfo);
        //user prefered distance
        userPrefDist = findViewById(R.id.actualDistance);
        locationBar = findViewById(R.id.seekBar2);
        //user initialized
        user = mAuth.getCurrentUser();
        //upload image - steven
        authData = FirebaseAuth.getInstance().getCurrentUser().getUid();
        upLoadImage = findViewById(R.id.uploadDP);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("profileImages/" + authData);
        circleImageView = findViewById(R.id.profilePageImage);
        userSearchImageReference = FirebaseDatabase.getInstance().getReference("UserIndex")
                .child(authData).child("userSearchImage");

        /*ALEX: Gets the old user data for the progress bar and sets the text and bar*/
        myRefLocationZ.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    LocationInfo thisLocInfo = dataSnapshot.getValue(LocationInfo.class);
                    oldDist = thisLocInfo.getDist();
                    System.out.println("The old value is " + oldDist);
                    locationBar.setProgress(oldDist);
                    userPrefDist.setText(oldDist + " km");
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

         /* ALEX: Set the value of the distance on the progress changed*/
        locationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int distance = seekBar.getProgress();
                myRefLocationZ.child(user.getUid()).child("dist").setValue(distance);
                userPrefDist.setText(distance + " km");
            }
        });

        //Steven setup profile pic
        userSearchImageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exist()) {
                  imgURL = dataSnapshot.getValue().toString();
                  Log.e("imageURL2", imgURL);
                  Glide.with(getApplicationContext()).load(imgURL).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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

        //Listener for the upload image button - steven
        upLoadImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chooseImage();
            }
        });

    }

    private void initUserProfile() {
        profileName.setText(mAuth.getCurrentUser().getEmail());
        userID.setText(mAuth.getCurrentUser().getUid());
    }

    private void removeTopBar() {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void setPreviousChosenPreferenceData() {
        myRefPreferenceInfo.child(user.getUid()).child("chosenPreferences").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    ArrayList<String> onLoadPreferences = new ArrayList<>();
                    String item = "";
                    System.out.println("In the method");
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        System.out.println("testing" + postSnapshot.getValue());
                        item = "" + postSnapshot.getValue();
                        onLoadPreferences.add(item);
                    }

                    chosenPreference1.setText(onLoadPreferences.get(0));
                    chosenPreference2.setText(onLoadPreferences.get(1));
                    chosenPreference3.setText(onLoadPreferences.get(2));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
  
    //choose the image to upload - steven
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    //used for choosing image. check if picking image is successful and upload the image - steven
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if success, upload image
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null ) {
            filePath = data.getData();
            uploadImage();
        }
    }

    //upload the image to firebase storage - steven
    private void uploadImage() {

        if(filePath != null)
        {
            //show upload progress - steven
            final ProgressDialog progressDialog = new ProgressDialog(ProfilePage.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            //upload image - steven
            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfilePage.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            //set the profile image
                            setProfileImage();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //handle upload failed
                            progressDialog.dismiss();
                            Toast.makeText(ProfilePage.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //update upload progress
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    //set profile image url to userindex - steven
    private void setProfileImage() {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri url) {
                //set image url
                userSearchImageReference.setValue(url.toString());
                //Log.d(TAG, url.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}
