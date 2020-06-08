package cc.darak.firehd.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;

public class BungTak extends Activity implements View.OnClickListener {

    //VideoView videoView;
    private int stopPosition;
    private VideoView vidv;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(BungTak.this, getResources().getString(R.string.ur_in_g), Toast.LENGTH_LONG).show();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_bt);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        vidv = findViewById(R.id.video_view);
        vidv.setOnClickListener(this);

        ((EEService) EEService.mContext).pause();
        VideoView videoView = findViewById(R.id.video_view);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.ugt;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        //Set looping
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        videoView.start();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_view:
                Toast.makeText(this, getResources().getString(R.string.g_toast), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoView videoView = findViewById(R.id.video_view);
        ((EEService) EEService.mContext).pause();
        videoView.seekTo(stopPosition);
        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoView videoView = findViewById(R.id.video_view);
        stopPosition = videoView.getCurrentPosition() + 1;
        videoView.pause();
        ((EEService) EEService.mContext).play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoView videoView = findViewById(R.id.video_view);
        ((EEService) EEService.mContext).play();
        videoView.stopPlayback();
    }
}
