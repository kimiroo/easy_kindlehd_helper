package cc.darak.firehd.helper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private String eeRescEnabled;

    // for device admin app
    static final int DEVICE_ADMIN_ADD_REQUEST = 1001;
    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.main_title);
        setContentView(R.layout.activity_main);

        chkUpdWorker.createWork();

        // Get Admin Restriction Settings
        SharedPreferences AdmPreferences = this.getSharedPreferences("SavedPref", Context.MODE_PRIVATE);
        String admState = AdmPreferences.getString("Restriction", "true");

        // Get Ask Admin Settings
        SharedPreferences AskAdmPreferences = this.getSharedPreferences("SavedPref", Context.MODE_PRIVATE);
        String askadmState = AskAdmPreferences.getString("StopAskAdmin", "false");

        ListView listview ;
        ListViewAdapter adapter;
        eeRescEnabled = "false";

        // Create adapter
        adapter = new ListViewAdapter() ;

        // Get listview & apply it to adapter
        listview = (ListView) findViewById(R.id.main_list1);
        listview.setAdapter(adapter);

        // Get EasterEgg State
        SharedPreferences preferences = this.getSharedPreferences("SavedPref", Context.MODE_PRIVATE);
        String eeState = preferences.getString("EEActive", "false");

        // 1st item
        adapter.addItem(getString(R.string.warn_title), getString(R.string.desc_warn)) ;
        // 2nd item
        adapter.addItem(getString(R.string.man_title), getString(R.string.desc_man)) ;
        // 3rd item
        adapter.addItem(getString(R.string.ohelp_title), getString(R.string.desc_ohel)) ;
        // 4th item
        if (eeState.equals("true")) {
            eeRescEnabled = "true";
            adapter.addItem(getString(R.string.eeresc_title), getString(R.string.desc_eeresc)) ;
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position) ;

                String titleStr = item.getTitle() ;
                String descStr = item.getDesc() ;

                // TODO : use item data.

                if (Objects.equals(titleStr, new String(getString(R.string.warn_title)))) {
                    startActivity(new Intent(getApplicationContext(),WarningPage.class));
                } else if (Objects.equals(titleStr, new String(getString(R.string.man_title)))) {
                    startActivity(new Intent(getApplicationContext(),ManualPage.class));
                } else if (Objects.equals(titleStr, new String(getString(R.string.ohelp_title)))) {
                    startActivity(new Intent(getApplicationContext(),OnlineHelpPage.class));
                } else if (Objects.equals(titleStr, new String(getString(R.string.eeresc_title)))) {
                    startActivity(new Intent(getApplicationContext(),EERescue.class));
                }

            }
        }) ;

        TextView btn_about = (TextView) findViewById(R.id.MAbout);

        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AboutPage.class));
            }
        });

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminComponentName = new ComponentName(this, AdminReceiver.class);

        if (isDeviceOwnerApp()) {
            if (admState.equals("true")) {
                defaultRestriction();
            }
        } else if(isDeviceAdminApp()) {
            //null
        } else {
            if (askadmState.equals("false")) {
                askAdmin();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get EasterEgg State
        SharedPreferences preferences = this.getSharedPreferences("SavedPref", Context.MODE_PRIVATE);
        String eeState = preferences.getString("EEActive", "false");

        if (eeRescEnabled.equals("false")) {
            if (eeState.equals("true")) {
                listEaster();
            } else {
                listOrdinary();
            }
        }
        if (eeRescEnabled.equals("true")) {
            if (eeState.equals("false")) {
                listOrdinary();
            } else {
                listEaster();
            }
        }
    }

    public void listOrdinary() {
        String eeRescEnabled = "false";

        ListView listview ;
        ListViewAdapter adapter;
        eeRescEnabled = "false";

        // Create adapter
        adapter = new ListViewAdapter() ;

        // Get listview & apply it to adapter
        listview = (ListView) findViewById(R.id.main_list1);
        listview.setAdapter(adapter);

        // 1st item
        adapter.addItem(getString(R.string.warn_title), getString(R.string.desc_warn)) ;
        // 2nd item
        adapter.addItem(getString(R.string.man_title), getString(R.string.desc_man)) ;
        // 3rd item
        adapter.addItem(getString(R.string.ohelp_title), getString(R.string.desc_ohel)) ;
    }

    public void askAdmin() {
        // Dialog body
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        // Msg
        alertdialog.setMessage(getResources().getString(R.string.ask_admin_text));

        // Ok btn
        alertdialog.setPositiveButton(getResources().getString(R.string.ask_admin_y), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences eePref = getSharedPreferences("SavedPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = eePref.edit();
                editor.putString("Restriction", "true");
                editor.apply();
                enableAdmin();
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.eesrc_t_disabled), Toast.LENGTH_SHORT).show();
                //finish();
            }
        });

        // Cancel btn
        alertdialog.setNegativeButton(getResources().getString(R.string.ask_admin_n), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.eesrc_t_c), Toast.LENGTH_SHORT).show();
            }
        });
        // Create dialog
        AlertDialog alert = alertdialog.create();
        // Set title
        alert.setTitle(getResources().getString(R.string.ask_admin_title));
        // Show dialog
        alert.show();

        // Set btn color
        Button bt_pos = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bt_pos.setTextColor(Color.BLACK);
        Button bt_neg = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        bt_neg.setTextColor(Color.BLACK);




    }

    public void listEaster() {
        String eeRescEnabled = "true";

        ListView listview ;
        ListViewAdapter adapter;
        eeRescEnabled = "false";

        // Create adapter
        adapter = new ListViewAdapter() ;

        // Get listview & apply it to adapter
        listview = (ListView) findViewById(R.id.main_list1);
        listview.setAdapter(adapter);

        // 1st item
        adapter.addItem(getString(R.string.warn_title), getString(R.string.desc_warn)) ;
        // 2nd item
        adapter.addItem(getString(R.string.man_title), getString(R.string.desc_man)) ;
        // 3rd item
        adapter.addItem(getString(R.string.ohelp_title), getString(R.string.desc_ohel)) ;
        // 4th item
        adapter.addItem(getString(R.string.eeresc_title), getString(R.string.desc_eeresc)) ;
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
        return mDevicePolicyManager.isAdminActive(mAdminComponentName);
    }

    private boolean isDeviceOwnerApp() {
        return mDevicePolicyManager.isDeviceOwnerApp(getPackageName());
    }

    private void enableAdmin() {
        if (isDeviceAdminApp()) {
            return;
        }
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminComponentName);
        // Start the add device admin activity
        startActivityForResult(intent, DEVICE_ADMIN_ADD_REQUEST);
    }

    private void disableAdmin() {
        if (!isDeviceAdminApp()) {
            return;
        }
        mDevicePolicyManager.removeActiveAdmin(mAdminComponentName);
    }


}
