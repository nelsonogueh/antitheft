package com.dulceprime.antitheft;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dulceprime.antitheft.other_components.*;
import com.dulceprime.antitheft.services.Background_Service;

public class IntroActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;
    public SQLiteDatabase db;

    String oldPin;
    private SQLController sqlController;

    private static final int MY_PERMISSIONS_REQUEST_CODE = 12345;
    private Context mContext;
    private IntroActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);


        db = openOrCreateDatabase(DBhelper.DB_NAME, MODE_PRIVATE, null);
        sqlController = new SQLController(getApplicationContext());
        oldPin = sqlController.fetchExistingLockPassword();


        // Initializing variables
        mContext = getApplicationContext();
        mActivity = IntroActivity.this;

        // checking if the permission has been granted for Android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }


        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
// IF THE OLD PASSWORD IS EMPTY WHETHER FIRST LAUNCH OR NOT, WE TAKE THE USER TO SET PASSWORD
            if (oldPin.trim().equalsIgnoreCase("")) {
                setNewPassword();
            } else {
                // IF PASSWORD IS NOT EMPTY, WE TAKE THE USER TO UNLOCK PASSWORD ACTIVITY
                grantUserAccessPassword(); // enter your set password
            }
        } else {
            // creating the tables
            DBhelper dBhelper = new DBhelper(getApplicationContext());
            dBhelper.onCreate(db);

            // INITIALIZE THE PASSWORD WITH EMPTY SO THAT WE USE UPDATE FOR IT
            SQLController sqlController = new SQLController(IntroActivity.this);
            ContentValues contentValues = new ContentValues();
            // Just initializing the password table with empty value so that we will use update query
            contentValues.put(DBhelper.PASSWORD, "");
            sqlController.insertNewRecord(DBhelper.PASSWORD_TABLE, contentValues);

            SomeComponents someComponents = new SomeComponents();
            // Just initializing sim details table with empty value so that we will use update query
            ContentValues contentValues2 = new ContentValues();
            // I just want to initialize the table with empty
            contentValues2.put(DBhelper.SERIAL_NUMBER_1, "");
            contentValues2.put(DBhelper.PHONE_NUMBER_1, "");
            contentValues2.put(DBhelper.SERIAL_NUMBER_2, "");
            contentValues2.put(DBhelper.PHONE_NUMBER_2, "");
            contentValues2.put(DBhelper.SIM_UPDATED_DATE, someComponents.dateFullType());
            sqlController.insertNewRecord(DBhelper.SIM_DETAILS_TABLE, contentValues2);


            // Just initializing sim details table with empty value so that we will use update query
            ContentValues contentValues3 = new ContentValues();
            contentValues3.put(DBhelper.GPS_COORDINATE_X, "");
            contentValues3.put(DBhelper.GPS_COORDINATE_Y, "");
            contentValues3.put(DBhelper.SENDING_STATUS, "");
            contentValues3.put(DBhelper.SAVED_DATE, "");
            sqlController.insertNewRecord(DBhelper.SAVED_REPORT_TABLE, contentValues3);


        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        // STARTING THE SERVICE THAT DOES THE REAL WORK
        stopService(new Intent(getApplicationContext(), Background_Service.class));
        Intent activityIntent = new Intent(getApplicationContext(), Background_Service.class);
        startService(activityIntent);


        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPin.trim().equalsIgnoreCase("")) {
                    setNewPassword();
                } else {
                    // IF PASSWORD IS NOT EMPTY, WE TAKE THE USER TO UNLOCK PASSWORD ACTIVITY
                    grantUserAccessPassword(); // enter your set password
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    if (oldPin.trim().equalsIgnoreCase("")) {
                        setNewPassword();
                    } else {
                        // IF PASSWORD IS NOT EMPTY, WE TAKE THE USER TO UNLOCK PASSWORD ACTIVITY
                        grantUserAccessPassword(); // enter your set password
                    }
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void setNewPassword() {
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(IntroActivity.this, LockActivity.class);
        intent.putExtra("intent_request_type", "set_new_pin");
        startActivity(intent);
        finish();
    }

    public void grantUserAccessPassword() {
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(IntroActivity.this, LockActivity.class);
        intent.putExtra("intent_request_type", "unlock_pin");
        startActivity(intent);
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText("Start");
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText("Next");
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    // REQUEST PERMISSION
    protected void checkPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE)
                + ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                + ContextCompat.checkSelfPermission(mActivity, Manifest.permission.RECEIVE_BOOT_COMPLETED)
                + ContextCompat.checkSelfPermission(mActivity, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {


            // Do something, when permissions not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_PHONE_STATE) || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.RECEIVE_BOOT_COMPLETED)
                    || ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.INTERNET)) {
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity);
                builder.setMessage("The permissions enable this app function effectively and accurately.");
                builder.setTitle("Please grant permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                                        Manifest.permission.INTERNET
                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel", null);
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        mActivity,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                                Manifest.permission.INTERNET
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        } else {
            prefManager.setIsPermissionGranted(true);
            // Do something, when permissions are already granted
//            Toast.makeText(mContext,"Permissions already granted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // When request is cancelled, the results array are empty
                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    prefManager.setIsPermissionGranted(true);
                    // Permissions are granted
//                    Toast.makeText(mContext,"Permissions granted.",Toast.LENGTH_SHORT).show();
                } else {
                    // Permissions are denied
                    Toast.makeText(mContext, "Permissions denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
