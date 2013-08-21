package com.example.videoproxy;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.videoproxy._Enum.MediaAction;

/**
 * Created by Yongwei on 8/19/13.
 */
public abstract class MediaHandler extends Handler implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private MediaObserver observer;

    protected MediaPlayer player;
    protected IMediaEventCallback callback;

    protected abstract void init();
    protected abstract void setSrc(String uri);
    protected abstract void dispatchCustomMessage(Message msg);

    @Override
    public void dispatchMessage(Message msg) {
        Bundle bundle = msg.getData();
        MediaAction action = (MediaAction)bundle.getSerializable("type");
        switch (action) {
            case setSrc:
                if (null == player) {
                    this.init();
                }
                this.setSrc(bundle.getString("src"));
                break;
            case play:
                this.play();
                break;
            case pause:
                this.pause();
                break;
            case stop:
                this.stop();
                break;
            case setCurrentTime:
                this.setCurrentTime(bundle.getFloat("currentTime"));
                break;
            case dispose:
                this.dispose();
                break;
            default:
                this.dispatchCustomMessage(msg);
                break;
        }
    }


    public float getCurrentTime() {
        return (float)player.getCurrentPosition() / 1000;
    }

    public float getDuration() {
        return (float)player.getDuration() / 1000;
    }

    protected void play() {
        if (null == this.player || this.player.isPlaying()) {
            return;
        }
        if(null == observer) {
            // the observer used to update the current video playing time/position to web part.
            observer = new MediaObserver();
        }
        player.start();
        callback.onPlay();
        // start the observer to give the timeupdate callback to web part
        observer.start();
        new Thread(observer).start();
    }

    protected void pause() {
        if (player.isPlaying()) {
            player.pause();
            callback.onPause();
            observer.stop();
        }
    }


    private void setCurrentTime(float currentTime) {
        player.seekTo((int) (currentTime * 1000));
        callback.onSeeked();
    }

    protected void stop(){
        if (null != observer) {
            observer.stop();
        }
//        player.stop();
        player.pause();
        player.seekTo(0);
    }

    protected void dispose() {
        if (null != observer) {
            observer.stop();
        }
        this.player = null;
    }


    /**
     * implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(TAG, "MediaPlayer onPrepared");
        this.player = mediaPlayer;
        callback.onLoaded();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        observer.stop();
        // update latest progress to web part
        callback.onProgressUpdate(this.getCurrentTime());
        callback.onEnded();
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
                callback.onProgressUpdate(MediaHandler.this.getCurrentTime());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
