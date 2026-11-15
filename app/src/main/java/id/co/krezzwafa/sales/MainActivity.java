package id.co.krezzwafa.sales;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.net.Uri;

public class MainActivity extends Activity {
    // URL deployment terbaru yang diberikan pemilik dashboard.
    private static final String DASHBOARD_URL =
        "https://script.google.com/macros/s/AKfycbyXx5dItdXQDg8T3qx-Pos6gpSafq2suZQPvX4WnBVzgyDJWTPom9KaJzYnknlgmkCn/exec";

    private WebView webView;
    private FrameLayout root;
    private View splash;
    private TextView loadingText;
    private ProgressBar progress;
    private boolean pageLoaded = false;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(184,15,23));
        getWindow().setNavigationBarColor(Color.rgb(23,9,10));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(23,9,10));
        webView = new WebView(this);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setLoadsImagesAutomatically(true);
        s.setSupportZoom(false);
        s.setJavaScriptCanOpenWindowsAutomatically(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        s.setUserAgentString(s.getUserAgentString()+" KrezzWafaAndroid/1.1");
        webView.setBackgroundColor(Color.rgb(23,9,10));
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int newProgress) {
                if (progress != null) progress.setProgress(newProgress);
                if (loadingText != null && !pageLoaded)
                    loadingText.setText("Menyiapkan dashboard... " + newProgress + "%");
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                return handleUrl(r.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView v, String url) {
                return handleUrl(Uri.parse(url));
            }
            private boolean handleUrl(Uri u) {
                String scheme=u.getScheme()==null?"":u.getScheme();
                String host=u.getHost()==null?"":u.getHost();
                if (("https".equals(scheme)||"http".equals(scheme)) &&
                    (host.equals("script.google.com") ||
                     host.equals("script.googleusercontent.com") ||
                     host.endsWith(".googleusercontent.com") ||
                     host.equals("accounts.google.com"))) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW,u)); } catch(Exception ignored) {}
                return true;
            }
            @Override public void onPageFinished(WebView v,String url) {
                pageLoaded = true;
                root.postDelayed(() -> showDashboard(),250);
            }
            @Override public void onReceivedError(WebView v, WebResourceRequest request,
                                                   android.webkit.WebResourceError error) {
                if (request.isForMainFrame()) showError();
            }
            @Override public void onReceivedHttpError(WebView v, WebResourceRequest request,
                                                       android.webkit.WebResourceResponse response) {
                if (request.isForMainFrame() && response.getStatusCode() >= 400) showError();
            }
        });

        webView.setVisibility(View.INVISIBLE);
        root.addView(webView,new FrameLayout.LayoutParams(-1,-1));
        splash=makeSplash();
        root.addView(splash,new FrameLayout.LayoutParams(-1,-1));
        setContentView(root);
        webView.loadUrl(DASHBOARD_URL);
    }

    private void showDashboard() {
        if (splash != null) { root.removeView(splash); splash=null; }
        webView.setVisibility(View.VISIBLE);
    }

    private View makeSplash() {
        LinearLayout l=new LinearLayout(this); l.setOrientation(1);
        l.setGravity(Gravity.CENTER); l.setPadding(32,24,32,24);
        l.setBackgroundColor(Color.rgb(23,9,10));
        ImageView logo=new ImageView(this); logo.setImageResource(R.drawable.krezz_logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(220,220);
        lp.bottomMargin=24; l.addView(logo,lp);
        TextView title=new TextView(this); title.setText("KREZZWAFA");
        title.setTextColor(Color.WHITE); title.setTextSize(25);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD); title.setLetterSpacing(.12f);
        title.setGravity(Gravity.CENTER); l.addView(title);
        TextView sub=new TextView(this); sub.setText("SALES MANAGEMENT");
        sub.setTextColor(Color.rgb(255,210,28)); sub.setTextSize(11);
        sub.setLetterSpacing(.18f); sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-2,-2);
        sp.topMargin=8; sp.bottomMargin=28; l.addView(sub,sp);
        progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100); progress.setIndeterminate(true);
        progress.setIndeterminateTintList(android.content.res.ColorStateList.valueOf(Color.rgb(229,9,20)));
        l.addView(progress,new LinearLayout.LayoutParams(170,5));
        loadingText=new TextView(this); loadingText.setText("Menyiapkan dashboard...");
        loadingText.setTextColor(Color.LTGRAY); loadingText.setTextSize(12);
        LinearLayout.LayoutParams tlp=new LinearLayout.LayoutParams(-2,-2);
        tlp.topMargin=15; l.addView(loadingText,tlp);
        return l;
    }

    private void showError() {
        if (pageLoaded) return;
        runOnUiThread(() -> {
            if (splash == null) return;
            LinearLayout panel = new LinearLayout(this);
            panel.setOrientation(1);
            panel.setGravity(Gravity.CENTER);
            panel.setPadding(28,24,28,24);
            panel.setBackgroundColor(Color.rgb(23,9,10));
            TextView title = new TextView(this);
            title.setText("Dashboard belum dapat dibuka");
            title.setTextColor(Color.WHITE);
            title.setTextSize(19);
            title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
            title.setGravity(Gravity.CENTER);
            panel.addView(title);
            TextView info = new TextView(this);
            info.setText("Periksa koneksi internet atau pastikan deployment Google Apps Script masih aktif dan dapat diakses oleh siapa saja.");
            info.setTextColor(Color.LTGRAY);
            info.setTextSize(13);
            info.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(-1,-2);
            ip.topMargin=12; panel.addView(info,ip);
            Button retry = new Button(this);
            retry.setText("COBA LAGI");
            retry.setOnClickListener(view -> {
                pageLoaded=false;
                webView.setVisibility(View.INVISIBLE);
                loadingText=null; progress=null;
                root.removeView(splash);
                splash=makeSplash();
                root.addView(splash,new FrameLayout.LayoutParams(-1,-1));
                webView.loadUrl(DASHBOARD_URL);
            });
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1,-2);
            bp.topMargin=20; panel.addView(retry,bp);
            Button browser = new Button(this);
            browser.setText("BUKA DI BROWSER");
            browser.setOnClickListener(view -> {
                try { startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(DASHBOARD_URL))); }
                catch(Exception ignored) {}
            });
            panel.addView(browser,new LinearLayout.LayoutParams(-1,-2));
            root.removeView(splash);
            splash=panel;
            root.addView(splash,new FrameLayout.LayoutParams(-1,-1));
        });
    }

    @Override public void onBackPressed() {
        if(webView!=null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }
    @Override protected void onDestroy() {
        if(webView!=null){webView.stopLoading();webView.destroy();}
        super.onDestroy();
    }
}