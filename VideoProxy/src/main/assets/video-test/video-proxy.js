function init() {
    document._video = window.android_native_video;

    window.android_native_video["loadedmetadata"] = function() {
        console.log("loadedmetadata");
    };
    window.android_native_video["timeupdate"] = function(currentTime) {
        console.log("timeupdate: " + currentTime);
    };
}
document.addEventListener("DOMContentLoaded", init, false);