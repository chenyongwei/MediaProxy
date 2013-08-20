package com.example.videoproxy;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import static com.example.videoproxy._Enum.MediaAction;

/**
 * Created by Yongwei on 8/19/13.
 */
public class VideoHandler extends MediaHandler {

    private final static String TAG = MediaProxy.class.getSimpleName();

    private ViewGroup container;
    private VideoView video;

    public VideoHandler(ViewGroup containerView, IMediaEventCallback mediaEventCallback) {
        container = containerView;
        super.callback = mediaEventCallback;
    }

    @Override
    protected void dispatchCustomMessage(Message msg) {
        Bundle bundle = msg.getData();
        MediaAction ma = (MediaAction)bundle.getSerializable("type");
        switch (ma) {
            case translateVideoX:
                this.translateVideoX(bundle.getFloat("x"));
                break;
            case setVideoLayout:
                this.setVideoLayout(bundle.getInt("x"), bundle.getInt("y"), bundle.getInt("width"), bundle.getInt("height"));
                break;
            case showVideo:
                this.showVideo();
                break;
            case hideVideo:
                this.hideVideo();
                break;
            default:

                break;
        }
    }

    @Override
    protected void init() {
        VideoView videoView = new VideoView(container.getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.height = 100;
        layoutParams.width = 100;
        layoutParams.leftMargin = 0;
        layoutParams.topMargin = 0;
        videoView.setLayoutParams(layoutParams);

        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);

        container.addView(videoView);
        this.video = videoView;
    }

    @Override
    protected void setSrc(String src) {
        Uri uri = Uri.parse(src);
        video.setVideoURI(uri);
    }

    @Override
    protected void play(){
        super.play();
        this.showVideo();
    }

    @Override
    protected void stop() {
        this.video.stopPlayback();
        this.hideVideo();
    }

    @Override
    protected void dispose() {
        super.dispose();
        container.removeView(video);
        this.video = null;
    }

    private void showVideo() {
        video.setVisibility(View.VISIBLE);
    }

    private void hideVideo() {
        video.setVisibility(View.INVISIBLE);
    }

    private void translateVideoX(float x) {
        video.animate().translationX(x);
    }

    private void setVideoLayout(int x, int y, int width, int height) {
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

}
