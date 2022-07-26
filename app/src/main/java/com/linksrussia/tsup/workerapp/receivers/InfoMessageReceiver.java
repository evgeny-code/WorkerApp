package com.linksrussia.tsup.workerapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class InfoMessageReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION = "InfoReceiver";
    public static final String TEXT_EXTRA = "TEXT_EXTRA";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getStringExtra(TEXT_EXTRA), Toast.LENGTH_LONG).show();
    }
}
