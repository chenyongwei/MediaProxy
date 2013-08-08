package com.example.videoproxy;

/**
 * Created by Yongwei on 8/7/13.
 */
public class _Constant {

    public final static String JS_CALLER_PROTOCAL = "javascript:";

    public final static String GLOBAL_JS_OBJ = "android_native_video";

    public final static String MEDIA_EVENT_ENDED = JS_CALLER_PROTOCAL + GLOBAL_JS_OBJ + ".ended()";
    public final static String MEDIA_EVENT_LOADEDMETADATA = JS_CALLER_PROTOCAL + GLOBAL_JS_OBJ + ".loadedmetadata()";
    public final static String MEDIA_EVENT_TIMEUPDATE = JS_CALLER_PROTOCAL + GLOBAL_JS_OBJ + ".timeupdate(%f)";

}
