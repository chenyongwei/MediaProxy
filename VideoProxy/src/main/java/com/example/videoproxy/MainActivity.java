package com.example.videoproxy;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.webkit.*;

import static com.example.videoproxy._Enum.MediaType;


public class MainActivity extends Activity {

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WebView webView = (WebView) findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("WebView Console Log: ", cm.message() + " -- From line " + cm.lineNumber()
                        + " of " + cm.sourceId());
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient());

//        File filesDir = getApplicationContext().getAssets().openFd("index.html").getFileDescriptor();
//        String appPath = filesDir.getPath();
//        Log.d("application path", appPath);
        webView.loadUrl("file:///android_asset/video-test/index.html");

        new MediaProxy(webView, MediaType.video);
        new MediaProxy(webView, MediaType.audio);
    }

}


