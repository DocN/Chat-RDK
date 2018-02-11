package com.drnserver.chatrdk;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;

public class mainframe extends AppCompatActivity {
    private static final String TAG = "mainframe";
    //back controller vars
    private boolean backExit;
    private boolean runningBackHold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.removeTopBar();
        setContentView(R.layout.activity_mainframe);


    }


    private void removeTopBar() {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * setupBackControl - sets up the back controller to prevent exit on back press
     */
    private void setupBackControl() {
        this.backExit = true;
        this.runningBackHold = true;
    }

    /**
     * onBackPressed supresses exit call when back is pressed
     */
    @Override
    public void onBackPressed() {
        if(backExit) {
            backExit = false;
            Toast.makeText(mainframe.this, "Press back again to exit "  ,
                    Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler();
            if(!runningBackHold) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backExit = true;
                        runningBackHold = true;
                        //Do something after 100ms
                    }
                }, 1000);
            }

        }
        else {
            //passed the back to exit program.
            moveTaskToBack(true);
        }

    }

}
