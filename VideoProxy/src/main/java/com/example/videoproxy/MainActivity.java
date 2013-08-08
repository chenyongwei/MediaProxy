package com.example.videoproxy;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.webkit.*;
import android.widget.VideoView;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends Activity {

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WebView webView = (WebView) findViewById(R.id.webView);
        VideoView videoView = (VideoView) findViewById(R.id.videoView);

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
        webView.loadUrl("file:///android_asset/video-test/index.html");

        // this is the only code to startup the MediaJsProxy
        new MediaJsProxy(videoView, webView);
    }

}


