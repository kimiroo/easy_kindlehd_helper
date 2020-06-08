package cc.darak.firehd.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import android.os.Handler;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class chkUpdWorker extends Worker {

    Context mContext;
    public static String cv = "na";

    public chkUpdWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG,"Worker initiated");

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                //Log.e(TAG, "Registering worker again...");
                //createWork();
                try {
                    if(isInternetAvailable()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            mContext.startForegroundService(new Intent(mContext, UpdaterService.class));
                        } else {
                            mContext.startService(new Intent(mContext, UpdaterService.class));
                        }
                    } else {
                        Log.e(TAG,"INTERNET NOT AVAILABLE!");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return Result.success();
    }

    public static void createWork() {

        Log.e("DATA", "Creating work");

        WorkManager workmanager = WorkManager.getInstance();

        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                // Many other constraints are available, see the
                // Constraints.Builder reference
                .build();

        // Periodic Work
        PeriodicWorkRequest.Builder job =
                new PeriodicWorkRequest.Builder(chkUpdWorker.class, 1, TimeUnit.HOURS)
                        .setConstraints(myConstraints);

        // Create the actual work object:
        PeriodicWorkRequest periodicJob = job.build();
        // Then enqueue the recurring task:  ExistingPeriodicWorkPolicy.KEEP
        workmanager.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP , periodicJob);
        //WorkManager.getInstance().enqueue(periodicJob);

        //// One Time Work
        //OneTimeWorkRequest onetimeJob = new OneTimeWorkRequest.Builder(chkUpdWorker.class)
        //        .setConstraints(myConstraints).build(); // OneTimeWorkRequest or PeriodicWorkRequest
        //WorkManager.getInstance().enqueue(onetimeJob);
    }

    public boolean isInternetAvailable() throws InterruptedException {

        final boolean[] returnVal = new boolean[1];

        Thread callChkJson = new Thread() {
            public void run() {
                try {
                    InetAddress ipAddr = InetAddress.getByName("www.google.com");
                    //You can replace it with your name
                    returnVal[0] = !ipAddr.equals("");

                } catch (Exception e) {
                    Log.e(TAG, String.valueOf(e));
                    returnVal[0] =  false;
                }
            }
        };
        callChkJson.start();

        callChkJson.join();

        return returnVal[0];
    }
}
