package com.dulceprime.antitheft;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dulceprime.antitheft.other_components.DBhelper;
import com.dulceprime.antitheft.other_components.PrefManager;
import com.dulceprime.antitheft.other_components.SQLController;
import com.dulceprime.antitheft.other_components.SomeComponents;
import com.dulceprime.antitheft.services.Background_Service;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List recoveryRowId;
    List recoveryName;
    List recoveryPhone;
    List recoveryEmail;

    public static String selectedItemId = "";

    ListView recoveryDetailsLV;


    private DBhelper DBhelper;
    private Context ourcontext;
    public SQLiteDatabase db;

    SQLController sqlController;
    String oldPin;

    TextView view_all_recovery_detailsTV;

    PrefManager prefManager;

    private LocationManager locationManager;
    private LocationListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = openOrCreateDatabase(DBhelper.DB_NAME, MODE_PRIVATE, null);
        sqlController = new SQLController(getApplicationContext());
        prefManager = new PrefManager(this);


        setViewBasedOnProtectionStatus();  // This sets either the protection on or off view based on received values

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddRecoveryDetails.class);
                intent.putExtra("intent_request_type", "add_contact");
                startActivity(intent);
            }
        });

        view_all_recovery_detailsTV = (TextView) findViewById(R.id.view_all_recovery_detailsTV);
        view_all_recovery_detailsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeviceRecoveryDetails.class);
                startActivity(intent);
            }
        });

        displayContactDetailsRecords(); // Fetch and display the results

        // Starting the background service
        stopService(new Intent(getApplicationContext(),Background_Service.class));
        startService(new Intent(getApplicationContext(),Background_Service.class));


    }




    private void setViewBasedOnProtectionStatus() {
        if (prefManager.isProtected()) {
            ConstraintLayout protection_on_container = (ConstraintLayout) findViewById(R.id.protection_on_container);
            ConstraintLayout protection_off_container = (ConstraintLayout) findViewById(R.id.protection_off_container);
            protection_on_container.setVisibility(View.VISIBLE);
            protection_off_container.setVisibility(View.GONE);

            ImageView protectionOnIV = (ImageView) findViewById(R.id.protection_onIV);
            protectionOnIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(MainActivity.this);
                    //setting custom layout to dialog
                    dialog.setContentView(R.layout.listview_context_cusotm_dialog_layout);
//                    dialog.setTitle("Enter PIN");

                    //adding button click event
                    Button okButton = (Button) dialog.findViewById(R.id.ok_btn);
                    Button cancelButton = (Button) dialog.findViewById(R.id.cancel_btn);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            oldPin = sqlController.fetchExistingLockPassword().trim();

                            String fieldPin = "";
                            TextInputEditText enterPinET = (TextInputEditText) dialog.findViewById(R.id.context_enter_password);
                            try {
                                fieldPin = enterPinET.getText().toString().trim();

                                if (oldPin.equalsIgnoreCase(fieldPin)) {
                                    prefManager.setIsProtected(false); // PROTECTED THE DEVICES
                                    setViewBasedOnProtectionStatus(); // we initialize the view again
                                    Toast.makeText(MainActivity.this, "Protection turned off", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                } else {
                                    Toast.makeText(MainActivity.this, "PIN incorrect!. Please try again", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        } else {

            ConstraintLayout protection_on_container = (ConstraintLayout) findViewById(R.id.protection_on_container);
            ConstraintLayout protection_off_container = (ConstraintLayout) findViewById(R.id.protection_off_container);
            protection_on_container.setVisibility(View.GONE);
            protection_off_container.setVisibility(View.VISIBLE);

            ImageView protectionOffIV = (ImageView) findViewById(R.id.protection_offIV);
            protectionOffIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefManager.setIsProtected(true); // PROTECTED THE DEVICES

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


                    long update = sqlController.updateTable(DBhelper.SIM_DETAILS_TABLE, contentValues2, DBhelper.SIM_TABLE_ID, "1");
                    if ((update + "").equalsIgnoreCase("1")) {
                        prefManager.setIsFreshRequest(true);
//                        Toast.makeText(MainActivity.this, "Serial number updated successfully! ", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(MainActivity.this, "Could not modify serial number", Toast.LENGTH_SHORT).show();
                    }

                    setViewBasedOnProtectionStatus(); // Re-initialize the view

                }
            });
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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_modify_pin) {

            Intent intent = new Intent(getApplicationContext(), LockActivity.class);
            intent.putExtra("intent_request_type","modify_pin");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        displayContactDetailsRecords(); // display the details
    }

    public void displayContactDetailsRecords() {
        fetchFewRecoveryDetailsToArrayList(); //  Fetch the details before assigning to the
        recoveryDetailsLV = (ListView) findViewById(R.id.homeRecoveryDetailsLV);
        MyRecoveryDetailsAdapter adapter = new MyRecoveryDetailsAdapter(getApplicationContext(), recoveryName, recoveryPhone, recoveryEmail);
        recoveryDetailsLV.setAdapter(adapter);
        registerForContextMenu(recoveryDetailsLV);

        // TODO: NOTIFY LISTVIEW OF DATA SET CHANGE AFTER DELETING USING DIALOG

        recoveryDetailsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemId = position + ""; // Sending the id of the selected item to the class variable so that I can use it everywhere
                return false;
            }
        });
    }


    public void fetchFewRecoveryDetailsToArrayList() {
        recoveryRowId = new ArrayList<String>();
        recoveryName = new ArrayList<String>();
        recoveryPhone = new ArrayList<String>();
        recoveryEmail = new ArrayList<String>();


        DBhelper = new DBhelper(MainActivity.this);
        db = DBhelper.getWritableDatabase();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_TABLE + " ORDER BY " + com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_ID + "  DESC LIMIT 0,3", null);
            while (c.moveToNext()) {
                recoveryRowId.add(c.getString(c.getColumnIndex(com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_ID)));
                recoveryName.add(c.getString(c.getColumnIndex(com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_NAME)));
                recoveryPhone.add(c.getString(c.getColumnIndex(com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_PHONE)));
                recoveryEmail.add(c.getString(c.getColumnIndex(com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_EMAIL)));
            }
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_details_context_menu, menu);
        menu.setHeaderTitle("Select an action");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_record) {
            String idSelected = recoveryRowId.get(Integer.parseInt(selectedItemId)) + "";

            Intent intent = new Intent(getApplicationContext(), AddRecoveryDetails.class);
            intent.putExtra("intent_request_type", "modify_contact");
            intent.putExtra("intent_modify_id", idSelected);
            startActivity(intent);

        } else if (item.getItemId() == R.id.delete_record) {

            final Dialog dialog = new Dialog(MainActivity.this);
            //setting custom layout to dialog
            dialog.setContentView(R.layout.listview_context_cusotm_dialog_layout);
//            dialog.setTitle("Enter PIN");

            //adding button click event
            Button okButton = (Button) dialog.findViewById(R.id.ok_btn);
            Button cancelButton = (Button) dialog.findViewById(R.id.cancel_btn);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oldPin = sqlController.fetchExistingLockPassword().trim();

                    String idSelected = recoveryRowId.get(Integer.parseInt(selectedItemId)) + "";
                    String fieldPin = "";
                    TextInputEditText enterPinET = (TextInputEditText) dialog.findViewById(R.id.context_enter_password);
                    try {
                        fieldPin = enterPinET.getText().toString().trim();

                        if (oldPin.equalsIgnoreCase(fieldPin)) {
                            sqlController.deleteItemFromTable(com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_TABLE, com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_ID, idSelected);
                            Toast.makeText(MainActivity.this, "Record deleted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
//                            recoveryDetailsLV.deferNotifyDataSetChanged();
                        } else {
                            enterPinET.setError("PIN incorrect! Please try again");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            return false;
        }
        return true;
    }


    class MyRecoveryDetailsAdapter extends ArrayAdapter {

        List<String> name;
        List<String> phoneNumber;
        List<String> email;

        SomeComponents myComponent;

        private MyRecoveryDetailsAdapter(Context context, List<String> rowName, List<String> rowPhone, List<String> rowEmail) {
            //Overriding Default Constructor off ArratAdapter
            super(context, R.layout.home_recovery_details_single_row, rowName);

            this.name = rowName;
            this.phoneNumber = rowPhone;
            this.email = rowEmail;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Inflating the layout
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.home_recovery_details_single_row, parent, false);

            myComponent = new SomeComponents();

            //Get the reference to the view objects
            TextView recoveryName = (TextView) row.findViewById(R.id.recoveryDetailsName);
            TextView recoveryPhone = (TextView) row.findViewById(R.id.recoveryDetailsPhone);
            TextView recoveryEmail = (TextView) row.findViewById(R.id.recoveryDetailsEmail);


            //Providing the element of an array by specifying its position
            recoveryName.setText(name.get(position));
            recoveryPhone.setText(phoneNumber.get(position));
            recoveryEmail.setText(email.get(position));

            return row;
        }
    }


}



