package cc.darak.firehd.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.util.Log;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Get EasterEgg State
        SharedPreferences preferences = context.getSharedPreferences("SavedPref", Context.MODE_PRIVATE);
        String eeState = preferences.getString("EEActive", "false");

        if(Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {

            chkUpdWorker.createWork();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, NotiService.class));
            } else {
                context.startService(new Intent(context, NotiService.class));
            }

            if (eeState.equals("true")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, EEService.class));
                } else {
                    context.startService(new Intent(context, EEService.class));
                }
            }

        }

        //ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
        //    @Override
        //    public void onAvailable(Network network) {
        //        Log.e("hihihihihihi","NETWOOOOOOOOOOOOOOORK");
        //        //doOnNetworkConnected();
        //    }
        //});
    }
}