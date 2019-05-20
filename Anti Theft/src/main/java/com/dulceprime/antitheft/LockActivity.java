package com.dulceprime.antitheft;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dulceprime.antitheft.other_components.DBhelper;
import com.dulceprime.antitheft.other_components.PrefManager;
import com.dulceprime.antitheft.other_components.SQLController;
import com.dulceprime.antitheft.services.Background_Service;

import java.util.ArrayList;
import java.util.List;

public class LockActivity extends AppCompatActivity {

    LinearLayout unlock_pin_container, set_new_pin_container, modify_pin_container;

    String intent_request_type = "set_new_password";

    private PrefManager prefManager;
    public SQLiteDatabase db;
    SQLController sqlController;
    String oldPin;

    private static final int MY_PERMISSIONS_REQUEST_CODE = 12345;
    private Context mContext;
    private LockActivity mActivity;


    TextInputEditText unlock_pinInputET, new_pin_enter_pin, new_pin_confirm_pin, new_pin_security_question, new_pin_security_answer, modify_pin_old_pin, modify_pin_new_pin, modify_pin_confirm_pin;
    Button unlock_pin_submit_btn, new_pin_submit_btn, modify_pin_submit_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);




        db = openOrCreateDatabase(DBhelper.DB_NAME, MODE_PRIVATE, null);
        sqlController = new SQLController(getApplicationContext());
        oldPin = sqlController.fetchExistingLockPassword();


        // Initializing variables
        mContext = getApplicationContext();
        mActivity = LockActivity.this;



        db = openOrCreateDatabase(DBhelper.DB_NAME, MODE_PRIVATE, null);
        sqlController = new SQLController(getApplicationContext());
        prefManager = new PrefManager(this);


        // checking if the permission has been granted for Android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        unlock_pin_container = (LinearLayout) findViewById(R.id.unlock_pin_container);
        set_new_pin_container = (LinearLayout) findViewById(R.id.set_new_pin_container);
        modify_pin_container = (LinearLayout) findViewById(R.id.modify_pin_container);


        String intent_request_type = getIntent().getStringExtra("intent_request_type");


        switch (intent_request_type) {
            case "unlock_pin": {
                unlockPIN(); // Calling the unlockPin() method when the user needs to get access to the app
                break;
            }
            case "set_new_pin": {
                setNewPIN(); // Calling the setNewPin() method when the user wants to set PIN
                break;
            }
            case "modify_pin": {
                modifyPIN(); // Calling the modifyPIN() method when the user requests to modify PIN
                break;
            }
            default: {
                setNewPIN();
            }
        }

