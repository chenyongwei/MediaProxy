package com.example.videoproxy;

import android.media.MediaPlayer;
import android.os.Message;
import android.util.Log;

/**
 * Created by Yongwei on 8/19/13.
 */
public class AudioHandler extends MediaHandler{

    private final static String TAG = MediaProxy.class.getSimpleName();

    private MediaPlayer audio;

    public AudioHandler(IMediaEventCallback mediaEventCallback) {
        super.callback = mediaEventCallback;
    }

    @Override
    protected void init() {
        Log.d(TAG, "Audio player inited");
        this.audio = new MediaPlayer();
        this.audio.setOnPreparedListener(this);
        this.audio.setOnCompletionListener(this);
    }

    @Override
    protected void setSrc(String src) {
        try {
            Log.d(TAG, "audio src is: " + src);
            audio.setDataSource(src);
            audio.prepareAsync();
        }
        catch (Exception ex) {
            Log.d(TAG, "Exception: " + ex.getMessage());
        }
    }

    @Override
    protected void dispose() {
        super.dispose();
        this.audio = null;
    }

    @Override
    protected void dispatchCustomMessage(Message msg) {

    }
}
