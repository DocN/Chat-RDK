package com.drnserver.chatrdk;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class DialogUserSearch extends AppCompatDialogFragment {
    private TextView userName;
    private TextView status;
    private CircleImageView userImage;
    //Strings
    private String userNameInfo;
    private String statusInfo;
    private String imageUrlInfo;
    private String currentUID;
    private String addUID;
    private DatabaseReference userFriendList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.search_user_profile_diaglogue, null);

        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
        userFriendList = FirebaseDatabase.getInstance().getReference().child("friend");
        Bundle userInfo = getArguments();
        userNameInfo = userInfo.getString("userName");
        imageUrlInfo = userInfo.getString("imageUrl");
        statusInfo = userInfo.getString("userStatus");
        addUID = userInfo.getString("userUID");

        userName = view.findViewById(R.id.dialog_user_name);
        status = view.findViewById((R.id.dialog_user_status));
        userImage = view.findViewById((R.id.dialog_profile_image));


        //set the add and cancel button
        builder.setView(view).setTitle("Add User")
                .setPositiveButton("Add User", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance().getReference().child("friend").child(currentUID)
                        .orderByValue().equalTo(addUID).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("friend").child(currentUID).push().setValue(addUID);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        })
          .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        userName.setText(userNameInfo);
        status.setText(statusInfo);
        Glide.with(getContext()).load(imageUrlInfo).into(userImage);

        return builder.create();
    }
}