//        Toast.makeText(getApplicationContext(), "Type: "+intent_request_type, Toast.LENGTH_SHORT).show();
    }

    public void unlockPIN() {
        // Handles everything that happens at the enter PIN activity
        // TOGGLE VISIBILITY OF THE CONTAINERS THAT HOLD THE FORMS ON THE LAYOUT RESOURCE.
        unlock_pin_container.setVisibility(View.VISIBLE);
        set_new_pin_container.setVisibility(View.GONE);
        modify_pin_container.setVisibility(View.GONE);

        unlock_pinInputET = (TextInputEditText) findViewById(R.id.unlock_pinInputET);
        unlock_pin_submit_btn = (Button) findViewById(R.id.unlock_pin_submit_btn);
        unlock_pin_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We get the old pin anytime the user clicks on the submit button
                oldPin = sqlController.fetchExistingLockPassword().trim();
                String fieldPin = unlock_pinInputET.getText().toString().trim();

                if (oldPin.equalsIgnoreCase(fieldPin)) {
//                    Toast.makeText(LockActivity.this, "Pin correct", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    unlock_pinInputET.setText("");
                    unlock_pinInputET.setError("PIN incorrect! Please try again");
                }
            }
        });

    }

    public void setNewPIN() {

        // Handles everything that happens at the set PIN activity
        unlock_pin_container.setVisibility(View.GONE);
        set_new_pin_container.setVisibility(View.VISIBLE);
        modify_pin_container.setVisibility(View.GONE);


        new_pin_enter_pin = (TextInputEditText) findViewById(R.id.new_pin_enter_pin);
        new_pin_confirm_pin = (TextInputEditText) findViewById(R.id.new_pin_confirm_pin);
        new_pin_security_question = (TextInputEditText) findViewById(R.id.new_pin_security_question);
        new_pin_security_answer = (TextInputEditText) findViewById(R.id.new_pin_security_answer);
        new_pin_submit_btn = (Button) findViewById(R.id.new_pin_submit_btn);
        new_pin_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We get the old pin anytime the user clicks on the submit button
                oldPin = sqlController.fetchExistingLockPassword().trim();


                String fieldNewPin = "";
                String fieldConfirmPin = "";
                String fieldSecurityQuestion = "";
                String fieldSecurityAnswer = "";

                fieldNewPin = new_pin_enter_pin.getText().toString().trim();
                fieldConfirmPin = new_pin_confirm_pin.getText().toString().trim();
                fieldSecurityQuestion = new_pin_security_question.getText().toString().trim();
                fieldSecurityAnswer = new_pin_security_answer.getText().toString().trim();


                if (fieldNewPin.equalsIgnoreCase(fieldConfirmPin) && (!fieldNewPin.equalsIgnoreCase(""))) {
                    if (fieldSecurityQuestion.equalsIgnoreCase("")) {
                        new_pin_security_question.setError("Security question cannot be empty!");
                        return;
                    }
                    if (fieldSecurityAnswer.equalsIgnoreCase("")) {
                        new_pin_security_answer.setError("Security answer cannot be empty!");
                        return;
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBhelper.PASSWORD, fieldNewPin);
                    contentValues.put(DBhelper.SECURITY_QUESTION, fieldSecurityQuestion);
                    contentValues.put(DBhelper.SECURITY_ANSWER, fieldSecurityAnswer);
                    long update = sqlController.updateTable(DBhelper.PASSWORD_TABLE, contentValues, DBhelper.PASSWORD_ID, "1");
                    if ((update + "").equalsIgnoreCase("1")) {
//                        Toast.makeText(LockActivity.this, "PIN set successfully! ", Toast.LENGTH_SHORT).show();
                        new_pin_enter_pin.setText("");
                        new_pin_confirm_pin.setText("");
                        new_pin_security_question.setText("");
                        new_pin_security_answer.setText("");


                        // UPDATE THE SERIAL NUMBER(S) WHEN THE PROTECTION IS TURNED ON
                        String deviceSerial1 = "";
                        String deviceSerial2 = "";

                        try {
                            deviceSerial1 = getDeviceSerialNumber1();
                            deviceSerial2 = getDeviceSerialNumber2();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put(DBhelper.SERIAL_NUMBER_1, deviceSerial1);
                        contentValues2.put(DBhelper.SERIAL_NUMBER_2, deviceSerial2);

                        long update2 = sqlController.updateTable(DBhelper.SIM_DETAILS_TABLE, contentValues2, DBhelper.SIM_TABLE_ID, "1");
                        if ((update2 + "").equalsIgnoreCase("1")) {

                            prefManager.setIsFreshRequest(true);
//                            Toast.makeText(LockActivity.this, "Serial number updated successfully! ", Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(LockActivity.this, "Could not modify serial number", Toast.LENGTH_SHORT).show();
                        }


                        prefManager.setIsProtected(true); // PROTECTED THE DEVICES

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LockActivity.this, "Could not set PIN", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    modify_pin_new_pin.setError("PIN do not match! Try again.");
                }
            }
        });
    }

    public void modifyPIN() {
        // Handles everything that happens at the modify PIN activity
        unlock_pin_container.setVisibility(View.GONE);
        set_new_pin_container.setVisibility(View.GONE);
        modify_pin_container.setVisibility(View.VISIBLE);

        modify_pin_old_pin = (TextInputEditText) findViewById(R.id.modify_pin_old_pin);
        modify_pin_new_pin = (TextInputEditText) findViewById(R.id.modify_pin_new_pin);
        modify_pin_confirm_pin = (TextInputEditText) findViewById(R.id.modify_pin_confirm_pin);
        modify_pin_submit_btn = (Button) findViewById(R.id.modify_pin_submit_btn);

        modify_pin_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We get the old pin anytime the user clicks on the submit button
                oldPin = sqlController.fetchExistingLockPassword().trim();

                String fieldOldPin = modify_pin_old_pin.getText().toString().trim();
                String fieldNewPin = modify_pin_new_pin.getText().toString().trim();
                String fieldConfirmPin = modify_pin_confirm_pin.getText().toString().trim();

                if (oldPin.equalsIgnoreCase(fieldOldPin)) {
                    if (fieldNewPin.equalsIgnoreCase(fieldConfirmPin)) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBhelper.PASSWORD, fieldNewPin);
                        long update = sqlController.updateTable(DBhelper.PASSWORD_TABLE, contentValues, DBhelper.PASSWORD_ID, "1");
                        if ((update + "").equalsIgnoreCase("1")) {
                            Toast.makeText(LockActivity.this, "PIN modified successfully! ", Toast.LENGTH_SHORT).show();

                            modify_pin_old_pin.setText("");
                            modify_pin_new_pin.setText("");
                            modify_pin_confirm_pin.setText("");

                        } else {
                            Toast.makeText(LockActivity.this, "Could not modify PIN", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        modify_pin_new_pin.setError("PIN do not match! Try again.");
                    }
                } else {
                    // if old pin from database not equal to the entered one
                    modify_pin_old_pin.setError("Old PIN is incorrect! Try again.");
                }
            }
        });
    }


    public String getDeviceSerialNumber1() {
// Getting the current sim 1 serial number
        try {
            int subId = getSimInfo(getApplicationContext()).get(0).getSubscriptionId();
            String subscriberId1 = subId + "";
            return subscriberId1;
        } catch (Exception e) {
            return "";
        }
    }

    public String getDeviceSerialNumber2() {
// Getting the current sim 2 serial number
        try {
            int subId = getSimInfo(getApplicationContext()).get(1).getSubscriptionId();
            String subscriberId2 = subId + "";
            return subscriberId2;
        } catch (Exception e) {
            return "";
        }
    }


    // getSimInfo
    public List<SubscriptionInfo> getSimInfo(Context context) {
        SubscriptionManager subManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> subscriptionInfoList = new ArrayList<>();
        subscriptionInfoList = subManager.getActiveSubscriptionInfoList();
        Log.d("LIST LIST", subscriptionInfoList.toString());
        if (subscriptionInfoList == null) {
            Toast.makeText(context, "address not found", Toast.LENGTH_SHORT).show();
        }
        return subscriptionInfoList;
    }

    // getNetworkOperator
    public List<String> getNetworkOperator(final Context context) {
        // Get System TELEPHONY service reference
        List<String> carrierNames = new ArrayList<>();
        try {
            final String permission = android.Manifest.permission.READ_PHONE_STATE;
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) && (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)) {
                final List<SubscriptionInfo> subscriptionInfos = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
                for (int i = 0; i < subscriptionInfos.size(); i++) {
                    carrierNames.add(subscriptionInfos.get(i).getCarrierName().toString());
                }

            } else {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                // Get carrier name (Network Operator Name)
                carrierNames.add(telephonyManager.getNetworkOperatorName());
                //enter code here
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carrierNames;
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




}
