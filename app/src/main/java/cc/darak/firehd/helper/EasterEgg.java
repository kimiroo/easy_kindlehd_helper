package cc.darak.firehd.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class EasterEgg extends Activity implements View.OnClickListener{

    //Click counter
    private int ClickCount=0;
    private ImageButton btn_b;
    private ImageButton btn_n;
    private ImageButton btn_p;
    private ImageView eeIView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easteregg);

        //Resets click counter
        ClickCount = 0;

        //Initialize View.OnClickListener
        btn_b = findViewById(R.id.btn_b);
        btn_n = findViewById(R.id.btn_n);
        btn_p = findViewById(R.id.btn_p);
        eeIView = findViewById(R.id.eeIView);
        btn_b.setOnClickListener(this);
        btn_n.setOnClickListener(this);
        btn_p.setOnClickListener(this);
        eeIView.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, EEService.class));
        } else {
            startService(new Intent(this, EEService.class));
        }
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
            case R.id.btn_b:
                //Start activity 1 here, for example
                Toast.makeText(EasterEgg.this, getResources().getString(R.string.back), Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_p:
                //Start activity 3 here
                Toast.makeText(EasterEgg.this, getResources().getString(R.string.play), Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_n:
                //Start activity 2 here
                Toast.makeText(EasterEgg.this, getResources().getString(R.string.next), Toast.LENGTH_LONG).show();
                break;
            case R.id.eeIView:
                ClickCount = ClickCount + 1 ;

                if (ClickCount >= 3) {
                    Toast.makeText(EasterEgg.this, getResources().getString(R.string.ee_t_g), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),BungTak.class));
                } else {
                    Toast.makeText(EasterEgg.this, getResources().getString(R.string.ee_t_c_1) + " " + ClickCount + " " + getResources().getString(R.string.ee_t_c_2), Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
}
