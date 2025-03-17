package com.example.webviewapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private ImageView centerImage;
    private LinearLayout loadingScreen, noInternetLayout;
    private Button btnRetry;
    private String currentUrl = "http://54.225.234.171:80/"; // Replace with your URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        webView = findViewById(R.id.webView);
        loadingScreen = findViewById(R.id.loadingScreen);
        progressBar = findViewById(R.id.progressBar);
        centerImage = findViewById(R.id.centerImage);
        noInternetLayout = findViewById(R.id.noInternetLayout);
        btnRetry = findViewById(R.id.btnRetry);

        setupWebView();

        // Load webpage based on network status
        if (isInternetAvailable()) {
            loadWebsite(currentUrl);
        } else {
            showNoInternetScreen();
        }

        // Retry button to reload webpage
        btnRetry.setOnClickListener(v -> {
            if (isInternetAvailable()) {
                hideNoInternetScreen();
                loadWebsite(currentUrl);
            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Setup WebView settings
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadingScreen.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                centerImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loadingScreen.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                centerImage.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                currentUrl = url; // Save the current page URL
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showNoInternetScreen();
            }
        });
    }

    // Load the webpage in WebView
    private void loadWebsite(String url) {
        webView.setVisibility(View.VISIBLE);
        loadingScreen.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
        webView.loadUrl(url);
    }

    // Check if the internet is available
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    // Show No Internet Layout
    private void showNoInternetScreen() {
        webView.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.VISIBLE);
        loadingScreen.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        centerImage.setVisibility(View.GONE);
        Log.d("VisibilityCheck", "No Internet Screen is now VISIBLE");
    }

    // Hide No Internet Layout when internet is restored
    private void hideNoInternetScreen() {
        noInternetLayout.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    // Handle back button for WebView navigation
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
