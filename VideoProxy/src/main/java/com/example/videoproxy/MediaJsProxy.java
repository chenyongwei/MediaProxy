package com.example.videoproxy;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by Yongwei on 8/7/13.
 */
public class MediaJsProxy {

    private final static String TAG = MediaJsProxy.class.getSimpleName();

    private VideoView video;
    private WebView web;

    private MediaObserver observer;
    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            Bundle bundle = msg.getData();
            String msgType = bundle.getString("type");
            if ("setLayout" == msgType) {
                setLayoutWrapper(bundle.getInt("x"), bundle.getInt("y"), bundle.getInt("width"), bundle.getInt("height"));
            }
            else if ("hideVideo" == msgType) {
                hideVideoWrapper();
            }
            else if ("showVideo" == msgType) {
                showVideoWrapper();
            }
        }
    };

    public MediaJsProxy(VideoView videoView, WebView webView) {
        video = videoView;
        web = webView;

        web.addJavascriptInterface(this, _Constant.GLOBAL_JS_OBJ);
    }

    /**
     * from Javascript call to Native
     */

    @JavascriptInterface
    public void load() {
        Log.d(TAG, "load");
        // when you setVideoURI to videoView, it will start to loadMetadata immediately.
        // this is not a final solution.
        playVideo();
        pauseVideo();
    }

    @JavascriptInterface
    public void play() {
        Log.d(TAG, "play");
        playVideo();
    }

    @JavascriptInterface
    public void pause() {
        pauseVideo();
    }

    @JavascriptInterface
    public void stop() {
        stopVideo();
    }

    @JavascriptInterface
    public void setSrc(String mediaSrc) {
        Log.d(TAG, "setSrc: " + mediaSrc);
        setVideoUrl(mediaSrc);
    }

    @JavascriptInterface
    public void setCurrentTime(float currentTime) {
        Log.d(TAG, "setCurrentTime: " + currentTime);
        seekVideo(currentTime);
    }

    @JavascriptInterface
    public float getCurrentTime() {
        float currentTime = getCurrentVideoTime();
        Log.d(TAG, "getCurrentTime: " + currentTime);
        return currentTime;
    }

    @JavascriptInterface
    public void setLayout(final int x, final int y, final int width, final int height) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "setLayout");
        bundle.putInt("x", x);
        bundle.putInt("y", y);
        bundle.putInt("width", width);
        bundle.putInt("height", height);

        Message msg = new Message();
        msg.setData(bundle);

        handler.sendMessage(msg);
        Log.d(TAG, "Thread name: " + Thread.currentThread().getName());
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


    private void setLayoutWrapper(int x, int y, int width, int height) {
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) video.getLayoutParams();

        Log.d(TAG, "setLayout, old layout, x: " + relativeParams.leftMargin + ", y: " + relativeParams.topMargin + ", width: " + relativeParams.width + ", height: " + relativeParams.height);

        relativeParams.leftMargin = x;
        relativeParams.topMargin = y;
        relativeParams.width = width;
        relativeParams.height = height;

        Log.d(TAG, "setLayout, new layout, x: " + relativeParams.leftMargin + ", y: " + relativeParams.topMargin + ", width: " + relativeParams.width + ", height: " + relativeParams.height);

        Log.d(TAG, "Thread name: " + Thread.currentThread().getName());
        video.setLayoutParams(relativeParams);
    }

    private void showVideo() {
        Bundle bundle = new Bundle();
        bundle.putString("type", "showVideo");
        Message msg = new Message();
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
    private void showVideoWrapper() {
        video.setVisibility(View.VISIBLE);
    }
    private void hideVideo() {
        Bundle bundle = new Bundle();
        bundle.putString("type", "hideVideo");
        Message msg = new Message();
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
    private void hideVideoWrapper() {
        video.setVisibility(View.INVISIBLE);
    }


    private void setVideoUrl(String src) {
        Uri uri = Uri.parse(src);
        // after set videoUri, it will start to load the video stream by default
        video.setVideoURI(uri);
        // reset the video to hide when changed videoUrl
        hideVideo();
    }

    private void playVideo() {
        if (video.isPlaying()) {
            return;
        }
        if(null == observer) {
            // the observer used to update the current video playing time/position to web part.
            observer = new MediaObserver();
            // add event listener to video
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    onLoaded();
                    showVideo();
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
                    hideVideo();
                }
            });

        }
        showVideo();
        video.start();
        // start the observer to give the timeupdate callback to web part
        observer.start();
        new Thread(observer).start();
    }

    private void pauseVideo() {
        if (video.isPlaying()) {
            video.pause();
            observer.stop();
        }
    }

    private void stopVideo() {
        video.stopPlayback();
        hideVideo();
    }

    private void seekVideo(float currentTime) {
        video.seekTo((int) (currentTime * 1000));
    }

    private float getCurrentVideoTime() {
        return ((float) video.getCurrentPosition() / 1000);
    }

    /**
     * inner class
     */
    class MediaObserver implements Runnable {

        private AtomicBoolean stop = new AtomicBoolean(false);

        public void stop() {
            stop.set(true);
        }

        public void start() {
            stop.set(false);
        }

        @Override
        public void run() {
            Log.d(TAG, "timer thread running");
            while (!stop.get()) {
                onProgressUpdated();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}