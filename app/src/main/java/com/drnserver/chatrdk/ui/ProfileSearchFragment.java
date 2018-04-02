package com.drnserver.chatrdk.ui;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.drnserver.chatrdk.DialogUserSearch;
import com.drnserver.chatrdk.adaptor.UserSearchAdaptor;
import com.drnserver.chatrdk.model.UserIndex;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.drnserver.chatrdk.R;

import java.util.ArrayList;
import java.util.Iterator;

public class ProfileSearchFragment extends Fragment implements
        UserSearchAdaptor.OnItemClickListener {
    private static final String TAG = "Upload";

    //Variables - Steven
    private FloatingActionButton searchButton;

    private EditText searchText;
    private RecyclerView resultList;
    private ArrayList<UserIndex> sList;
    private DatabaseReference userIndex;
    private UserSearchAdaptor userSearchAdaptor;
    private String authData;

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
        authData = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //recyclerview - Steven
        resultList = getView().findViewById(R.id.searchList);
        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(getContext()));

        //Listener for user search button - Steven
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String searchWord = searchText.getText().toString();
                firebaseUserSearch(searchWord);

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
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }

    //bind users to the arraylist
    private void bindToList(DataSnapshot dataSnapshot) {
        Toast.makeText(getContext(), "Searching", Toast.LENGTH_LONG).show();
        Iterator iterator = dataSnapshot.getChildren().iterator();
        //iterate through each data element and store into an object
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
                //set the object to the list
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
        //bundle the user info to send to the custom dialog
        Bundle userInfo = new Bundle();
        userInfo.putString("imageUrl", clickedItem.getUserSearchImage());
        userInfo.putString("userName", clickedItem.getUserSearchName());
        userInfo.putString("userStatus", clickedItem.getUserSearchStatus());
        userInfo.putString("userUID", clickedItem.getUserSearchUID());
        //start and show the dialog
        DialogUserSearch dialogUserSearch = new DialogUserSearch();
        dialogUserSearch.setArguments(userInfo);
        dialogUserSearch.show(getFragmentManager(), "user search dialog");
    }
}