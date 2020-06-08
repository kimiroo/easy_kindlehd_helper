package cc.darak.firehd.helper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.UserManager;

import androidx.core.app.NotificationCompat;

public class NotiService extends Service {

    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (isDeviceOwnerApp()) {
            defaultRestriction();
        }

        createNotification();
    }

    @SuppressLint("WrongConstant")
    public void createNotification() {

        Notification notification = new Notification();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.noti_boot_channel));
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        builder.setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.noti_title))
                .setContentText(getString(R.string.noti_short))
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.noti_long)))
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));

        // Launch App
        PendingIntent mPendingIntent = PendingIntent.getActivity(NotiService.this, 0,
                new Intent(getApplicationContext(), WarningPage.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Launch App on Notification Click
        builder.setContentIntent(mPendingIntent);

        // Show Notification
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert notificationManager != null;
            notificationManager.createNotificationChannel(new NotificationChannel(getString(R.string.noti_boot_channel), getString(R.string.noti_boot_channel), NotificationManager.IMPORTANCE_HIGH));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        assert notificationManager != null;
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(100, builder.build());
            //stopForeground(false);
        } else {
            notificationManager.notify(100, builder.build());
        }
    }

    private void defaultRestriction() {
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, true);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, true);
    }

    private void setUserRestriction(String restriction, boolean disallow) {

        mAdminComponentName = new ComponentName(this, AdminReceiver.class);
        mDevicePolicyManager = (DevicePolicyManager)this.getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (disallow) {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName,restriction);
        } else {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName,restriction);
        }
    }

    private boolean isDeviceAdminApp() {
        mAdminComponentName = new ComponentName(this, AdminReceiver.class);
        mDevicePolicyManager = (DevicePolicyManager)this.getSystemService(Context.DEVICE_POLICY_SERVICE);

        return mDevicePolicyManager.isAdminActive(mAdminComponentName);
    }

    private boolean isDeviceOwnerApp() {
        mAdminComponentName = new ComponentName(this, AdminReceiver.class);
        mDevicePolicyManager = (DevicePolicyManager)this.getSystemService(Context.DEVICE_POLICY_SERVICE);

        return mDevicePolicyManager.isDeviceOwnerApp(getPackageName());
    }
}
