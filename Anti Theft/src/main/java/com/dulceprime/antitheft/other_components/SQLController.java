package com.dulceprime.antitheft.other_components;

/**
 * Created by Nelson on 3/25/2019.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class SQLController {

    private DBhelper DBhelper;
    private Context ourcontext;
    public SQLiteDatabase db;

    public SQLController(Context c) {
        ourcontext = c;
    }

    public SQLController open() throws SQLException {
        DBhelper = new DBhelper(ourcontext);
        db = DBhelper.getWritableDatabase();
        return this;

    }

    public void close() {
        DBhelper.close();
    }

    public int updateTable(String tableName, ContentValues fieldsAndValues, String primaryKeyColumnName, String rowValueID) {

        DBhelper = new DBhelper(ourcontext);
        db = DBhelper.getWritableDatabase();

        int i = db.update(tableName, fieldsAndValues,
                primaryKeyColumnName + " = " + rowValueID, null);
        return i;
    }


    public void deleteItemFromTable(String tableName, String primaryKeyColumnName, String rowValueID) {
        DBhelper = new DBhelper(ourcontext);
        db = DBhelper.getWritableDatabase();

        db.delete(tableName, primaryKeyColumnName + "=" + rowValueID, null);
        // Example below
//        db.delete(DBhelper.SCHEDULED_BIRTHDAY_TABLE, DBhelper.SCHEDULED_ID + "=" + schedule_id, null);
    }


    // INSERT NEW RECORD TABLE
    public void insertNewRecord(String tableName, ContentValues columnAndValues) {
        DBhelper = new DBhelper(ourcontext);
        db = DBhelper.getWritableDatabase();

//        ContentValues contentValue = new ContentValues();
//        contentValue.put(DBhelper.SCHEDULED_RECIPENT, phoneNumber);

        db.insert(tableName, null, columnAndValues);
    }

    public String fetchExistingLockPassword() {
        String password = "";
        DBhelper = new DBhelper(ourcontext);
        db = DBhelper.getWritableDatabase();
        try {
            Cursor c = db.rawQuery("SELECT * FROM "+ com.dulceprime.antitheft.other_components.DBhelper.PASSWORD_TABLE, null);
            if (c.moveToFirst()) {
                password = c.getString(c.getColumnIndex(com.dulceprime.antitheft.other_components.DBhelper.PASSWORD));
            }
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();


        return password;
    }


}