package com.drnserver.chatrdk.service;

import com.drnserver.chatrdk.loginActivity;
import com.drnserver.chatrdk.model.QueueData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.drnserver.chatrdk.loginActivity.mAuth;

/**
 * Created by DrN on 4/1/2018.
 */

public class ChatQueue {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference chatReqRef = myRef.child("chatReq");
    DatabaseReference locationZRef = myRef.child("locationZ");
    DatabaseReference preferenceInfoRef = myRef.child("preferenceInfo");
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private String myUid;
    private QueueData qData;

    private static final String LAT_VAL = "lat";
    private static final String LON_VAL = "lon";
    private static final String RANGE_VAL = "range";
    private static final String PREF_VAL = "preferences";

    public ChatQueue() {
        //init database
        mAuth = loginActivity.mAuth;
        System.out.println(locationZRef);
        user = mAuth.getCurrentUser();
        myUid = user.getUid();
        System.out.println(myUid);
        qData = new QueueData();
        gatherDataInit();
    }

    public void gatherDataInit() {
        //collect chosenPreferences
        preferenceInfoRef.child(myUid).child("chosenPreferences").addListenerForSingleValueEvent(new ValueEventListener() {
            int prefCount = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    System.out.println("received new chosen chosenPreferences Data");
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String currentVal = postSnapshot.getValue().toString();
                        if(qData.checkExistchosenPreferences(currentVal) == false) {
                            qData.getChosenPreferences().add(currentVal);
                        }
                    }
                    System.out.println(qData.getChosenPreferences().toString());
                } catch(Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //collect preferences
        preferenceInfoRef.child(myUid).child("premadePreferences").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    System.out.println("received new chosen premadePreference Data");
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String currentVal = postSnapshot.getValue().toString();
                        if(qData.checkExistPremadePreference(currentVal) == false) {
                            qData.getPremadePreferences().add(currentVal);
                        }
                    }
                    System.out.println(qData.getPremadePreferences().toString());
                }catch(Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //collecting lat long values
        //addListenerForSingleValueEvent or addValueEventListener depending on if we want to keep listening
        locationZRef.child(myUid).child("lat").addListenerForSingleValueEvent(new ValueEventListener() {
            int prefCount = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    System.out.println("collected Lat");
                    qData.setLat(dataSnapshot.getValue().toString());
                    System.out.println(qData.getLat());
                } catch(Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        locationZRef.child(myUid).child("lon").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    System.out.println("collected lon");
                    qData.setLon(dataSnapshot.getValue().toString());
                    System.out.println(qData.getLon());
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        locationZRef.child(myUid).child("dist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    System.out.println("collected dist");
                    qData.setDistance(dataSnapshot.getValue().toString());
                    System.out.println(qData.getDistance());
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void enterQueue() {
        chatReqRef.child(user.getUid()).child(LAT_VAL).setValue(qData.getLat());
        chatReqRef.child(user.getUid()).child(LON_VAL).setValue(qData.getLon());
        chatReqRef.child(user.getUid()).child(RANGE_VAL).setValue(qData.getLat());
        chatReqRef.child(user.getUid()).child(PREF_VAL).setValue(qData.returnCombinedPreferences());
    }
}