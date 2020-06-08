package cc.darak.firehd.helper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AboutPage extends AppCompatActivity implements View.OnClickListener {
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    //Click counter
    private int ClickCount=0;
    private int ClickLeft=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.ab_title);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ClickCount = 0;

        //Version button
        LinearLayout btn_ver = findViewById(R.id.easter);
        btn_ver.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ClickCount = 0;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.easter:
                ClickCount = ClickCount + 1 ;
                ClickLeft = 5 - ClickCount ;
                if (ClickCount == 1) {
                    //startActivity(new Intent(getApplicationContext(),EasterEgg.class));
                    Toast.makeText(this, getResources().getString(R.string.pre_egg1), Toast.LENGTH_SHORT).show();
                    break;
                } else if (ClickCount >= 2) {
                    if (ClickCount <= 4) {
                        Toast.makeText(this, ClickLeft + " " + getResources().getString(R.string.pre_egg2), Toast.LENGTH_SHORT).show();
                    } else if (ClickCount >= 5) {
                        SharedPreferences eePref = getSharedPreferences("SavedPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = eePref.edit();
                            editor.putString("EEActive", "true");
                            editor.apply();
                        Toast.makeText(this, getResources().getString(R.string.pre_egg3), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(),EasterEgg.class));
                    }
                }
        }
    }

    public void onBtnUpd(View v) {
        startActivity(new Intent(getApplicationContext(),Updater.class));
        //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.url_base) + "/" + getResources().getString(R.string.lang) + getResources().getString(R.string.url_update) + "?v=" + getResources().getString(R.string.versionName)));
        //startActivity(browserIntent);
    }

    public void onBtnCon(View v) {
        popupCon();
    }

    public void onTxtHun(View v) {
        popupHun();
    }

    public void popupCon() {
        // Dialog body
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        // Msg
        alertdialog.setMessage(getResources().getString(R.string.ab_p_cp));

        // Email btn
        alertdialog.setNegativeButton(getResources().getString(R.string.ab_p_ce), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.url_con_e)));
                startActivity(browserIntent);
            }
        });

        // Web btn
        alertdialog.setPositiveButton(getResources().getString(R.string.ab_p_cw), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.url_base) + "/" + getResources().getString(R.string.lang) + getResources().getString(R.string.url_con_w)));
                startActivity(browserIntent);
            }
        });

        // Create dialog
        AlertDialog alert = alertdialog.create();
        // Set title
        alert.setTitle(getResources().getString(R.string.ab_p_ct));
        // Show dialog
        alert.show();

        // Set btn color
        Button bt_neg = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        bt_neg.setTextColor(Color.BLACK);
        Button bt_pos = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bt_pos.setTextColor(Color.BLACK);

    }

    public void popupHun() {
        // Dialog body
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        // Msg
        alertdialog.setMessage(getResources().getString(R.string.ab_p_hp));

        // Ok btn
        alertdialog.setPositiveButton(getResources().getString(R.string.ab_p_hy), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AboutPage.this, getResources().getString(R.string.ab_p_toast), Toast.LENGTH_LONG).show();
            }
        });

        // Create dialog
        AlertDialog alert = alertdialog.create();
        // Set title
        alert.setTitle(getResources().getString(R.string.ab_p_ht));
        // Show dialog
        alert.show();

        // Set btn color
        Button bt_pos = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bt_pos.setTextColor(Color.BLACK);
    }
}
