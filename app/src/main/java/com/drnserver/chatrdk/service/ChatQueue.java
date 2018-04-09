package com.drnserver.chatrdk.service;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;

import com.drnserver.chatrdk.loginActivity;
import com.drnserver.chatrdk.model.GroupData;
import com.drnserver.chatrdk.model.QueueData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.drnserver.chatrdk.loginActivity.mAuth;

/**
 * Created by DrN on 4/1/2018.
 */

public class ChatQueue {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference chatReqRef = myRef.child("chatQueue");
    DatabaseReference locationZRef = myRef.child("locationZ");
    DatabaseReference preferenceInfoRef = myRef.child("preferenceInfo");

    private FirebaseAuth mAuth;
    FirebaseUser user;
    private String myUid;
    private QueueData qData;

    private static final String LAT_VAL = "Lat";
    private static final String LON_VAL = "Lon";
    private static final String RANGE_VAL = "MaxRange";
    private static final String PREF_VAL = "Preferences";
    private static final String USER_VAL = "UserID";


    private ArrayList<GroupData> groupDataList;
    private int numberOfChats;
    private boolean inQueue;

    public ChatQueue() {
        //init database
        mAuth = loginActivity.mAuth;
        System.out.println(locationZRef);
        user = mAuth.getCurrentUser();
        myUid = user.getUid();
        System.out.println(myUid);
        qData = new QueueData();
        numberOfChats = 10;
        inQueue = false;
        groupDataList = new ArrayList<GroupData>();

        //gather data
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

    public int getNumberOfChats() {
        return numberOfChats;
    }

    public void setNumberOfChats(int numberOfChats) {
        this.numberOfChats = numberOfChats;
    }

    public void enterQueue() {
        try {
            chatReqRef.child(user.getUid()).child(LAT_VAL).setValue(qData.getLat());
            chatReqRef.child(user.getUid()).child(LON_VAL).setValue(qData.getLon());
            chatReqRef.child(user.getUid()).child(RANGE_VAL).setValue(qData.getDistance());
            chatReqRef.child(user.getUid()).child(USER_VAL).setValue(user.getUid());
            chatReqRef.child(user.getUid()).child("TimeCreated").setValue("1239494");
            chatReqRef.child(user.getUid()).child(PREF_VAL).setValue(qData.returnCombinedPreferences());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void getGroupData(final String groupName) {
        final DatabaseReference group = myRef.child("group");
        //collect chosenPreferences
        group.child(groupName).child("MatchedPreferences").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    boolean alreadyExists = false;
                    GroupData currentGroupData = new GroupData(groupName);
                    for(int i =0; i < groupDataList.size(); i++) {
                        if(groupDataList.get(i).getGroupName().equals(groupName)) {
                            alreadyExists =true;
                            currentGroupData = groupDataList.get(i);
                            break;
                        }
                    }
                    if(alreadyExists == false){
                        groupDataList.add(currentGroupData);
                    }
                    ArrayList<String> preferences = new ArrayList<String>();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String currentVal = postSnapshot.getValue().toString();
                        preferences.add(currentVal);

                    }
                    currentGroupData.setPreferences(preferences);
                } catch(Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<GroupData> getGroupDataList() {
        return groupDataList;
    }

    public void setGroupDataList(ArrayList<GroupData> groupDataList) {
        this.groupDataList = groupDataList;
    }


    public boolean isInQueue() {
        return inQueue;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }

}
