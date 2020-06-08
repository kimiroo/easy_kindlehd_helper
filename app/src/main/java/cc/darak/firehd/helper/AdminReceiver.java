package cc.darak.firehd.helper;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

public class AdminReceiver extends DeviceAdminReceiver {

    static final String TAG = "AdminReceiver";
    DevicePolicyManager deviceManger;
    ComponentName compName;
    long current_time;
    Timer myThread;

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.e(TAG,"ACTIVATED");
        //Toast.makeText(context, getResources().getString(R.string.receiver_enabled), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log.e(TAG,"DISABLE IN PROGRESS");
        //Toast.makeText(context, getResources().getString(R.string.receiver_disabled), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLockTaskModeEntering(Context context, Intent intent, String pkg) {
        Log.i(TAG, "onLockTaskModeEntering: " + pkg);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {

        compName = new ComponentName(context, AdminReceiver.class);
        deviceManger = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        Log.e(TAG,"DISABLE REQUESTED");

        if(deviceManger.isDeviceOwnerApp("cc.darak.firehd.helper")) {
            deviceManger.reboot(compName);
        } else {
            Log.e(TAG,"This application not whitelisted. Trying alternative way.");

            Intent i = new Intent(android.provider.Settings.ACTION_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            //Intent i2 = new Intent(getBaseContext(), Updater.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(i2);

            myThread = new Timer();
            current_time = System.currentTimeMillis();
            myThread.schedule(lock_task,0,500);
        }

        return "Disabling it will limit functions of the app.";
    }

    // Repeatedly lock the phone every second for 5 seconds
    TimerTask lock_task = new TimerTask() {
        @Override
        public void run() {
            long diff = System.currentTimeMillis() - current_time;
            if (diff<5000) {
                Log.d("Timer","1 second");
                deviceManger.lockNow();
            }
            else{
                myThread.cancel();
            }
        }
    };
}