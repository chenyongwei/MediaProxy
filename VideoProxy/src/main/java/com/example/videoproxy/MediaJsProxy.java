package com.example.videoproxy;

import android.media.MediaPlayer;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.VideoView;

/**
 * Created by Yongwei on 8/7/13.
 */
public class MediaJsProxy implements MediaPlayer.OnCompletionListener,
                                     MediaPlayer.OnPreparedListener,
                                     MediaPlayerExt.OnProgressUpdate
                                     {

    private final static String TAG = MediaJsProxy.class.getSimpleName();

    private final VideoView video;
    private final WebView web;

    public MediaJsProxy(VideoView videoView, WebView webView) {
        video = videoView;
        web = webView;
    }


    /**
    * from Javascript call to Native
    * */

    @JavascriptInterface
    public void load(){
        Log.d(TAG, "pause");
        // when you setVideoURI to videoView, it will start to loadMetadata immediately.
        // this is not a final solution.
        video.start();
        video.pause();
    }

    @JavascriptInterface
    public void play() {
        Log.d(TAG, "play");
        video.start();
    }

    @JavascriptInterface
    public void pause(){
        Log.d(TAG, "pause");
        video.pause();
    }

    @JavascriptInterface
    public void setCurrentTime(float currentTime){
        Log.d(TAG, "setCurrentTime: " + currentTime);
        video.seekTo((int)(currentTime * 1000));
    }

     @JavascriptInterface
     public float getCurrentTime(){
         float currentTime = (float)(video.getCurrentPosition() / 1000);
         Log.d(TAG, "getCurrentTime: " + currentTime);
         return currentTime;
     }

    /**
     * from Native call to Javascript
    * */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        web.loadUrl(_Constant.MEDIA_EVENT_ENDED);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        web.loadUrl(_Constant.MEDIA_EVENT_LOADEDMETADATA);
    }

    @Override
    public void OnProgressUpdate(MediaPlayer mediaPlayer, float currentTime) {
        web.loadUrl(String.format(_Constant.MEDIA_EVENT_LOADEDMETADATA, currentTime));
    }

}