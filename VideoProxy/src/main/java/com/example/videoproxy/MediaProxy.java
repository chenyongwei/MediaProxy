package com.example.videoproxy;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import static com.example.videoproxy._Enum.MediaAction;
import static com.example.videoproxy._Enum.MediaType;


/**
 * Created by Yongwei on 8/7/13.
 */
public class MediaProxy implements IMediaEventCallback {

    private final static String TAG = MediaProxy.class.getSimpleName();

    private WebView web;
    private MediaHandler mediaHandler;
    private String mediaEventObj;

    public MediaProxy(WebView webView, MediaType mediaType) {
        web = webView;
        if (MediaType.audio == mediaType) {
            web.addJavascriptInterface(this, _Constant.GLOBAL_JS_AUDIO);
            mediaHandler = new AudioHandler(this);
            mediaEventObj = _Constant.GLOBAL_JS_AUDIO_EVENT;
        }
        else if(MediaType.video == mediaType) {
            web.addJavascriptInterface(this, _Constant.GLOBAL_JS_VIDEO);
            mediaHandler = new VideoHandler((ViewGroup)web.getParent(), this);
            mediaEventObj = _Constant.GLOBAL_JS_VIDEO_EVENT;
        }
    }

    /**
     * from Javascript call to Native
     */

    @JavascriptInterface
    public void load() {
        Log.d(TAG, "load media");
        // when you setVideoURI to videoView, it will start to loadMetadata immediately.
        // don't need to do anything now
    }

    @JavascriptInterface
    public void play() {
        Message msg = this.createMessage(MediaAction.play);
        mediaHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void pause() {
        Message msg = this.createMessage(MediaAction.pause);
        mediaHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void stop() {
        Message msg = this.createMessage(MediaAction.stop);
        mediaHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void setSrc(String mediaSrc) {
        Message msg = this.createMessage(MediaAction.setSrc);
        Bundle bundle = msg.getData();
        bundle.putString("src", mediaSrc);
        msg.setData(bundle);
        mediaHandler.sendMessage(msg);
        Log.d(TAG, "setSrc(" + mediaSrc + ")");
    }

    @JavascriptInterface
    public void setCurrentTime(float currentTime) {
        Message msg = this.createMessage(MediaAction.setCurrentTime);
        Bundle bundle = msg.getData();
        bundle.putFloat("currentTime", currentTime);
        msg.setData(bundle);
        mediaHandler.sendMessage(msg);
        Log.d(TAG, "setCurrentTime: " + currentTime);

    }

    @JavascriptInterface
    public float getCurrentTime() {
        float currentTime = mediaHandler.getCurrentTime();
        Log.d(TAG, "getCurrentTime: " + currentTime);
        return currentTime;
    }

    @JavascriptInterface
    public void setVideoLayout(int x, int y, int width, int height, int webPageWidth, int webPageHeight) {
        int webViewWidth = web.getWidth();
        int webViewHeight = web.getHeight();
        float ratioWidth = (float)webViewWidth / (float)webPageWidth;
        float ratioHeight = (float)webViewHeight / (float)webPageHeight;

        Message msg = this.createMessage(MediaAction.setVideoLayout);
        // add more bundle data, so get it back first
        Bundle bundle = msg.getData();
        bundle.putInt("x", (int)(x * ratioWidth));
        bundle.putInt("y", (int)(y * ratioHeight));
        bundle.putInt("width", (int)(width * ratioWidth));
        bundle.putInt("height", (int)(height * ratioHeight));
        // reset bundle back
        msg.setData(bundle);
        mediaHandler.sendMessage(msg);
        Log.d(TAG, "Thread name: " + Thread.currentThread().getName());
    }

    @JavascriptInterface
    public void translateVideoX(int x, int webPageWidth) {
        int webViewWidth = web.getWidth();
        float ratioWidth = (float)webViewWidth / (float)webPageWidth;

        Message msg = this.createMessage(MediaAction.translateVideoX);
        Bundle bundle = msg.getData();
        bundle.putFloat("x", x * ratioWidth);
        msg.setData(bundle);
        mediaHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void dispose() {
        Message msg = this.createMessage(MediaAction.dispose);
        mediaHandler.sendMessage(msg);
        Log.d(TAG, "dispose()");
    }

    /**
     * Implemented IMediaEventCallback
     */
    @Override
    public void onLoaded() {
        web.post(new Runnable() {
            @Override
            public void run() {
                float duration = mediaHandler.getDuration();
                web.loadUrl(String.format(_Constant.VIDEO_EVENT_LOADEDMETADATA, mediaEventObj));
                web.loadUrl(String.format(_Constant.VIDEO_EVENT_DURATIONCHANGE, mediaEventObj, duration));
            }
        });
    }

    @Override
    public void onSeeked() {
        web.post(new Runnable() {
            @Override
            public void run() {
                web.loadUrl(String.format(_Constant.VIDEO_EVENT_SEEKED, mediaEventObj));
            }
        });
    }

    @Override
    public void onPlay() {
        web.post(new Runnable() {
            @Override
            public void run() {
                web.loadUrl(String.format(_Constant.VIDEO_EVENT_PLAY, mediaEventObj));
                web.loadUrl(String.format(_Constant.VIDEO_EVENT_PLAYING, mediaEventObj));
            }
        });
    }

    @Override
    public void onPause() {
        web.post(new Runnable() {
            @Override
            public void run() {
                web.loadUrl(String.format(_Constant.VIDEO_EVENT_PAUSE, mediaEventObj));
            }
        });
    }

    @Override
    public void onProgressUpdate(final float currentTime) {
        web.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "updateProgress: " + String.format(_Constant.VIDEO_EVENT_TIMEUPDATE, mediaEventObj, currentTime));
                web.loadUrl(String.format(_Constant.VIDEO_EVENT_TIMEUPDATE, mediaEventObj, currentTime));
            }
        });
    }

    @Override
    public void onEnded() {
        web.post(new Runnable() {
            @Override
            public void run() {
                web.loadUrl(String.format(_Constant.VIDEO_EVENT_ENDED, mediaEventObj));
            }
        });
    }

    /**
     * Private methods
     */
    private Message createMessage(MediaAction actionType) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("type", actionType);

        Message msg = new Message();
        msg.setData(bundle);
        return msg;
    }

}