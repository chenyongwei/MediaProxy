package com.example.videoproxy;

/**
 * Created by Yongwei on 8/7/13.
 */
public class MediaPlayerExt {


    public static interface OnProgressUpdate {
        void OnProgressUpdate(android.media.MediaPlayer mediaPlayer, float currentTime);
    }

}
