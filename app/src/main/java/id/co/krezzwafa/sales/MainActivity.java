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
import android.content.Intent;
import android.net.Uri;

public class MainActivity extends Activity {
    private static final String DASHBOARD_URL =
        "https://script.google.com/macros/s/AKfycbzqwtmEvZx4Eeh6s_XWRbkgVwjKuGxPmKrOjBOYOPG_sQHVe3uWtBaJJhpGMyNssl6/exec";
    private WebView webView;
    private FrameLayout root;
    private View splash;

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
        s.setUserAgentString(s.getUserAgentString()+" KrezzWafaAndroid/1.0");
        webView.setBackgroundColor(Color.rgb(23,9,10));
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u=r.getUrl(); String scheme=u.getScheme()==null?"":u.getScheme();
                String host=u.getHost()==null?"":u.getHost();
                if (("https".equals(scheme)||"http".equals(scheme)) &&
                    (host.equals("script.google.com") || host.endsWith(".googleusercontent.com") ||
                     host.equals("accounts.google.com"))) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW,u)); } catch(Exception ignored) {}
                return true;
            }
            @Override public void onPageFinished(WebView v,String url) {
                root.postDelayed(() -> {
                    if(splash!=null){root.removeView(splash);splash=null;}
                    webView.setVisibility(View.VISIBLE);
                },300);
            }
        });
        webView.setVisibility(View.INVISIBLE);
        root.addView(webView,new FrameLayout.LayoutParams(-1,-1));
        splash=makeSplash();
        root.addView(splash,new FrameLayout.LayoutParams(-1,-1));
        setContentView(root);
        webView.loadUrl(DASHBOARD_URL);
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
        ProgressBar p=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        p.setIndeterminate(true);
        p.setIndeterminateTintList(android.content.res.ColorStateList.valueOf(Color.rgb(229,9,20)));
        l.addView(p,new LinearLayout.LayoutParams(150,5));
        TextView loading=new TextView(this); loading.setText("Menyiapkan dashboard...");
        loading.setTextColor(Color.LTGRAY); loading.setTextSize(12);
        LinearLayout.LayoutParams tlp=new LinearLayout.LayoutParams(-2,-2);
        tlp.topMargin=15; l.addView(loading,tlp);
        return l;
    }
    @Override public void onBackPressed() {
        if(webView!=null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }
    @Override protected void onDestroy() {
        if(webView!=null){webView.stopLoading();webView.destroy();}
        super.onDestroy();
    }
}