package com.drnserver.chatrdk;

        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.TextView;
        import android.widget.Toast;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.firebase.auth.FirebaseAuth;

public class mainframe extends AppCompatActivity {
    private static final String TAG = "mainframe";
    //back controller vars
    private boolean backExit;
    private boolean runningBackHold;

    private TextView profileName;
    private TextView userID;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.removeTopBar();
        setContentView(R.layout.activity_mainframe);
        //store local auth reference
        mAuth = MainActivity.mAuth;
        //declare objects in view
        profileName = (TextView) findViewById(R.id.profileName);
        userID = (TextView) findViewById(R.id.tempIDCheck);
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
