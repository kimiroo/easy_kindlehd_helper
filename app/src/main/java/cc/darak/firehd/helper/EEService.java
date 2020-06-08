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
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.UserManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class EEService extends Service {

    MediaPlayer mPlayer;
    public static Context mContext;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        maxVolume();

        playMusic();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    public void playMusic() {

        // OLD CODE

        Notification notification = new Notification();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.noti_ee_channel));
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        builder.setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.noti_ee_title))
                .setContentText(getString(R.string.noti_ee_text))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));

        // Launch App
        PendingIntent mPendingIntent = PendingIntent.getActivity(EEService.this, 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Launch App on Notification Click
        builder.setContentIntent(mPendingIntent);

        // Show Notification
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert notificationManager != null;
            notificationManager.createNotificationChannel(new NotificationChannel(getString(R.string.noti_ee_channel), getString(R.string.noti_ee_channel), NotificationManager.IMPORTANCE_MIN));
        }

        // help: id value is unique int value of each notification channel
        assert notificationManager != null;
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(99, builder.build());
            //stopForeground(true);
        } else {
            notificationManager.notify(99, builder.build());
        }

        mPlayer = MediaPlayer.create(this, R.raw.onegai);
        mPlayer.setLooping(true);
        play();
    }

    public void play() {
        mPlayer.start();
    }
    public void pause() {
        if(mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }
    public void maxVolume() {
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);
    }
    public void startEgg() {
        SharedPreferences eePref = getSharedPreferences("SavedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = eePref.edit();
        editor.putString("EEActive", "true");
        editor.apply();
    }
    public void stopEgg() {
        SharedPreferences eePref = getSharedPreferences("SavedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = eePref.edit();
        editor.putString("EEActive", "false");
        editor.apply();
        stopForeground(true);
    }
    public void destroy() {
        mPlayer.release();
        stopSelf();
    }
}
