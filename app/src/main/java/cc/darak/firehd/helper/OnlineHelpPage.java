package cc.darak.firehd.helper;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OnlineHelpPage extends AppCompatActivity {

    private ProgressBar progressBar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.ohelp_title);
        setContentView(R.layout.activity_ohelp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.ohelp_wview);
        progressBar = (ProgressBar) findViewById(R.id.ohelp_pg);

        webView.setWebChromeClient( new MyWebChromeClient());
        webView.setWebViewClient( new webClient());
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getResources().getString(R.string.url_base) + "/" + getResources().getString(R.string.lang) + getResources().getString(R.string.url_ohelp));

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                try {
                    webView.loadUrl("file:///android_asset/www/error." + getResources().getString(R.string.lang) + ".html?errorCode=" + errorCode + "&errorDescription=" + description);
                }catch  (Exception e) {
                    Log.e("error", e.toString());
                }
            }
        });
    }

    public class MyWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(newProgress);
            if(newProgress == 100) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    public class webClient extends WebViewClient {
        public boolean  shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ohelp, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ohelp_home :
                webView.loadUrl(getResources().getString(R.string.url_base) + "/" + getResources().getString(R.string.lang) + getResources().getString(R.string.url_ohelp));
                return true ;
            case R.id.ohelp_refresh :
                webView.reload();
                return true ;
            case R.id.ohelp_back :
                webView.goBack();
                return true ;
            case R.id.ohelp_forward :
                webView.goForward();
                return true ;
            case android.R.id.home:
                finish();
                return true;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }
}
