package id.co.krezzwafa.sales;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
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
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(184, 15, 23));
        getWindow().setNavigationBarColor(Color.rgb(23, 9, 10));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(new WebView(this), true);

        root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(23, 9, 10));
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);
        settings.setUserAgentString(settings.getUserAgentString() + " KrezzWafaAndroid/1.0");
        webView.setBackgroundColor(Color.rgb(23, 9, 10));
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme() == null ? "" : uri.getScheme();
                String host = uri.getHost() == null ? "" : uri.getHost();
                if ("http".equals(scheme) || "https".equals(scheme)) {
                    if (host.equals("script.google.com") || host.endsWith(".googleusercontent.com")
                        || host.equals("accounts.google.com")) return false;
                    try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
                    catch (Exception ignored) { }
                    return true;
                }
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
                catch (Exception ignored) { }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                root.postDelayed(() -> {
                    if (splash != null) {
                        root.removeView(splash);
                        splash = null;
                    }
                    webView.setVisibility(View.VISIBLE);
                }, 350);
            }
        });

        webView.setVisibility(View.INVISIBLE);
        root.addView(webView, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        splash = createSplash();
        root.addView(splash, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(root);
        webView.loadUrl(DASHBOARD_URL);
    }

    private View createSplash() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(36, 30, 36, 30);
        layout.setBackgroundColor(Color.rgb(23, 9, 10));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.krezz_logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(230, 230);
        logoParams.bottomMargin = 26;
        layout.addView(logo, logoParams);

        TextView title = new TextView(this);
        title.setText("KREZZWAFA");
        title.setTextColor(Color.WHITE);
        title.setTextSize(25);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setLetterSpacing(0.12f);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("SALES MANAGEMENT");
        subtitle.setTextColor(Color.rgb(255, 210, 28));
        subtitle.setTextSize(11);
        subtitle.setLetterSpacing(0.2f);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subParams.topMargin = 8;
        subParams.bottomMargin = 28;
        layout.addView(subtitle, subParams);

        ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setIndeterminate(true);
        bar.setIndeterminateTintList(android.content.res.ColorStateList.valueOf(Color.rgb(229, 9, 20)));
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(150, 4);
        layout.addView(bar, barParams);

        TextView loading = new TextView(this);
        loading.setText("Menyiapkan dashboard...");
        loading.setTextColor(Color.rgb(190, 170, 170));
        loading.setTextSize(12);
        LinearLayout.LayoutParams loadParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadParams.topMargin = 15;
        layout.addView(loading, loadParams);
        return layout;
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }
}
