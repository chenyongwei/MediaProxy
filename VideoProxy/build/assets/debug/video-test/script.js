// implemented based on http://www.w3schools.com/tags/ref_av_dom.asp
function init() {
	var detectionCss = "use-native-mediaplayer";
	var htmlClassName = document.getElementsByTagName("html")[0].className;
	var enableNativeMediaPlayer = htmlClassName.indexOf(detectionCss) >= 0;
	if (!enableNativeMediaPlayer) {
		return;
	}


	// create fake dom and events to delegate the events between "native media player" and "mediaelement"
	var video = document.createElement("div");
	var audio = document.createElement("div");
	var videoEvent = {};
	var audioEvent = {};

	window.android_native_video = window.android_native_video || {};
	window.android_native_audio = window.android_native_audio || {};
	/** HTML Audio/Video Events mapping **/
	createVideoEvent("abort", "canPlay", "canplaythrough", "durationchange" , "emptied", "ended", "error", 
		"loadeddata", "loadedmetadata", "loadstart", "pause", "play", "playing", "progress", "ratechange", 
		"seeked", "seeking", "stalled", "suspend", "timeupdate", "volumechange", "waiting");
	createAudioEvent("abort", "canPlay", "canplaythrough", "durationchange" , "emptied", "ended", "error", 
		"loadeddata", "loadedmetadata", "loadstart", "pause", "play", "playing", "progress", "ratechange", 
		"seeked", "seeking", "stalled", "suspend", "timeupdate", "volumechange", "waiting");


	/** HTML Audio/Video Methods mapping **/
	// accept the video control command and forward it to native media player
	video.addTextTrack = function(){ 
		throw new Error("addTextTrack has not been implemented now");
	};
	audio.addTextTrack = function(){ 
		throw new Error("addTextTrack has not been implemented now");
	};
	video.canPlayType = function(){ 
		throw new Error("canPlayType has not been implemented now");
	};
	audio.canPlayType = function(){ 
		throw new Error("canPlayType has not been implemented now");
	};
	video.load = function(){ 
		window.android_native_video.load();
	};
	audio.load = function(){ 
		window.android_native_audio.load();
	};
	video.play = function(){ 
		console.log("NativePlayer.play(), the url is :" + video.src);
		if (window.android_native_video.play) {
			// call native media play
			window.android_native_video.play();
		}
		else {
			// simulate the native media play callback
			window.android_native_video_event.play();
			// simulate to update the progress
			window.simulate_videoevent_timeupdate = setInterval(function(){
				var i = video.currentTime || 0;
				i += 0.2;
				window.android_native_video_event.timeupdate(i);
			}, 200);
		}
	};
	audio.play = function(){ 
		console.log("NativePlayer.play(), the url is :" + audio.src);
		if (window.android_native_audio.play) {
			// call native media play
			window.android_native_audio.play();
		}
		else {
			// simulate the native media play callback
			window.android_native_audio_event.play();
			// simulate to update the progress
			window.simulate_audioevent_timeupdate = setInterval(function(){
				var i = audio.currentTime || 0;
				i += 0.2;
				window.android_native_audio_event.timeupdate(i);
			}, 200);
		}
	};
	video.pause = function(){ 
		if (window.android_native_video.pause) {
			window.android_native_video.pause();
		}
		else {
			window.android_native_video_event.pause();
			clearInterval(window.simulate_videoevent_timeupdate);
		}
	};
	audio.pause = function(){ 
		if (window.android_native_audio.pause) {
			window.android_native_audio.pause();
		}
		else {
			window.android_native_audio_event.pause();
			clearInterval(window.simulate_audioevent_timeupdate);
		}
	};
	video.stop = function(){ 
		window.android_native_video.stop();
	};
	audio.stop = function(){ 
		window.android_native_audio.stop();
	};
	video.dispose = function(){
		if (window.android_native_video.dispose) {
			window.android_native_video.dispose();
		}
		else {
			console.log("NativePlayer.dispose()");
		}	
	};
	audio.dispose = function(){
		if (window.android_native_audio.dispose) {
			window.android_native_audio.dispose();
		}
		else {
			console.log("NativePlayer.dispose()");
		}	
	};
	// this method is only for mediaProxy, not a generic HTML5 audio/video method
	video.setVideoLayout = function(x, y, width, height){
		console.log("NativePlayer.setLayout");
		var clientWidth = document.body.clientWidth;
        var clientHeight = document.body.clientHeight;
        console.log("x:" + x + ", y: " + y + ", width: " + width + ", height:" + height + ", client width: " + clientWidth + ", client height: " + clientHeight);
		window.android_native_video.setVideoLayout && window.android_native_video.setVideoLayout(x, y, width, height, clientWidth, clientHeight);
	};
 	video.translateVideoX = function(x) {
 		console.log("NativePlayer.translateX");
 		var clientWidth = document.body.clientWidth;
 		window.android_native_video.translateVideoX && window.android_native_video.translateVideoX(x, clientWidth);	
 	}

	/** HTML Audio/Video Properties mapping **/
	/** these are all of properties for HTML5 Audio/Video tag, we want to manage them from following callbacks
	audioTracks				Returns an AudioTrackList object representing available audio tracks
	autoplay				Sets or returns if the audio/video should start playing as soon as it is loaded
	buffered				Returns a TimeRanges object representing the buffered parts of the audio/video
	controller				Returns the MediaController object representing the current media controller of the audio/video
	controls				Sets or returns if the audio/video should display controls (like play/pause etc.)
	crossOrigin				Sets or returns the CORS settings of the audio/video
	currentSrc				Returns the URL of the current audio/video
	currentTime				Sets or returns the current playback position in the audio/video (in seconds)
	defaultMuted			Sets or returns if the audio/video is muted by default
	defaultPlaybackRate		Sets or returns the default speed of the audio/video playback
	duration				Returns the length of the current audio/video (in seconds)
	ended					Returns if the playback of the audio/video has ended or not
	error					Returns a MediaError object representing the error state of the audio/video
	loop					Sets or returns if the audio/video should start over again when finished
	mediaGroup				Sets or returns a the group the audio/video belongs to (used to link multiple audio/video elements)
	muted					Sets or returns if the audio/video is muted or not
	networkState			Returns the current network state of the audio/video
	paused					Sets or returns if the audio/video is paused or not
	playbackRate			Sets or returns the speed of the audio/video playback
	played					Returns a TimeRanges object representing the played parts of the audio/video
	preload					Sets or returns if the audio/video should be loaded when the page loads
	readyState				Returns the current ready state of the audio/video
	seekable				Returns a TimeRanges object representing the seekable parts of the audio/video
	seeking					Returns if the user is currently seeking in the audio/video
	src						Sets or returns the current source of the audio/video element
	startDate				Returns a Date object representing the current time offset
	textTracks				Returns a TextTrackList object representing the available text tracks
	videoTracks				Returns a VideoTrackList object representing the available video tracks
	volume					Sets or returns the volume of the audio/video
	**/
	
	// init default values for some properties
	video.paused = true;
	video.played = false;
	video.buffered = false;
	video.ended = false;
	audio.paused = true;
	audio.played = false;
	audio.buffered = false;
	audio.ended = false;
	
	// watch property changes and notify to android native code
	// depend on Watch.JS https://github.com/melanke/Watch.JS
	watch(video, "src", function(){
		try{
			console.log("NativePlayer.setSrc(" + video.src + ")");
			video.setSrc(video.src);
		}
		catch (e){
			console.log(e.message);
		}
	});
	watch(audio, "src", function(){
		try{
			console.log("NativePlayer.setSrc(" + audio.src + ")");
			audio.setSrc(audio.src);
		}
		catch (e){
			console.log(e.message);
		}
	});
	video.setSrc = function(src) {
		if (window.android_native_video.setSrc) {
			console.log("window.android_native_video.setSrc");
			window.android_native_video.setSrc(src);
		}
		else {
			// simulate the events
			window.android_native_video_event.loadedmetadata(30);
			window.android_native_video_event.durationchange(30);
		}
	}
	audio.setSrc = function(src) {
		if (window.android_native_audio.setSrc) {
			window.android_native_audio.setSrc(src);	
		}
		// else {
		// 	// simulate the events
		// 	window.android_native_video_event.loadedmetadata(30);
		// 	window.android_native_video_event.durationchange(30);
		// }
	}

	var currentTimeChangedFromVideoPlayer = false;
	watch(video, "currentTime", function(){
		console.log("NativePlayer.currentTime = " + video.currentTime);
		if (!currentTimeChangedFromVideoPlayer) {
			video.setCurrentTime(video.currentTime);		
		}
		currentTimeChangedFromVideoPlayer = false;
	});
	var currentTimeChangedFromAudioPlayer = false;
	watch(audio, "currentTime", function(){
		console.log("NativePlayer.currentTime = " + audio.currentTime);
		if (!currentTimeChangedFromAudioPlayer) {
			audio.setCurrentTime(audio.currentTime);		
		}
		currentTimeChangedFromAudioPlayer = false;
	});
	video.setCurrentTime = function(currentTime) {
		console.log("NativePlayer.currentTime = " + currentTime);
		if (window.android_native_video.setCurrentTime) {
			window.android_native_video.setCurrentTime(currentTime);
		}
		else {
			currentTimeChangedFromVideoPlayer = true;
			video.currentTime = currentTime;
			window.android_native_video_event.seeked();
		}
	}
	audio.setCurrentTime = function(currentTime) {
		console.log("NativePlayer.currentTime = " + currentTime);
		if (window.android_native_audio.setCurrentTime) {
			window.android_native_audio.setCurrentTime(currentTime);
		}
		else {
			currentTimeChangedFromAudioPlayer = true;
			audio.currentTime = currentTime;
			window.android_native_audio_event.seeked();
		}
	}

	// register an android native object shadow to javascript, it should be overrided later from android native code.
	window.android_native_video_event = window.android_native_video_event || {};
	window.android_native_audio_event = window.android_native_audio_event || {};
	// accept the callback from native media player and update the fake video dom element to right state
	window.android_native_video_event.loadedmetadata = function(duration) {
		console.log("NativePlayer send loadedmetadata event, duration is : "+ duration);
		video.duration = duration;
		video.buffered = true;
	    video.dispatchEvent(videoEvent.loadedmetadata);
	};
	window.android_native_audio_event.loadedmetadata = function(duration) {
		console.log("NativePlayer send loadedmetadata event, duration is : "+ duration);
		audio.duration = duration;
		audio.buffered = true;
	    audio.dispatchEvent(audioEvent.loadedmetadata);
	};
	window.android_native_video_event.durationchange = function(duration) {
		console.log("NativePlayer send durationchange event, duration is :" + duration);
		//currentTimeChangedFromVideoPlayer = true;
		video.duration = duration;
	    video.dispatchEvent(videoEvent.durationchange);
	};
	window.android_native_audio_event.durationchange = function(duration) {
		console.log("NativePlayer send durationchange event, duration is :" + duration);
		//currentTimeChangedFromVideoPlayer = true;
		audio.duration = duration;
	    audio.dispatchEvent(audioEvent.durationchange);
	};
	window.android_native_video_event.timeupdate = function(currentTime) {
		console.log("NativePlayer send timeupdate event, currentTime is : "+ currentTime);
		currentTimeChangedFromVideoPlayer = true;
		video.currentTime = currentTime;
		video.dispatchEvent(videoEvent.timeupdate);
	};
	window.android_native_audio_event.timeupdate = function(currentTime) {
		console.log("NativePlayer send timeupdate event, currentTime is : "+ currentTime);
		currentTimeChangedFromAudioPlayer = true;
		audio.currentTime = currentTime;
		audio.dispatchEvent(audioEvent.timeupdate);
	};
	window.android_native_video_event.play = function() {
		video.paused = false;
		console.log("NativePlayer send play event");
		video.dispatchEvent(videoEvent.play);
	};
	window.android_native_audio_event.play = function() {
		audio.paused = false;
		console.log("NativePlayer send play event");
		audio.dispatchEvent(audioEvent.play);
	};
	window.android_native_video_event.playing = function() {
		console.log("NativePlayer send playing event");
		video.paused = false;
		video.dispatchEvent(videoEvent.playing);
	};
	window.android_native_audio_event.playing = function() {
		console.log("NativePlayer send playing event");
		audio.paused = false;
		audio.dispatchEvent(audioEvent.playing);
	};
	window.android_native_video_event.pause = function() {
		console.log("NativePlayer send pause event");
		video.paused = true;
		video.dispatchEvent(videoEvent.pause);
	};
	window.android_native_audio_event.pause = function() {
		console.log("NativePlayer send pause event");
		audio.paused = true;
		audio.dispatchEvent(audioEvent.pause);
	};
	window.android_native_video_event.ended = function() {
		console.log("NativePlayer send ended event");
	    video.ended = true;
	    video.dispatchEvent(videoEvent.ended);
	};
	window.android_native_audio_event.ended = function() {
		console.log("NativePlayer send ended event");
	    audio.ended = true;
	    audio.dispatchEvent(audioEvent.ended);
	};
	window.android_native_video_event.seeked = function() {
		console.log("NativePlayer send seeked event");
		video.dispatchEvent(videoEvent.seeked);
	};
	window.android_native_audio_event.seeked = function() {
		console.log("NativePlayer send seeked event");
		audio.dispatchEvent(audioEvent.seeked);
	};

	function createVideoEvent(event1name, event2name /*event3name, .....*/) {
		for (var argIndex in arguments) {
			var eventname = arguments[argIndex];
			videoEvent[eventname] = document.createEvent("Event");
			videoEvent[eventname].initEvent(eventname, false, true);
		}
	};
	function createAudioEvent(event1name, event2name /*event3name, .....*/) {
		for (var argIndex in arguments) {
			var eventname = arguments[argIndex];
			audioEvent[eventname] = document.createEvent("Event");
			audioEvent[eventname].initEvent(eventname, false, true);
		}
	};

	// cache to global objects
	document._video = video;
	document._audio = audio;
	document._videoEvent = videoEvent;
	document._audioEvent = audioEvent;

}
document.addEventListener("DOMContentLoaded", init, false);