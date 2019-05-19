package com.dulceprime.antitheft.other_components;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by NELSON on 11/22/2017.
 */

public class SomeComponents extends AppCompatActivity {

    public Activity activity;
    public SQLiteDatabase db;

    // [Add Event Activity] These variable holds all the final values to be sent to the database
//    public String finalPhoneNumber;
    public static String finalPhoneNumber = ""; //
    public static String phoneInputType = "none"; // none, selected or typed *how the contact was entered
    public static String finalBirthMonth = ""; //
    public static String finalBirthDay = ""; //
    public static String finalMessageBody = ""; //
    public static String finalSendingTimeHour = ""; //
    public static String finalSendingTimeMinute = ""; //


    public String nelsonTime() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        String stringTime = simpleDateFormat.format(now);
        return stringTime;
    }


    public String nowDateDay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        Date now = new Date();
        String stringDate = simpleDateFormat.format(now);
        return stringDate;
    }

    public String nowDateMonth() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        Date now = new Date();
        return simpleDateFormat.format(now);
    }

    public String nowDateYear() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        Date now = new Date();
        return simpleDateFormat.format(now);
    }

    public String dateFullType() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        return simpleDateFormat.format(now);
    }


    public String removeSpaceAndHyphen(String string) {
        String result = string.replaceAll(" ", "");
        return result.replaceAll("-", "");
    }

    // this method gets the name of the contact when the phone number is passed to it
    public String getContactName(Context context, final String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0) + " (" + phoneNumber + ")";
            } else {
                contactName = phoneNumber;
            }
            cursor.close();
        } else {
            contactName = phoneNumber;
        }
        return contactName;
    }


    public String returnSMSCharacterCount(String message) {
        // Count number of message characters and output the number of SMS it is resulting to.
        int totalCharacterLength = message.length();
        double numberOfSMSResultedTo = Math.round(totalCharacterLength / 160);
        int intResult = ((int) numberOfSMSResultedTo + 1);
        return "(" + totalCharacterLength + "/" + intResult + ")";
    }

    public int randomNumber(int minNumber, int maxNumber) {
        Random random = new Random(); // Class for generating random numbers
        //get the next random number within range
        int randomNumber = random.nextInt((maxNumber - minNumber) + minNumber) + minNumber;
        return randomNumber;

    }


    public void sendTextMessageWithAirtime(final String phoneNumber, final String smsBody) {
        // SEND SMS WITH AIRTIME
        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SMS_DELIVERED), 0);

// When the SMS has been sent
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:

                        Log.i("", "---------------------------------------------------------------------------------------------------------------------------------------");
                        Log.i("TEXT MESSAGE SENT", "TEXT MESSAGE HAS BEEN SENT TO:_ " + phoneNumber);
//                        updateSentMessagesTable(phoneNumber, smsBody, nowDateDay(), nowDateMonth(), nowDateYear());


                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // no airtime
//                        Log.i("", "\n");
                        Log.i("", "---------------------------------------------------------------------------------------------------------------------------------------");
                        Log.i("NO AIRTIME", "SORRY YOU DON'T HAVE AIRTIME TO SEND MESSAGE");
//                        updateSentMessagesTable(phoneNumber, smsBody, nowDateDay(), nowDateMonth(), nowDateYear());
//                        Toast.makeText(context, "It seems you don't have airtime", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // no service
//                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // network turned off
//                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

// Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
// Send a text based SMS
        smsManager.sendTextMessage(phoneNumber, null, smsBody, sentPendingIntent, deliveredPendingIntent);
    }


    public void sendTextMessageWithBulkSMSAPI(final String phoneNumber, final String smsBody) {
        // SEND SMS WITH BULK SMS API


    }

    public static String dateMonthName(String dateNumber) {
        // Returns month name depending on what number sent to it
        // E.g. 03 => March
        String dateName = null;
        switch (dateNumber) {
            case "01":
                dateName = "January";
                break;
            case "02":
                dateName = "February";
                break;
            case "03":
                dateName = "March";
                break;
            case "04":
                dateName = "April";
                break;
            case "05":
                dateName = "May";
                break;
            case "06":
                dateName = "June";
                break;
            case "07":
                dateName = "July";
                break;
            case "08":
                dateName = "August";
                break;
            case "09":
                dateName = "September";
                break;
            case "10":
                dateName = "October";
                break;
            case "11":
                dateName = "November";
                break;
            case "12":
                dateName = "December";
                break;


            default:
                dateName = dateNumber;
                break;
        }
        return dateName;
    }

    public static String dateDaySuffix(String dayPassed) {
        // Returns the day's suffix
        // E.g. 03 => 3rd, 01 => 1st
        String dateName = null;
        switch (dayPassed) {
            case "01":
                dateName = "1st";
                break;
            case "02":
                dateName = "2nd";
                break;
            case "03":
                dateName = "3rd";
                break;
            case "21":
                dateName = "21st";
                break;
            case "22":
                dateName = "22nd";
                break;
            case "23":
                dateName = "23rd";
                break;
            case "31":
                dateName = "31st";
                break;


            default:
                dateName = dayPassed + "th";
                break;
        }
        return dateName;
    }


    public void showSnackbarMessage(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)  // action text on the right side
                .show();
    }

}

