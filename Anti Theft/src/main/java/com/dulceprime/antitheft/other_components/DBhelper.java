package com.dulceprime.antitheft.other_components;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper {

    // Database Information
    public static final String DB_NAME = "MySQLITEDB.DB";

    // database version
    private static final int DB_VERSION = 1;

    /**
     * PASSWORD TABLE
     **/
    // SCHEDULED BIRTHDAY TABLE NAME
    public static final String PASSWORD_TABLE = "password";
    // Declaring the field for PASSWORD TABLE COLUMNS
    public static final String PASSWORD_ID = "id", PASSWORD = "password", SECURITY_QUESTION = "security_question", SECURITY_ANSWER = "security_answer";
    // Creating password table query
    private static final String CREATE_PASSWORD_TABLE = "CREATE TABLE IF NOT EXISTS " + PASSWORD_TABLE + "(" + PASSWORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PASSWORD + " VARCHAR, " + SECURITY_QUESTION + " TEXT, " + SECURITY_ANSWER + " TEXT)";


    /**
     * RECOVERY DETAILS TABLE
     **/
    // RECOVERY DETAILS TABLE NAME
    public static final String RECOVERY_DETAILS_TABLE = "recovery_details";
    // Declaring the field for RECOVERY DETAILS TABLE COLUMNS
    public static final String RECOVERY_DETAILS_ID = "recovery_details_id", RECOVERY_DETAILS_NAME = "recovery_details_name", RECOVERY_DETAILS_PHONE = "recovery_details_phone", RECOVERY_DETAILS_EMAIL = "recovery_details_email";
    // Creating scheduled birthday table query
    private static final String CREATE_RECOVERY_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + RECOVERY_DETAILS_TABLE + "(" + RECOVERY_DETAILS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RECOVERY_DETAILS_NAME + " VARCHAR, " + RECOVERY_DETAILS_PHONE + " VARCHAR, " + RECOVERY_DETAILS_EMAIL + " VARCHAR)";


    /**
     * SIM DETAILS TABLE
     **/
    // RECOVERY DETAILS TABLE NAME
    public static final String SIM_DETAILS_TABLE = "sim_details";
    // Declaring the field for SIM DETAILS TABLE COLUMNS
    public static final String SIM_TABLE_ID = "sim_table_id", SERIAL_NUMBER_1 = "serial_number_1", PHONE_NUMBER_1 = "phone_number_1", SERIAL_NUMBER_2 = "serial_number_2", PHONE_NUMBER_2 = "phone_number_2", SIM_UPDATED_DATE = "sim_updated_date";
    // Creating scheduled birthday table query
    private static final String CREATE_SIM_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + SIM_DETAILS_TABLE + " (" + SIM_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SERIAL_NUMBER_1 + " VARCHAR, " + PHONE_NUMBER_1 + " VARCHAR, " + SERIAL_NUMBER_2 + " VARCHAR, " + PHONE_NUMBER_2 + " VARCHAR, " + SIM_UPDATED_DATE + " VARCHAR)";


    /**
     * SIM DETAILS TABLE
     **/
    // SAVED REPORT TABLE NAME
    public static final String SAVED_REPORT_TABLE = "saved_reports";
    // Declaring the field for SAVED DETAILS TABLE COLUMNS
    public static final String SAVED_REPORT_ID = "report_id", GPS_COORDINATE_X = "gps_coordinate_x", GPS_COORDINATE_Y = "gps_coordinate_y", SENDING_STATUS = "sending_status", SAVED_DATE = "saved_date";
    // Creating scheduled birthday table query
    private static final String CREATE_SAVED_REPORT_TABLE = "CREATE TABLE IF NOT EXISTS " + SAVED_REPORT_TABLE + "(" + SAVED_REPORT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + GPS_COORDINATE_X + " VARCHAR, " + GPS_COORDINATE_Y + " VARCHAR, " + SENDING_STATUS + " VARCHAR, " + SAVED_DATE + " VARCHAR)";


    public DBhelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static final String SENDING_STATUS_SENT = "sent";
    public static final String SENDING_STATUS_UNSENT = "unsent";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating the database tables
        db.execSQL(CREATE_PASSWORD_TABLE);
        db.execSQL(CREATE_RECOVERY_DETAILS_TABLE);
        db.execSQL(CREATE_SIM_DETAILS_TABLE);
        db.execSQL(CREATE_SAVED_REPORT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS  " + PASSWORD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS  " + RECOVERY_DETAILS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS  " + SIM_DETAILS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS  " + SAVED_REPORT_TABLE);
        onCreate(db);
    }
}



