package cc.darak.firehd.helper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AdminPortal extends AppCompatActivity {

    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_portal);
        setTitle(R.string.device_admin_portal_title);

        TextView tvAdminState = (TextView)findViewById(R.id.dis_admin_state);
        TextView tvAdminType = (TextView)findViewById(R.id.dis_admin_type);

        mAdminComponentName = new ComponentName(this, AdminReceiver.class);
        mDevicePolicyManager = (DevicePolicyManager)this.getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (isDeviceAdminApp()) {
            tvAdminState.setText(getResources().getString(R.string.admin_s_true));
            if (isDeviceOwnerApp()) {
                tvAdminType.setText(getResources().getString(R.string.admin_t_own));
            } else {
                tvAdminType.setText(getResources().getString(R.string.admin_t_adm));
            }
        } else {
            tvAdminState.setText(getResources().getString(R.string.admin_s_false));
            tvAdminType.setText(getResources().getString(R.string.admin_t_na));
        }
    }

    public void btnEnableRestriction(View v) {
        if (isDeviceOwnerApp()) {
            SharedPreferences adminPref = getSharedPreferences("SavedPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = adminPref.edit();
            editor.putString("Restriction", "true");
            editor.apply();

            enableRestriction();
        } else if (isDeviceAdminApp()) {
            Toast.makeText(this, getResources().getString(R.string.task_fail_adm), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.disable_fail_not_admin), Toast.LENGTH_SHORT).show();
        }
    }

    public void btnDisableRestriction(View v) {
        if (isDeviceOwnerApp()) {
            SharedPreferences adminPref = getSharedPreferences("SavedPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = adminPref.edit();
            editor.putString("Restriction", "false");
            editor.apply();

            disableRestriction();
        } else if (isDeviceAdminApp()) {
            Toast.makeText(this, getResources().getString(R.string.task_fail_adm), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.disable_fail_not_admin), Toast.LENGTH_SHORT).show();
        }
    }

    public void btnDisableAdmin(View v) {
        disableAdmin();
    }

    private void enableRestriction() {
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, true);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, true);
        Toast.makeText(this, getResources().getString(R.string.task_success), Toast.LENGTH_SHORT).show();
    }

    private void disableRestriction() {
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, true);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, true);
        Toast.makeText(this, getResources().getString(R.string.task_success), Toast.LENGTH_SHORT).show();
    }

    private void disableAdmin() {
        if (isDeviceAdminApp()) {
            if (!isDeviceOwnerApp()) {
                mAdminComponentName = new ComponentName(this, AdminReceiver.class);
                mDevicePolicyManager = (DevicePolicyManager)this.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDevicePolicyManager.removeActiveAdmin(mAdminComponentName);
                finish();
            } else {
                Toast.makeText(this, getResources().getString(R.string.disable_fail_own), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.disable_fail_not_admin), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDeviceAdminApp() {
        return mDevicePolicyManager.isAdminActive(mAdminComponentName);
    }

    private boolean isDeviceOwnerApp() {
        return mDevicePolicyManager.isDeviceOwnerApp(getPackageName());
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
}