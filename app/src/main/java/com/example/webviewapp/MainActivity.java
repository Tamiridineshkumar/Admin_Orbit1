package com.example.webviewapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ImageView rotatingProgressBar, staticProgressBar;
    private LinearLayout loadingScreen, noInternetLayout;
    private Button btnRetry;
    private Animation rotateAnimation;
    private String currentUrl = "http://54.225.234.171:80/"; // Default URL
    private String lastLoadedUrl; // Stores the last successfully loaded URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        webView = findViewById(R.id.webView);
        rotatingProgressBar = findViewById(R.id.rotating_progress_bar);
        staticProgressBar = findViewById(R.id.centerImage);
        loadingScreen = findViewById(R.id.loadingScreen);
        noInternetLayout = findViewById(R.id.noInternetLayout);
        btnRetry = findViewById(R.id.btnRetry);

        // Adjust the size of the progress bar images
        rotatingProgressBar.getLayoutParams().width = 500;
        rotatingProgressBar.getLayoutParams().height = 500;
        rotatingProgressBar.requestLayout();

        staticProgressBar.getLayoutParams().width = 2000;
        staticProgressBar.getLayoutParams().height = 2000;
        staticProgressBar.requestLayout();

        // Load rotation animation
        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        setupWebView();

        // Load webpage based on network status
        if (isInternetAvailable()) {
            loadWebsite(currentUrl);
        } else {
            showNoInternetScreen();
        }

        // Retry button to reload the last visited webpage
        btnRetry.setOnClickListener(v -> {
            if (isInternetAvailable()) {
                hideNoInternetScreen();
                loadWebsite(lastLoadedUrl != null ? lastLoadedUrl : currentUrl);
            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                // Show the JavaScript alert message as a Toast
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                result.confirm(); // Dismiss the JavaScript alert
                return true; // We handled the alert
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                lastLoadedUrl = url;
                loadingScreen.setVisibility(View.VISIBLE);
                rotatingProgressBar.setVisibility(View.VISIBLE);
                staticProgressBar.setVisibility(View.VISIBLE);
                rotatingProgressBar.startAnimation(rotateAnimation);
                webView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                webView.postDelayed(() -> {
                    loadingScreen.setVisibility(View.GONE);
                    rotatingProgressBar.clearAnimation();
                    rotatingProgressBar.setVisibility(View.GONE);
                    staticProgressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }, 100);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (lastLoadedUrl == null || !lastLoadedUrl.equals(failingUrl)) {
                    lastLoadedUrl = failingUrl;
                }
                showNoInternetScreen();
            }
        });

        // Prevent long press behavior that might cause unwanted movement
        webView.setOnLongClickListener(v -> true);

        // Prevent multi-touch issues
        webView.setOnTouchListener((v, event) -> {
            if (event.getPointerCount() > 1) {
                return true; // Ignore multi-touch events
            }
            return false;
        });
    }

    private void loadWebsite(String url) {
        if (url == null) url = currentUrl;
        webView.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
        rotatingProgressBar.startAnimation(rotateAnimation);
        webView.loadUrl(url);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void showNoInternetScreen() {
        webView.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.VISIBLE);
        loadingScreen.setVisibility(View.GONE);
        rotatingProgressBar.clearAnimation();
        rotatingProgressBar.setVisibility(View.GONE);
        staticProgressBar.setVisibility(View.GONE);
    }

    private void hideNoInternetScreen() {
        noInternetLayout.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
