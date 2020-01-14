package com.example.gongduck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;

public class SMSReceiver extends BroadcastReceiver {
    final static String SMSReceiverTAG = "SMSReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(SMSReceiverTAG, "## sms 리시버 실행 되었음");

        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())){
            Log.d(SMSReceiverTAG, "## SMS 받아졌음");

            Bundle bundle = intent.getExtras();
            Object[] messages = (Object[]) bundle.get("pdus");
            SmsMessage[] smsMessages = new SmsMessage[messages.length];

            for(int i = 0; i < smsMessages.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = bundle.getString("format");
                    smsMessages[i] = SmsMessage.createFromPdu((byte[]) messages[i], format);
                } else {
                    smsMessages[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
                }
            }

            String contents = smsMessages[0].getMessageBody().toString();

            //Toast.makeText(context, contents, Toast.LENGTH_LONG).show();

            Log.d(SMSReceiverTAG, "## SMS Message:" + contents);

            // 특정 재난 단어가 포함될 경우만 전달
            if (contents.contains("한파") || contents.contains("폭염") || contents.contains("지진") || contents.contains("화재")) {
                sendToActivity(context, contents);
            }

        }
    }

    private void sendToActivity(Context context, String contents) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("contents", contents);
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
