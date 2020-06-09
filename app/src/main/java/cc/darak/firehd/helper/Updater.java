package cc.darak.firehd.helper;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import org.json.JSONObject;
import java.io.File;
import java.net.URL;
import java.util.Scanner;

public class Updater extends AppCompatActivity {

    private static final String TAG = "Updater";
    public String lv;
    public String lb;
    public String du;
    public String callJsonResult;
    public String permStr;
    private long downMgr;
    public String apkPath;
    public String apkName;
    public File f_apkPath;
    public BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.upd_title);
        setContentView(R.layout.activity_updater);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getResources().getString(R.string.folderName);
        apkName = getResources().getString(R.string.apkName);
        f_apkPath = new File(apkPath);

        prepDir();

        Intent updService = new Intent(this, UpdaterService.class);
        stopService(updService);

        TextView textState = (TextView)findViewById(R.id.upd_stat);
        TextView textLVer = (TextView)findViewById(R.id.upd_lver);
        ProgressBar updPg = (ProgressBar)findViewById(R.id.upd_pg);
        Button updInst = (Button)findViewById(R.id.btn_inst);

        chkUpdate();

    }

    public void runUpdate(String uriFile) {
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(uriFile));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(intent);
    }

    public void askUpdate(final String uriFile) {
        // Dialog body
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        // Msg
        alertdialog.setMessage(getResources().getString(R.string.upd_dia_p));

        // Ok btn
        alertdialog.setPositiveButton(getResources().getString(R.string.upd_dia_y), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                runUpdate(uriFile);
            }
        });

        // Cancel btn
        alertdialog.setNegativeButton(getResources().getString(R.string.upd_dia_n), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Edit here
                displayToast(getResources().getString(R.string.upd_toast_cancelled));
            }
        });
        // Create dialog
        AlertDialog alert = alertdialog.create();
        // Set title
        alert.setTitle(getResources().getString(R.string.upd_dia_t));
        // Show dialog
        alert.show();

        // Set btn colore
        Button bt_pos = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bt_pos.setTextColor(Color.BLACK);
        Button bt_neg = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        bt_neg.setTextColor(Color.BLACK);
    }

    public void update() {
        final Button updInst = (Button)findViewById(R.id.btn_inst);
        final Button updChk = (Button)findViewById(R.id.btn_upd);

        updChk.setEnabled(false);
        updInst.setEnabled(false);

        prepDir();
        getApk();
    }

    public void getApk() {

        final Button updInst = (Button)findViewById(R.id.btn_inst);

        //set filename
        final String filename = "khdhelper_latest.apk";
        //DownloadManager.Request created with url.
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(du));
        //cookie
        String cookie = CookieManager.getInstance().getCookie(du);
        //Add cookie and User-Agent to request
        request.addRequestHeader("Cookie", cookie);
        //file scanned by MediaScanner
        request.allowScanningByMediaScanner();
        //Download is visible and its progress, after completion too.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //DownloadManager created
        final DownloadManager[] downloadManager = {(DownloadManager) getSystemService(DOWNLOAD_SERVICE)};
        //Saving files in Download folder
        request.setDestinationInExternalPublicDir(getResources().getString(R.string.folderName), apkName);
        //download enqued
        //downloadManager[0].enqueue(request);
        //Toast.makeText(this, getResources().getString(R.string.upd_dstart), Toast.LENGTH_LONG).show();

        //declaring 'enq' variable (long)
        Log.i("INFO","Download starting");
        downMgr = downloadManager[0].enqueue(request);

        updInst.setText(R.string.upd_btn_down_ing);


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                setVis("updInst",true);
                setVis("updChk",true);

                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downMgr);
                    downloadManager[0] = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    Cursor c = downloadManager[0].query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

                            String uriOri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            //Replace "file:/" to ""
                            String uriFile = uriOri.replace("file:/", "");

                            //TODO : Use this local uri and launch intent to open file
                            unregisterReceiver(receiver);

                            displayToast(getResources().getString(R.string.upd_dfin) + " " + uriFile);

                            setBtnFinished();
                            askUpdate(uriFile);
                            //runUpdate(new File(uriFile));

                        }
                    } else {
                        Log.e(TAG,"Something happened to the downloads");
                        updInst.setText(R.string.upd_btn_down);
                        unregisterReceiver(receiver);
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void prepDir() {
        //Checking runtime permission for devices above Marshmallow.
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            //Permission granted
            permStr = "true";
        } else {
            //requesting permissions.
            ActivityCompat.requestPermissions(Updater.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
                permStr = "true";
            }
            //Permission denied
            permStr = "false";
            setVis("updChk", true);
            setVis("updInst", true);
            return;
        }


        if(!f_apkPath.exists()){
            Log.e("FOLDER", "Folder doesn't exist!");
            f_apkPath.mkdirs();
        }
        Log.i("FOLDER", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getResources().getString(R.string.folderName));


        File outputFile = new File(apkPath, apkName);
        if (outputFile.exists()) {
            outputFile.delete();
        }
    }

    public void chkUpdate() {

        TextView textState = (TextView)findViewById(R.id.upd_stat);
        TextView textLVer = (TextView)findViewById(R.id.upd_lver);
        ProgressBar updPg = (ProgressBar)findViewById(R.id.upd_pg);
        Button updInst = (Button)findViewById(R.id.btn_inst);
        Button updChk = (Button)findViewById(R.id.btn_upd);

        updChk.setEnabled(false);
        updInst.setVisibility(View.INVISIBLE);
        updPg.setVisibility(View.VISIBLE);
        textLVer.setText(R.string.upd_lver_checking);
        textState.setText(R.string.upd_stat_checking);

        if (existFile()) {
            File outputFile = new File(apkPath, apkName);
            outputFile.delete();
        }

        callJsonResult = "0";

        Thread callChkJson = new Thread() {
            public void run() {

                try {
                    chkJSON();
                    callJsonResult = "1";
                    disResult();

                }catch (Exception e) {
                    Log.e("ERROR", String.valueOf(e));
                    callJsonResult = "0";
                    disResult();
                }
            }
        };
        callChkJson.start();
    }

    public void disResult() {

        Log.i("RESULT","chkJSON result: " + callJsonResult);

        final TextView textState = (TextView)findViewById(R.id.upd_stat);
        final TextView textLVer = (TextView)findViewById(R.id.upd_lver);
        final ProgressBar updPg = (ProgressBar)findViewById(R.id.upd_pg);
        final Button updInst = (Button)findViewById(R.id.btn_inst);
        final Button updChk = (Button)findViewById(R.id.btn_upd);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                updPg.setVisibility(View.INVISIBLE);
            }
        });

        if (!callJsonResult.equals("1")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Stuff that updates the UI
                    textState.setText(R.string.upd_stat_error);
                    textLVer.setText(R.string.upd_lver_error);
                    updChk.setEnabled(true);

                }
            });
            return;
        }

        //String to Integer
        int lver = Integer.valueOf(lv);
        int cver = Integer.valueOf(getResources().getString(R.string.buildNumber));

        if (cver>=lver) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Stuff that updates the UI
                    textState.setText(R.string.upd_stat_latest);
                    textLVer.setText(lb);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Stuff that updates the UI
                    textState.setText(R.string.upd_stat_update);
                    textLVer.setText(lb);
                    visUpdBtn();
                }
            });
        }
    }

    public boolean existFile() {
        File outputFile = new File(apkPath, apkName);
        return outputFile.exists();
    }

    private void setBtnFinished() {
        final Button updInst = (Button)findViewById(R.id.btn_inst);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                updInst.setText(R.string.upd_btn_down_inst);
            }
        });
    }

    public void visUpdBtn() {

        final Button updInst = (Button)findViewById(R.id.btn_inst);
        final Button updChk = (Button)findViewById(R.id.btn_upd);

        //Check if update file is ready to install

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                updInst.setVisibility(View.VISIBLE);
                updInst.setText(R.string.upd_btn_down);
                updChk.setEnabled(true);
                updInst.setEnabled(true);
            }
        });
    }

    public void chkJSON() throws Exception {
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
        lb = obj.optString("latestBuild");
        System.out.println(lb);
        du = obj.optString("latestURL");
        System.out.println(du);
    }

    public void onBtnChk(View v) {
        chkUpdate();
    }

    public void onBtnInst(View v) {

        File outputFile = new File(apkPath, apkName);

        if (existFile()) {
            Log.e(TAG,"File already exists! " + String.valueOf(outputFile));
            askUpdate(String.valueOf(outputFile));
        } else if (!existFile()) {
            Log.e(TAG,"File does not exist!");
            update();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void setVis(final String btn, final boolean state) {

        final Button updInst = (Button)findViewById(R.id.btn_inst);
        final Button updChk = (Button)findViewById(R.id.btn_upd);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI

                if (btn.equals("updInst")) {
                    updInst.setEnabled(state);
                } else if (btn.equals("updChk")) {
                    updChk.setEnabled(state);
                }
            }
        });
    }
}
