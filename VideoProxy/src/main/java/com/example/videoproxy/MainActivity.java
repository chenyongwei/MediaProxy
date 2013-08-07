package com.example.videoproxy;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.webkit.*;
import android.widget.VideoView;

public class MainActivity extends Activity {

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final WebView webView = (WebView) findViewById(R.id.webView);
        final VideoView videoView = (VideoView) findViewById(R.id.videoView);
        MediaJsProxy videoJsProxy = new MediaJsProxy(videoView, webView);

        this.initWebView(webView);
        this.initVideoView(videoView, videoJsProxy);

        webView.addJavascriptInterface(videoJsProxy, _Constant.GLOBAL_JS_OBJ);
        webView.loadUrl("file:///android_asset/video-test/index.html");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initWebView(WebView webView){
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
    }

    private void initVideoView(VideoView videoView, MediaJsProxy videoJsProxy){
        videoView.setOnCompletionListener(videoJsProxy);
        videoView.setOnPreparedListener(videoJsProxy);
//        String uriPath = "file:///android_asset/video-test/trailer.mp4";
        String uriPath = "http://media.w3.org/2010/05/sintel/trailer.mp4";
        Uri uri = Uri.parse(uriPath);
        videoView.setVideoURI(uri);
    }







}
//
//
//public class MediaObserver implements Runnable {
//    private AtomicBoolean stop = new AtomicBoolean(false);
//
//    public void stop() {
//        stop.set(true);
//    }
//
//    @Override
//    public void run() {
//        while (!stop.get()) {
////            progress.setProgress(mediaPlayer.getCurrentPosition());
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//
//private MediaObserver observer = null;
//
//    public void runMedia() {
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener{
//            @Override
//            public void onComplete(MediaPlayer mp) {
//                observer.stop();
//                progress.setProgress(mp.getCurrentPosition());
//            }
//        });
//
//
//        observer = new MediaObserver();
//        mediaPlayer.start();
//        new Thread(observer).start();
//    }