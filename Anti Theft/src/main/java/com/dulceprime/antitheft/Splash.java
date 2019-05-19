package com.dulceprime.antitheft;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.dulceprime.antitheft.other_components.*;

public class Splash extends AppCompatActivity {

    private PrefManager prefManager;
    public SQLiteDatabase db;
    private String oldPin;
    private SQLController sqlController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        changeStatusBarColor(); // Change the status bar color

        db = openOrCreateDatabase(DBhelper.DB_NAME, MODE_PRIVATE, null);
        sqlController = new SQLController(getApplicationContext());

        oldPin = sqlController.fetchExistingLockPassword();
        // Checking for first time launch  and create all the tables
        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            DBhelper dBhelper = new DBhelper(getApplicationContext());
            dBhelper.onCreate(db);
        }


        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(6000);  // wait for 6 seconds before you do the following things



                    if (!prefManager.isFirstTimeLaunch()) {
// IF THE OLD PASSWORD IS EMPTY WHETHER FIRST LAUNCH OR NOT, WE TAKE THE USER TO SET PASSWORD
                        if (!(oldPin.trim().equalsIgnoreCase(""))) {
                            // IF PASSWORD IS NOT EMPTY, WE TAKE THE USER TO UNLOCK PASSWORD ACTIVITY
                            grantUserAccessPassword(); // enter your set password
                            return;
                        }
                    }


                    Intent i = new Intent(getApplicationContext(), IntroActivity.class);
                    startActivity(i);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }; // ends the Thread

        // calling the thread
        myThread.start();
    }


    public void grantUserAccessPassword() {
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(Splash.this, LockActivity.class);
        intent.putExtra("intent_request_type", "unlock_pin");
        startActivity(intent);
        finish();
    }


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
