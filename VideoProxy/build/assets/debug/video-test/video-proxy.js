function init() {
    document._video = window.android_native_video;

    window.android_native_video["loadedmetadata"] = function() {
        console.log("loadedmetadata");
    };
}
document.addEventListener("DOMContentLoaded", init, false);