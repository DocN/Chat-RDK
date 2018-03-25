package com.drnserver.chatrdk.ui;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.drnserver.chatrdk.DialogUserSearch;
import com.drnserver.chatrdk.MainActivity;
import com.drnserver.chatrdk.adaptor.UserSearchAdaptor;
import com.drnserver.chatrdk.model.UserIndex;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.drnserver.chatrdk.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class ProfileSearchFragment extends Fragment implements
        UserSearchAdaptor.OnItemClickListener {
    private static final String TAG = "Upload";

    //Variables - Steven
    private FloatingActionButton searchButton;
    private FloatingActionButton upLoadImage;
    private EditText searchText;
    private RecyclerView resultList;
    private ArrayList<UserIndex> sList;
    private DatabaseReference userIndex;
    private UserSearchAdaptor userSearchAdaptor;
    private Uri filePath;
    private String authData;
    private final int PICK_IMAGE_REQUEST = 71;
    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;


    String id = "CZ1Bcon808dHrAJAl6ExxulmgFZ2";
    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_search_fragment, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Variable init - Steven
        userIndex = FirebaseDatabase.getInstance().getReference("UserIndex");
        searchText = getView().findViewById(R.id.searchText);
        searchButton = getView().findViewById((R.id.searchButton));
        upLoadImage = getView().findViewById(R.id.uploadDP);
        storage = FirebaseStorage.getInstance();
        authData = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = storage.getReference().child("profileImages/" + authData);

        //recyclerview - Steven
        resultList = getView().findViewById(R.id.searchList);
        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(getContext()));

        //Listener - Steven
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String searchWord = searchText.getText().toString();
                firebaseUserSearch(searchWord);

            }
        });

        upLoadImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    //choose the image to upload
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            uploadImage();
        }
    }

    //upload the image to firebase storage
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            setProfileImage();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void setProfileImage() {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri url) {
                userIndex.child(authData+"/userSearchImage").setValue(url.toString());
                //Log.d(TAG, url.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    //search firebase
    private void firebaseUserSearch(final String searchUser) {
        //Arraylist
        sList = new ArrayList<>();
        Toast.makeText(getContext(), "Started Search", Toast.LENGTH_LONG).show();
        //Search firebse - Steven
        Query firebaseSearchQuery =
                userIndex.orderByChild("userSearchName").startAt(searchUser.toLowerCase())
                        .endAt(searchUser.toLowerCase() + "\uf8ff");

        firebaseSearchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                bindToList(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                bindToList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                bindToList(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    //bind users to the arraylist
    private void bindToList(DataSnapshot dataSnapshot) {
        Toast.makeText(getContext(), "Searching", Toast.LENGTH_LONG).show();
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()) {
            String email = (String) ((DataSnapshot) iterator.next()).getValue();
            String image_url = (String) ((DataSnapshot) iterator.next()).getValue();
            String username = (String) ((DataSnapshot) iterator.next()).getValue();
            String phone = (String) ((DataSnapshot) iterator.next()).getValue();
            String status = (String) ((DataSnapshot) iterator.next()).getValue();
            String uid = (String) ((DataSnapshot) iterator.next()).getValue();
            UserIndex user = new UserIndex (email, image_url,username, phone, status, uid);
            if ((user.getUserSearchUID()).equals(authData)){

            } else {
                sList.add(user);
            }

        }
        startAdapter();
    }

    //Start adaptor to populate list
    private void startAdapter() {
        userSearchAdaptor = new UserSearchAdaptor(getContext(), sList);
        resultList.setAdapter(userSearchAdaptor);
        userSearchAdaptor.setOnItemClickListener(this);
    }

    //handle list item click
    @Override
    public void onItemClick(int position) {
        openDialog(position);
    }

    //Dialog to add friend
    public void openDialog(int position){
        UserIndex clickedItem = sList.get(position);
        //bundle the user info to send to dialog
        Bundle userInfo = new Bundle();
        userInfo.putString("imageUrl", clickedItem.getUserSearchImage());
        userInfo.putString("userName", clickedItem.getUserSearchName());
        userInfo.putString("userStatus", clickedItem.getUserSearchStatus());
        userInfo.putString("userUID", clickedItem.getUserSearchUID());
        //make a dialog
        DialogUserSearch dialogUserSearch = new DialogUserSearch();
        dialogUserSearch.setArguments(userInfo);
        dialogUserSearch.show(getFragmentManager(), "user search dialog");
    }
}