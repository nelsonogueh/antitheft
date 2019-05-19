package com.dulceprime.antitheft;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dulceprime.antitheft.other_components.DBhelper;
import com.dulceprime.antitheft.other_components.SQLController;

public class AddRecoveryDetails extends AppCompatActivity {

    TextInputEditText add_recovery_details_name, add_recovery_details_phone, add_recovery_details_email;
    Button add_recovery_details_submit;

    String intent_request_type = "add_contact", intent_modify_id = "";

    ConstraintLayout addRecoveryDetailsContainerCL;

    public SQLiteDatabase db;
    SQLController sqlController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recovery_details);

        db = openOrCreateDatabase(DBhelper.DB_NAME, MODE_PRIVATE, null);
        sqlController = new SQLController(getApplicationContext());
        addRecoveryDetailsContainerCL = (ConstraintLayout) findViewById(R.id.addRecoveryDetailsContainerCL);

        intent_request_type = getIntent().getStringExtra("intent_request_type");


        switch (intent_request_type) {
            case "add_contact": {
                addContact(); // Call the add contact method
                break;
            }
            case "modify_contact": {
                intent_modify_id = getIntent().getStringExtra("intent_modify_id");
                modifyContact(); // Call the add contact method
                break;
            }
        }
    }


    public void addContact() {
        add_recovery_details_name = (TextInputEditText) findViewById(R.id.add_recovery_details_name);
        add_recovery_details_phone = (TextInputEditText) findViewById(R.id.add_recovery_details_phone);
        add_recovery_details_email = (TextInputEditText) findViewById(R.id.add_recovery_details_email);
        add_recovery_details_submit = (Button) findViewById(R.id.add_recovery_details_submit);

        String name = "";
        String phone = "";
        String email = "";

        add_recovery_details_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = add_recovery_details_name.getText().toString().trim();
                String phone = add_recovery_details_phone.getText().toString().trim();
                String email = add_recovery_details_email.getText().toString().trim();

                ContentValues contentValues = new ContentValues();
                contentValues.put(DBhelper.RECOVERY_DETAILS_NAME, name);
                contentValues.put(DBhelper.RECOVERY_DETAILS_PHONE, phone);
                contentValues.put(DBhelper.RECOVERY_DETAILS_EMAIL, email);

                if (!(name.equalsIgnoreCase(""))) {
                    if (!(phone.equalsIgnoreCase(""))) {
                        if (!(email.equalsIgnoreCase(""))) {
                            sqlController.insertNewRecord(DBhelper.RECOVERY_DETAILS_TABLE, contentValues);

                            Toast.makeText(AddRecoveryDetails.this, "Contact details added successfully!", Toast.LENGTH_SHORT).show();
                            add_recovery_details_name.setText("");
                            add_recovery_details_phone.setText("");
                            add_recovery_details_email.setText("");
                        } else {
                            add_recovery_details_email.setError("Email cannot be empty!");
                        }
                    } else {
                        add_recovery_details_phone.setError("Phone number cannot be empty!");
                    }
                } else {
                    add_recovery_details_name.setError("Name cannot be empty!");
                }
            }
        });
    }


    public void modifyContact() {

        add_recovery_details_name = (TextInputEditText) findViewById(R.id.add_recovery_details_name);
        add_recovery_details_phone = (TextInputEditText) findViewById(R.id.add_recovery_details_phone);
        add_recovery_details_email = (TextInputEditText) findViewById(R.id.add_recovery_details_email);
        add_recovery_details_submit = (Button) findViewById(R.id.add_recovery_details_submit);


        DBhelper dBhelper = new DBhelper(this);
        db = dBhelper.getWritableDatabase();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + DBhelper.RECOVERY_DETAILS_TABLE + " WHERE " + DBhelper.RECOVERY_DETAILS_ID + " = '" + intent_modify_id + "'", null);

            if (c.moveToNext()) {
                // Populating the List

                add_recovery_details_name.setText(c.getString(c.getColumnIndex(DBhelper.RECOVERY_DETAILS_NAME)));
                add_recovery_details_phone.setText(c.getString(c.getColumnIndex(DBhelper.RECOVERY_DETAILS_PHONE)));
                add_recovery_details_email.setText(c.getString(c.getColumnIndex(DBhelper.RECOVERY_DETAILS_EMAIL)));
            }

            c.close();

            db.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "User details not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        String name = "";
        String phone = "";
        String email = "";

        add_recovery_details_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = add_recovery_details_name.getText().toString().trim();
                String phone = add_recovery_details_phone.getText().toString().trim();
                String email = add_recovery_details_email.getText().toString().trim();

                ContentValues contentValues = new ContentValues();
                contentValues.put(DBhelper.RECOVERY_DETAILS_NAME, name);
                contentValues.put(DBhelper.RECOVERY_DETAILS_PHONE, phone);
                contentValues.put(DBhelper.RECOVERY_DETAILS_EMAIL, email);

                if (!(name.equalsIgnoreCase(""))) {
                    if (!(phone.equalsIgnoreCase(""))) {
                        if (!(email.equalsIgnoreCase(""))) {

                            long update = sqlController.updateTable(DBhelper.RECOVERY_DETAILS_TABLE, contentValues, DBhelper.RECOVERY_DETAILS_ID, intent_modify_id);
                            if ((update + "").equalsIgnoreCase("1")) {
                                Toast.makeText(AddRecoveryDetails.this, "Details modified successfully! ", Toast.LENGTH_SHORT).show();

                                add_recovery_details_name.setText("");
                                add_recovery_details_phone.setText("");
                                add_recovery_details_email.setText("");
                                finish();
                            } else {
                                Toast.makeText(AddRecoveryDetails.this, "Could not modify details", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            add_recovery_details_email.setError("Email cannot be empty!");
                        }
                    } else {
                        add_recovery_details_phone.setError("Phone number cannot be empty!");
                    }
                } else {
                    add_recovery_details_name.setError("Name cannot be empty!");
                }
            }
        });
    }
}
