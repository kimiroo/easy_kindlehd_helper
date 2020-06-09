package cc.darak.firehd.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import org.json.JSONObject;
import java.net.URL;
import java.util.Scanner;

public class UpdaterService extends Service {

    private NotificationManager mManager;
    private static final String TAG = "UpdaterService";
    public String lv;
    public String chkRes;
    public String upd;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.e(TAG, "Service Started!");

        createChannels();
        Notification.Builder nb = getAndroidChannelNotification(getString(R.string.upd_title), getString(R.string.upd_stat_checking),true,false);
        //getManager().notify(1000, nb.build());
        startForeground(1000,nb.build());
                //startNotification(1000,getString(R.string.upd_title),getString(R.string.upd_stat_checking),true,false,true);

        //while loop
        int i=1;
        while(i<=5){
            Log.e(TAG, "Loop " + i);
            i++;

            try {
                chkUpdate();
            } catch (InterruptedException e) {
                e.printStackTrace();
                chkRes = "fail";

                Notification.Builder nb2 = getAndroidChannelNotification(getString(R.string.upd_title), getString(R.string.upd_s_re),true,false);
                getManager().notify(1000, nb2.build());
                //startNotification(1000,getString(R.string.upd_title),getString(R.string.upd_s_re),true,false,true);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }

            if (chkRes.equals("success")) {
                Log.e(TAG,"DEBUG: " + upd);
                if (!upd.equals("true")) {
                    stopService();
                }
                //Keeping the service
                break;
            }
        }

        if (chkRes.equals("fail")) {
            Log.e(TAG, "3");
            Notification.Builder nb3 = getAndroidChannelNotification(getString(R.string.upd_title), getString(R.string.upd_s_fail),false,true);
            getManager().notify(1000, nb3.build());
            //startNotification(1000,getString(R.string.upd_title),getString(R.string.upd_s_fail),false,true,true);
            stopForeground(false);
            stopSelf();
        }

        if (upd.equals("true")) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1000);
        }
    }

    public void chkUpdate() throws InterruptedException {
        Log.e(TAG, "ChkUpdate!");
        lv = "null";
        chkRes = "null";

        final String[] res = {"success"};

        Thread callChkJson = new Thread() {
            public void run() {
                try {
                    chkJSON();
                } catch (Exception e) {
                    e.printStackTrace();
                    res[0] = "fail";
                }
            }
        };
        callChkJson.start();

        callChkJson.join();

        Log.e(TAG,"chkJSON DONE");

        if (res[0].equals("fail")) {
            Log.e(TAG,"chkJSON FAILED");
            chkRes = "fail";
            return;
        }

        if (lv.equals("null")) {
            chkRes = "fail";
            return;
        }

        int i_lv = Integer.valueOf(lv);
        int i_bN = Integer.valueOf(getResources().getString(R.string.buildNumber));

        if (!(i_bN >= i_lv)) {
            Log.e(TAG, "4");
            createAlertChannels();
            Notification.Builder nb4 = getAndroidAlertChannelNotification(getString(R.string.upd_alert_title), getString(R.string.upd_s_upd),true,true);
            //getManager().notify(1100, nb4.build());
            startForeground(1100,nb4.build());
            //startNotification(1000,getString(R.string.upd_title),getString(R.string.upd_s_upd),false,true,true);
            //stopForeground(false);
            upd = "true";
        } else {
            upd = "false";
        }

        chkRes = "success";
    }

    public void chkJSON() throws Exception {

        Log.e(TAG, "chkJSON");

        // build a URL
        String s = "https://khd.4dollar.tk/update/version.json";
        URL url = new URL(s);

        // read from the URL
        Scanner scan = new Scanner(url.openStream());
        String str = new String();
        while (scan.hasNext())
            str += scan.nextLine();
        scan.close();

        // build a JSON object
        JSONObject obj = new JSONObject(str);
        if (! obj.getString("app").equals("cc.darak.firehd.helper"))
            return;

        // get the first result
        lv = obj.optString("latestVersion");
        System.out.println(lv);
    }

    public Notification.Builder getAndroidChannelNotification(String title, String body, boolean ongoing, boolean autocancel) {

        // Launch App
        PendingIntent mPendingIntent = PendingIntent.getActivity(UpdaterService.this, 0,
                new Intent(getApplicationContext(), Updater.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //Create Notification
        return new Notification.Builder(getApplicationContext(), getString(R.string.upd_title))
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setOngoing(ongoing)
                .setAutoCancel(autocancel)
                .setContentIntent(mPendingIntent);
    }

    public Notification.Builder getAndroidAlertChannelNotification(String title, String body, boolean ongoing, boolean autocancel) {

        // Launch App
        PendingIntent mPendingIntent = PendingIntent.getActivity(UpdaterService.this, 0,
                new Intent(getApplicationContext(), Updater.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //Create Notification
        return new Notification.Builder(getApplicationContext(), getString(R.string.upd_alert_title))
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setOngoing(ongoing)
                .setAutoCancel(autocancel)
                .setContentIntent(mPendingIntent);
    }


    private void createChannels() {

        // create android channel
        NotificationChannel androidChannel = new NotificationChannel(getString(R.string.upd_title),
                getString(R.string.upd_title), NotificationManager.IMPORTANCE_LOW);

        androidChannel.enableLights(false);
        androidChannel.enableVibration(false);
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getManager().createNotificationChannel(androidChannel);
    }

    private void createAlertChannels() {

        // create android channel
        NotificationChannel androidAlertChannel = new NotificationChannel(getString(R.string.upd_alert_title),
                getString(R.string.upd_alert_title), NotificationManager.IMPORTANCE_HIGH);

        androidAlertChannel.enableLights(true);
        androidAlertChannel.enableVibration(true);
        androidAlertChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getManager().createNotificationChannel(androidAlertChannel);
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public final void stopServiceWithNotification() {
        stopForeground(false);
        stopSelf();
    }

    public final void stopService() {
        stopForeground(true);
        stopSelf();
    }
}
