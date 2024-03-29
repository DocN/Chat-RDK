package com.drnserver.chatrdk;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.drnserver.chatrdk.model.User;
import com.drnserver.chatrdk.data.StaticConfig;
import com.google.firebase.database.ValueEventListener;

import com.drnserver.chatrdk.data.SharedPreferenceHelper;
import com.drnserver.chatrdk.model.UserIndex;


import java.util.HashMap;

public class loginActivity extends AppCompatActivity {
    private static final int MIN_PASSWORD_LENGTH = 5;
    private static final String TAG = "MainActivity";
    //sign on RC
    private static final int RC_SIGN_IN = 123;
    //view variables
    private EditText userField;
    private EditText passwordField;
    private Button loginButton;
    private Button signupButton;

    public static FirebaseAuth mAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference myRefLocationZ = myRef.child("locationZ");
    DatabaseReference myRefPreferenceInfo = myRef.child("preferenceInfo");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        //init view variables
        userField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.SignupButton);

        //init auth instance
        mAuth = FirebaseAuth.getInstance();

        //init button lister for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //store the userfield and password field for account creation
                String currentUser = userField.getText().toString();
                String currentPassword = passwordField.getText().toString();
                //try to make account
                if(validateCredentials(currentUser, currentPassword)) {
                    System.out.println("here brah");
                    loginPortal(currentUser, currentPassword);
                }
                else {
                    Toast.makeText(loginActivity.this, "Failed to create account, invalid username or password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //init button lister for login button
        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //store the userfield and password field for account login
                String currentUser = userField.getText().toString();
                String currentPassword = passwordField.getText().toString();

                //try to make account
                if(validateCredentials(currentUser, currentPassword)) {
                    createUser(currentUser, currentPassword);
                }
                else {
                    Toast.makeText(loginActivity.this, "Failed to create account, invalid username or password",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }


    /**
     * validateCreateCredentials - the function validates a username and password format for usage
     * @param username
     * @param password
     * @return
     */
    private boolean validateCredentials(String username, String password) {
        //check if it's null first D:
        if(username == null || password == null) {
            return false;
        }
        //check if it's a valid email address and then check password length
        if(isValidEmail(username)) {
            if((password.length() > MIN_PASSWORD_LENGTH)) {
                return true;
            }
        }
        return false;
    }

    /**
     * isValidEmail - internet reference of isValidEmail for check if an email address is valid
     * @param target
     * @return
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    /**
     * createUser - creates a firebase user with default system email, password setup
     * @param email - the email address for login
     * @param password - the password duh..
     */
    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            initNewUserInfo(user);
                            initUserSearchIndex(user);
                            /* ALEX: default distance / lat / lon pushed */
                            myRefLocationZ.child(user.getUid()).child("dist").setValue(100);
                            myRefLocationZ.child(user.getUid()).child("lat").setValue(0);
                            myRefLocationZ.child(user.getUid()).child("lon").setValue(0);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            System.out.println(task.getException().toString());
                            Toast.makeText(loginActivity.this, "Failed. " + task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    /**
     * loginPortal - login portal call to login
     * @param email - credential email
     * @param password - credential password
     */
    private void loginPortal(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(loginActivity.this, "Succsex login.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            StaticConfig.UID = user.getUid();
                            //
                            saveUserInfo();
                            Intent myIntent = new Intent(loginActivity.this, MainActivity.class);
                            loginActivity.this.startActivity(myIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(loginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    void saveUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("user/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap hashUser = (HashMap) dataSnapshot.getValue();
                User userInfo = new User();
                userInfo.name = (String) hashUser.get("name");
                userInfo.email = (String) hashUser.get("email");
                userInfo.avata = (String) hashUser.get("avata");
                SharedPreferenceHelper.getInstance(loginActivity.this).saveUserInfo(userInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void initNewUserInfo(FirebaseUser user) {
        User newUser = new User();
        newUser.email = user.getEmail(); //need to implement filter
        newUser.name = user.getEmail().substring(0, user.getEmail().indexOf("@"));
        newUser.avata = StaticConfig.STR_DEFAULT_BASE64;
        FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser);
    }

    //Steven - UserIndexNode
    void initUserSearchIndex(FirebaseUser user){
        //New node for searching users - Steven
        String email = user.getEmail();
        String nameIndex = user.getEmail().substring(0, user.getEmail().indexOf("@"));;
        String phone = ""; //need to implement filter
        String image ="";
        String uid = user.getUid();
        //PlaceHolder
        String status = "Hi!" + " My name is " + nameIndex + ". Come chat with me!";
        UserIndex userIndex = new UserIndex( email, image, nameIndex, phone,status, uid);
        FirebaseDatabase.getInstance().getReference()
                .child("UserIndex/" + user.getUid()).setValue(userIndex);
    }
}
