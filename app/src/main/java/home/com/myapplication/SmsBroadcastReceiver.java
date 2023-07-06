package home.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";



    private Listener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    smsBody += smsMessage.getMessageBody();
                }
            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");
                    if (pdus == null) {
                        // Display some error to the user
                        Log.e(TAG, "SmsBundle had no pdus key");
                        return;
                    }
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        smsBody += messages[i].getMessageBody();
                    }
                    smsSender = messages[0].getOriginatingAddress();
                }
            }
            String phone = new Pref(context).PrefGetString(Pref.SIMNO);
            if (smsSender.contains(phone.substring(2)) ) {
                show_sms_dialog(smsSender,smsBody,context);
                /*if (listener != null) {
                    listener.onTextReceived(smsBody);
                }*/
            }
        }
    }


    private void show_sms_dialog(String msg_from,String msgBody,Context context){

        Intent myint;
        if(msgBody.contains("Lat") &&  msgBody.contains("Long"))
        {
            myint=new Intent(context,Map_Activity.class);
        }
        else
            myint=new Intent(context,Dialog_activity.class);
        myint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myint.putExtra("from", msg_from);
        myint.putExtra("body", msgBody);
        context.startActivity(myint);
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onTextReceived(String text);
    }
}
