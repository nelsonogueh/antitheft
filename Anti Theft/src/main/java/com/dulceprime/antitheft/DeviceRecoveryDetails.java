package com.dulceprime.antitheft;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dulceprime.antitheft.other_components.DBhelper;
import com.dulceprime.antitheft.other_components.PrefManager;
import com.dulceprime.antitheft.other_components.SQLController;
import com.dulceprime.antitheft.other_components.SomeComponents;

import java.util.ArrayList;
import java.util.List;

public class DeviceRecoveryDetails extends AppCompatActivity {


    List recoveryRowId;
    List recoveryName;
    List recoveryPhone;
    List recoveryEmail;

    private DBhelper DBhelper;
    private Context ourcontext;
    public SQLiteDatabase db;

    SQLController sqlController;
    String oldPin;

    public static String selectedItemId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_recovery_details);

        db = openOrCreateDatabase(DBhelper.DB_NAME, MODE_PRIVATE, null);
        sqlController = new SQLController(getApplicationContext());


        displayContactDetailsRecords(); // display all the records
    }

    @Override
    protected void onStart() {
        super.onStart();

        displayContactDetailsRecords(); // display the details
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayContactDetailsRecords(); // display the details
    }

    public void displayContactDetailsRecords(){
        fetchAllRecoveryDetailsToArrayList(); //  Fetch the details before assigning to the
        ListView recoveryDetailsLV = (ListView) findViewById(R.id.homeRecoveryDetailsLV);
        DeviceRecoveryDetails.MyRecoveryDetailsAdapter adapter = new DeviceRecoveryDetails.MyRecoveryDetailsAdapter(getApplicationContext(), recoveryName, recoveryPhone, recoveryEmail);

        recoveryDetailsLV.setAdapter(adapter);

        // TODO: NOTIFY LISTVIEW OF DATA SET CHANGE AFTER DELETING USING DIALOG
        registerForContextMenu(recoveryDetailsLV);
        recoveryDetailsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemId = position + ""; // Sending the id of the selected item to the class variable so that I can use it everywhere
                return false;
            }
        });
    }


    public void fetchAllRecoveryDetailsToArrayList() {
        recoveryRowId = new ArrayList<String>();
        recoveryName = new ArrayList<String>();
        recoveryPhone = new ArrayList<String>();
        recoveryEmail = new ArrayList<String>();


        DBhelper = new DBhelper(DeviceRecoveryDetails.this);
        db = DBhelper.getWritableDatabase();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_TABLE + " ORDER BY " + com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_ID + " DESC", null);
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

            final Dialog dialog = new Dialog(DeviceRecoveryDetails.this);
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
                            sqlController.deleteItemFromTable(com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_TABLE, com.dulceprime.antitheft.other_components.DBhelper.RECOVERY_DETAILS_ID, idSelected); //   updateTable(DBhelper.PASSWORD_TABLE, contentValues, DBhelper.PASSWORD_ID, "1");
                            Toast.makeText(DeviceRecoveryDetails.this, "Record deleted", Toast.LENGTH_SHORT).show();
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
