package com.drnserver.chatrdk.ui;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drnserver.chatrdk.model.UserIndex;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.drnserver.chatrdk.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSearchFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Tab1Fragment";

    //Variables - Steven
    private CircleImageView circleImageView;
    private EditText searchText;
    private RecyclerView resultList;
    private DatabaseReference userIndex;

/*    private ImageButton btnTEST;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;*/



    String id = "CZ1Bcon808dHrAJAl6ExxulmgFZ2";
    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_search_fragment, container, false);

        /*        View view = inflater.inflate(R.layout.user_search_fragment, container, false);




        //btnTEST = (ImageButton) view.findViewById(R.id.searchButton);
        *//* work in progress
        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        showData(dataSnapshot);
                    }

                mDatabase = FirebaseDatabase.getInstance().getReference().child("user/");
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1", Toast.LENGTH_SHORT).show();
            }
        });
        *//*
        return view;*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Variable init - Steven
        userIndex = FirebaseDatabase.getInstance().getReference("UserIndex");
        searchText = getView().findViewById(R.id.searchText);
        circleImageView = getView().findViewById((R.id.searchButton));
        resultList = getView().findViewById(R.id.searchList);
        //List in recyclerview - Steven
        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(getContext()));
        //Listener - Steven
        circleImageView.setOnClickListener(this);



    }

    //onClick, calls search function - Steven
    @Override
    public void onClick(View view) {
        String searchUser = searchText.getText().toString();
        firebaseUserSearch(searchUser);

    }

    //Seach funtion - Steven
    private void firebaseUserSearch(String searchUser) {

        Toast.makeText(getContext(), "Started Search", Toast.LENGTH_LONG).show();
        //Search firebse - Steven
        Query firebaseSearchQuery =
                userIndex.orderByChild("nameIndex").startAt(searchUser.toLowerCase()).endAt(searchUser.toLowerCase() + "\uf8ff");

        //Custom adaptor to bind data from the Firebase to list ui
        //https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md
        FirebaseRecyclerAdapter<UserIndex,UsersViewHolder>
                firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UserIndex, UsersViewHolder>(
                UserIndex.class, R.layout.list_layout, UsersViewHolder.class, firebaseSearchQuery

        ) {
            @Override
            protected void populateViewHolder
                    (UsersViewHolder viewHolder, UserIndex userIndex, int position) {
                viewHolder.setDetails(getContext(), userIndex.nameIndex, userIndex.status,
                        userIndex.image);

            }
        };
        resultList.setAdapter(firebaseRecyclerAdapter);
    }

    //Static class sets the detail to the view - Steven
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        //Set the detail of the list view - Steven
        public void setDetails(Context context, String userName, String userStatus, String userImage) {

            TextView user_name = (TextView) mView.findViewById(R.id.name_text);
            TextView user_status = (TextView) mView.findViewById(R.id.status_text);
            ImageView user_image = (ImageView) mView.findViewById(R.id.profile_image);

            user_name.setText(userName);
            user_status.setText(userStatus);

            //add user image
            Glide.with(context).load(userImage).into(user_image);


        }
    }

}


