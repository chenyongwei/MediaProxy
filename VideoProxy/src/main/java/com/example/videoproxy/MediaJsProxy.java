package com.example.videoproxy;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.VideoView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Yongwei on 8/7/13.
 */
public class MediaJsProxy {

    private final static String TAG = MediaJsProxy.class.getSimpleName();

    private VideoView video;
    private WebView web;

    public MediaJsProxy(VideoView videoView, WebView webView) {
        video = videoView;
        web = webView;

        web.addJavascriptInterface(this, _Constant.GLOBAL_JS_OBJ);
    }

    /**
    * from Javascript call to Native
    * */

    @JavascriptInterface
    public void load(){
        Log.d(TAG, "load");
        // when you setVideoURI to videoView, it will start to loadMetadata immediately.
        // this is not a final solution.
        startVideo();
        pauseVideo();
    }

    @JavascriptInterface
    public void play() {
        Log.d(TAG, "play");
        startVideo();
    }

    @JavascriptInterface
    public void pause(){
        pauseVideo();
    }

    @JavascriptInterface
    public void setSrc(String mediaSrc){
        Log.d(TAG, "setSrc: " + mediaSrc);
        setVideoUrl(mediaSrc);
    }

    @JavascriptInterface
    public void setCurrentTime(float currentTime){
        Log.d(TAG, "setCurrentTime: " + currentTime);
        seekVideo(currentTime);
    }

    @JavascriptInterface
    public float getCurrentTime(){
        float currentTime = getCurrentVideoTime();
        Log.d(TAG, "getCurrentTime: " + currentTime);
        return currentTime;
    }

    /**
     * from Native to Web
     */
    private void onProgressUpdated() {
        web.post(new Runnable() {
            @Override
            public void run() {
                float currentTime = getCurrentVideoTime();
                Log.d(TAG, "updateProgress: " + String.format(_Constant.MEDIA_EVENT_TIMEUPDATE, currentTime));
                web.loadUrl(String.format(_Constant.MEDIA_EVENT_TIMEUPDATE, currentTime));
            }
        });
    }

    private void onEnded() {
        web.post(new Runnable() {
            @Override
            public void run() {
                web.loadUrl(_Constant.MEDIA_EVENT_ENDED);
            }
        });
    }

    private void onLoaded() {
        web.post(new Runnable() {
            @Override
            public void run() {
                web.loadUrl(_Constant.MEDIA_EVENT_LOADEDMETADATA);
            }
        });
    }

    /**
     * private methods to interact with native video player
     */

    private void setVideoUrl(String src) {
        Uri uri = Uri.parse(src);
        // after set videoUri, it will start to load the video stream by default
        video.setVideoURI(uri);
    }

    private void startVideo() {

        // the observer used to update the current video playing time/position to web part.
        final MediaObserver observer = new MediaObserver();
        // add event listener to video
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                onLoaded();
            }
        });
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                observer.stop();
                // update latest progress to web part
                onProgressUpdated();
                // send the ended event to web part
                onEnded();
            }
        });
        video.start();
        // start the observer to give the timeupdate callback to web part
        new Thread(observer).start();
    }

    private void pauseVideo() {
        if (video.isPlaying()) {
            video.pause();
        }
    }

    private void seekVideo(float currentTime) {
        video.seekTo((int)(currentTime * 1000));
    }

    private float getCurrentVideoTime() {
        return ((float)video.getCurrentPosition() / 1000);
    }

    /**
     * inner class
     */
    class MediaObserver implements Runnable {

         private AtomicBoolean stop = new AtomicBoolean(false);

         public void stop() {
             stop.set(true);
         }

         @Override
         public void run() {
             while (!stop.get()) {
                 onProgressUpdated();
                 try {
                     Thread.sleep(200);
                 }
                 catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             }
         }
    }

}