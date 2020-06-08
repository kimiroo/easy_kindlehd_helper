package cc.darak.firehd.helper;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class EERescue extends AppCompatActivity {

    private CheckBox cb_1 = null;
    private CheckBox cb_2 = null;
    private CheckBox cb_3 = null;
    private CheckBox cb_fn = null;
    private Button bt_ds = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.eeresc_title);
        setContentView(R.layout.activity_eeresc);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cb_1 = (CheckBox) findViewById(R.id.rsc_cb_1);
        cb_2 = (CheckBox) findViewById(R.id.rsc_cb_2);
        cb_3 = (CheckBox) findViewById(R.id.rsc_cb_3);
        cb_fn = (CheckBox) findViewById(R.id.rsc_cb_fn);
        bt_ds = (Button) findViewById(R.id.rsc_bt_disarm);

        cb_fn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_fn.isChecked()){
                    cb_1.setChecked(true);
                    cb_2.setChecked(true);
                    cb_3.setChecked(true);
                }else{
                    cb_1.setChecked(false);
                    cb_2.setChecked(false);
                    cb_3.setChecked(false);
                }
            }
        });

        bt_ds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_fn.isChecked()) {
                    popupEgg();
                } else {
                    Toast.makeText(EERescue.this, getResources().getString(R.string.eesrc_t_bad), Toast.LENGTH_LONG).show();
                }
            }
        });

        cb_1.setOnClickListener(onCheckBoxClickListener);
        cb_2.setOnClickListener(onCheckBoxClickListener);
        cb_3.setOnClickListener(onCheckBoxClickListener);
    }

    public void popupEgg() {
        // Dialog body
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        // Msg
        alertdialog.setMessage(getResources().getString(R.string.eersc_p_p));

        // Ok btn
        alertdialog.setPositiveButton(getResources().getString(R.string.eersc_p_y), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences eePref = getSharedPreferences("SavedPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = eePref.edit();
                editor.putString("EEActive", "false");
                editor.apply();
                disableEgg();
                Toast.makeText(EERescue.this, getResources().getString(R.string.eesrc_t_disabled), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Cancel btn
        alertdialog.setNegativeButton(getResources().getString(R.string.eesrc_p_n), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(EERescue.this, getResources().getString(R.string.eesrc_t_c), Toast.LENGTH_SHORT).show();
            }
        });
        // Create dialog
        AlertDialog alert = alertdialog.create();
        // Set title
        alert.setTitle(getResources().getString(R.string.eersc_p_t));
        // Show dialog
        alert.show();

        // Set btn color
        Button bt_pos = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bt_pos.setTextColor(Color.BLACK);
        Button bt_neg = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        bt_neg.setTextColor(Color.BLACK);
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

    private View.OnClickListener onCheckBoxClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(isAllChecked()){
                cb_fn.setChecked(true);

            }else{
                cb_fn.setChecked(false);
            }

        }
    };


    private boolean isAllChecked(){
        return (cb_1.isChecked() && cb_2.isChecked() && cb_3.isChecked()) ?  true :  false;
    }

    private void disableEgg(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, EEService.class));
        } else {
            startService(new Intent(this, EEService.class));
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                ((EEService) EEService.mContext).pause();
                ((EEService) EEService.mContext).stopEgg();
                ((EEService) EEService.mContext).destroy();
            }
        }, 500);
    }
}
