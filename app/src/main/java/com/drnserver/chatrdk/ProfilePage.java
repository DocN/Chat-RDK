package com.drnserver.chatrdk;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Window;
        import android.widget.TextView;

        import com.bumptech.glide.Glide;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfilePage";
    //back controller vars
    private boolean backExit;
    private boolean runningBackHold;


    private TextView profileName;
    private TextView userID;
    private FirebaseAuth mAuth;
    private CircleImageView circleImageView;
    private DatabaseReference userIndex;
    private String imgURL;

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

        //Steven setup profile pic
        circleImageView = findViewById(R.id.imageButton);
        userIndex = FirebaseDatabase.getInstance().getReference("UserIndex")
                .child(mAuth.getCurrentUser().getUid()).child("image");

        userIndex.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imgURL = dataSnapshot.getValue().toString();
                Log.e("imageURL2", imgURL);
                Glide.with(getApplicationContext()).load(imgURL).into(circleImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        this.initUserProfile();


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
