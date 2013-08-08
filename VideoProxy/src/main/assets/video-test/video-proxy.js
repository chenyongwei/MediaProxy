function init() {

    document._video = [];

    // events of native video player
    document._video["setSource"] = function(relativePath) {
        var a = document.createElement("a");
        a.href = relativePath;
        var absolutePath = a.href;
        window.android_native_video.setSrc(absolutePath);
    };
    document._video["play"] = function() {
        window.android_native_video.play();
    }
    document._video["pause"] = function() {
        window.android_native_video.pause();
    }
    document._video["stop"] = function() {
        window.android_native_video.stop();
    }
    document._video["setCurrentTime"] = function(currentTime) {
        window.android_native_video.setCurrentTime(currentTime);
    }
    document._video["getCurrentTime"] = function() {
        return window.android_native_video.getCurrentTime();
    }
    document._video["hide"] = function() {
        window.android_native_video.hide();
    }
    document._video["show"] = function() {
        window.android_native_video.show();
    }
    document._video["setLayout"] = function(x, y, width, height) {
        window.android_native_video.setLayout(x, y, width, height);
    }

    // callback from native video player
    window.android_native_video["loadedmetadata"] = function() {
        console.log("loadedmetadata");
        document._video["loadedmetadata"]();
    };
    window.android_native_video["timeupdate"] = function(currentTime) {
        console.log("timeupdate: " + currentTime);
        document._video["timeupdate"](currentTime);
    };
    window.android_native_video["ended"] = function() {
        console.log("ended");
        document._video["ended"](currentTime);
    };

    // for testing only
    document._video["forwardCurrentTime"] = function(forwardTime) {
        var currentTime = document._video.getCurrentTime();
        currentTime -= forwardTime;
        window.android_native_video.setCurrentTime(currentTime);
    };

    document._video["backwardCurrentTime"] = function(backwardTime) {
        var currentTime = document._video.getCurrentTime();
        currentTime += backwardTime;
        window.android_native_video.setCurrentTime(currentTime);
    };


}
document.addEventListener("DOMContentLoaded", init, false);