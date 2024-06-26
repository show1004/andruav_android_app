package ap.andruav_ap.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import ap.andruav_ap.communication.telemetry.AndruavSMSClientParser;
import ap.andruavmiddlelibrary.preference.Preference;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSReceiver";
    private static final AndruavSMSClientParser andruavSMSClientParser = new AndruavSMSClientParser();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Preference.isSMSRXEnabled(null))
        {
            return ; // ModuleFeatures is disabled by user.
        }

        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Object[] pdus = (Object[]) extras.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = extras.getString("format");
                            smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
                        } else {
                            smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        }
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();

                        Log.d(TAG, "Received SMS from: " + sender);
                        Log.d(TAG, "Message: " + messageBody);

                        andruavSMSClientParser.executeCommand (sender, messageBody);
                        // Process or display the received SMS as per your app's requirements
                    }
                }
            }
        }
    }
}