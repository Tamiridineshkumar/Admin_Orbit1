package com.example.webviewapp.ui.theme;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WebAppInterface {
    private Context context;
    private WebView webView;

    public WebAppInterface(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    @JavascriptInterface
    public void printWebPage() {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        if (printManager != null) {
            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("WebView_Print");
            PrintAttributes.Builder builder = new PrintAttributes.Builder();
            builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
            builder.setResolution(new PrintAttributes.Resolution("res1", "Resolution", 600, 600));
            builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
            printManager.print("Print_WebView", printAdapter, builder.build());
        }
    }
}
