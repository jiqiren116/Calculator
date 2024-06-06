package com.example.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.example.calculator.MY_BROADCAST")) {
            String expression = intent.getStringExtra("expression");
            Toast.makeText(context, "在MyBroadcastReceiver中接收到了广播，你点击了表达式：" + expression, Toast.LENGTH_SHORT).show();
        }
    }
}