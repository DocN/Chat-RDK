package com.drnserver.chatrdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.firebase.storage.FirebaseStorage;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
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
    /* ALEX preference view 1 */
    private EditText chosenPreference1;
    /* ALEX preference view 2 */
    private EditText chosenPreference2;
    /* ALEX preference view 3 */
    private EditText chosenPreference3;

    private TextView premadePreference1;
    private TextView premadePreference2;
    private TextView premadePreference3;

    static int counter1 = 0;

    //steven: profile picture
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



    /* ALEX These are database references we will use to push */
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference myRefLocationZ = myRef.child("locationZ");
    DatabaseReference myRefPreferenceInfo = myRef.child("preferenceInfo");

    /* ALEX the current user */
    FirebaseUser user;

    /* The distance we will grab from the progress bar*/
    int distance;

    /*This is the old progress bar value from the users last sign in */
    int oldDist;

    /* ALEX:  Preference Variables*/
    Button preferenceBtn;
    TextView preferenceView;
    String[] preferenceList;
    boolean[] checkedItems;
    ArrayList<Integer> selectedPreferences;
    ArrayList<String> selectedPreferencesValues;
    ArrayList<String> chosenPreferenceValues;

    private TextView profileName;
    private TextView userID;
    private FirebaseAuth mAuth;
    private DatabaseReference userIndex;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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

        /*Initializing preference variables */
        preferenceBtn = findViewById(R.id.addPreferences);
         checkedItems = new boolean[5];
         preferenceList = getResources().getStringArray(R.array.preference_items);
        selectedPreferences = new ArrayList<>();
        chosenPreference1 = findViewById(R.id.chosenPreference1);
        chosenPreference2 = findViewById(R.id.chosenPreference2);
        chosenPreference3 = findViewById(R.id.chosenPreference3);
        premadePreference1 = findViewById(R.id.preferenceTextView1);
        premadePreference2 = findViewById(R.id.preferenceTextView2);
        premadePreference3 = findViewById(R.id.preferenceTextView3);

        //upload image - steven
        authData = FirebaseAuth.getInstance().getCurrentUser().getUid();
        upLoadImage = findViewById(R.id.uploadDP);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("profileImages/" + authData);
        circleImageView = findViewById(R.id.profilePageImage);
        userSearchImageReference = FirebaseDatabase.getInstance().getReference("UserIndex")
                .child(authData).child("userSearchImage");

        /*ALEX: Opens a dialog box and sets preferences */
        preferenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfilePage.this);
                mBuilder.setTitle("Pick Your Top  Three Preferences");
                mBuilder.setMultiChoiceItems(preferenceList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked) {
                            counter1++;
                            System.out.println("Counter value is " + counter1);
                            selectedPreferences.add(which);
                        } else {
                            counter1--;
                            System.out.println("Counter value is " + counter1);
                            selectedPreferences.remove((Integer.valueOf(which)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(android.R.string.ok, null);


                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            selectedPreferences.clear();
                            counter1 = 0;
//                            preferenceView.setText("");
                        }
                        premadePreference1.setText("empty");
                        premadePreference2.setText("empty");
                        premadePreference3.setText("empty");
                        myRefPreferenceInfo.child(user.getUid()).child("premadePreferences").setValue(null);
                    }
                });

                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();
                Button b  = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                        String item = "";
                        boolean wantToCloseDialog = false;

                        if(counter1 >3 || counter1 < 3) {
                            Toast.makeText(getApplicationContext(), "You must choose THREE options", Toast.LENGTH_SHORT).show();
                        }

                        if(counter1 ==3) {
                            wantToCloseDialog = true;
                        }

                        if(wantToCloseDialog) {
                            int prefCounter = 0;
                            selectedPreferencesValues = new ArrayList<>();
                            for (int i = 0; i < selectedPreferences.size(); i++) {
                                item = preferenceList[selectedPreferences.get(i)];
                                selectedPreferencesValues.add(preferenceList[selectedPreferences.get(i)]);

                              //  if (i != selectedPreferences.size() - 1) {

                                   // item = item + ", ";
                                    //preferenceView.setText(item);
                                    if(prefCounter == 0) {
                                        premadePreference1.setText(item);
                                    } else if (prefCounter == 1) {
                                        premadePreference2.setText(item);
                                    } else if (prefCounter == 2) {
                                        premadePreference3.setText(item);
                                    }
                                    prefCounter++;
                                //}
                            }
                            myRefPreferenceInfo.child(user.getUid()).child("premadePreferences").setValue(selectedPreferencesValues);
                            mDialog.dismiss();
                        }

                    }
                });

            }
        });


        /*ALEX: Getting the old user data for the premade preferences */
        myRefPreferenceInfo.child(user.getUid()).child("premadePreferences").addListenerForSingleValueEvent(new ValueEventListener() {
            int prefCount = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList onLoadPreferences = new ArrayList<>();
                String item = "";
                System.out.println("In the method");
                     for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        System.out.println("testing" + postSnapshot.getValue());
                        item = "" + postSnapshot.getValue();
                        if (prefCount == 0) {
                            premadePreference1.setText(item);
                        }
                        if(prefCount == 1) {
                            premadePreference2.setText(item);
                        }
                        if(prefCount == 2) {
                            premadePreference3.setText(item);
                        }
                        prefCount++;
                     }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        //Steven setup profile pic
        userSearchImageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
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


        /* Alex Set Chosen Preferences Listeners */

         TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener(){
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    System.out.println("We have hit the enter key");
                    View current = getCurrentFocus();
                    if (current != null) current.clearFocus();
                    current.clearFocus();

                }

                return true;
            }
        };

        chosenPreference1.setOnEditorActionListener(exampleListener);
        chosenPreference2.setOnEditorActionListener(exampleListener);
        chosenPreference3.setOnEditorActionListener(exampleListener);

        /*Sets up data */

        setPreviousChosenPreferenceData();

        //Listener for the upload image button - steven

        upLoadImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chooseImage();
            }
        });

            /* Alex: the bottom navigation view */
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);


        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, displayMetrics);
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }



        /*ALEX: bottom navigation view  onclick*/
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.singlechat) {
                    //
                    System.out.println("singlechat");
                    item.setChecked(true);
                    System.out.println("we reached here");
                    Intent i = new Intent(ProfilePage.this , MainActivity.class);
                    i.putExtra("index","singlechat");  //Position means 0,1,2
                    startActivity(i);
                    return true;
                }

                if (item.getItemId() == R.id.groupchat) {
                    //
                    System.out.println("groupchat");
                    item.setChecked(true);
                    Intent i = new Intent(ProfilePage.this , MainActivity.class);
                    i.putExtra("index","groupchat");  //Position means 0,1,2
                    startActivity(i);
                    return true;
                }

                if (item.getItemId() == R.id.friends) {
                    //
                    System.out.println("friends");
                    item.setChecked(true);
                    Intent i = new Intent(ProfilePage.this , MainActivity.class);
                    i.putExtra("index","friends");  //Position means 0,1,2
                    startActivity(i);

                    return true;
                }

                if (item.getItemId() == R.id.settings) {
                    //
                    System.out.println("setting");
                    item.setChecked(true);


                    return true;
                }

                return false;
            }
        });
        // ALEX: set item to checked
        bottomNavigationView.getMenu().getItem(3).setChecked(true);


        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

    }


    /* Ryan: initiates the user profile */
    private void initUserProfile() {
        profileName.setText(mAuth.getCurrentUser().getEmail());
        userID.setText(mAuth.getCurrentUser().getUid());
    }

    private void removeTopBar() {
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

    public void updateData(View v){
            chosenPreferenceValues = new ArrayList<>();
            chosenPreferenceValues.add("" + chosenPreference1.getText());
            chosenPreferenceValues.add("" + chosenPreference2.getText());
            chosenPreferenceValues.add("" + chosenPreference3.getText());
            myRefPreferenceInfo.child(user.getUid()).child("chosenPreferences").setValue(chosenPreferenceValues);
    }

    public void editTextView(View v) {

        counter1 = 0;
        final boolean[] selected = new boolean[5];
        System.out.println("clicked");
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfilePage.this);
        mBuilder.setTitle("Test");
        mBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i =0; i < selected.length; i++) {
                    if(selected[i]) {
                        selected[i] = false;
                    }
                }
            }
        });
        mBuilder.setMultiChoiceItems(preferenceList, new boolean[]{false, false, false, false, false}, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
               if(isChecked) {
                    if (counter1 < 3) {
                        selected[which] = isChecked;
                        counter1++;
                    } else {
                        Toast.makeText(getApplicationContext(), "You may only select one!", Toast.LENGTH_SHORT);
                    }
                }else {
                    counter1--;
                }
            }
        });
        mBuilder.show();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
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
