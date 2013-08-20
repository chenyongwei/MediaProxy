package com.example.videoproxy;

/**
 * Created by Yongwei on 8/19/13.
 * The events from native to web
 */
public interface IMediaEventCallback {
    void onLoaded();
    void onSeeked();
    void onPlay();
    void onPause();
    void onProgressUpdate(float currentTime);
    void onEnded();
}
