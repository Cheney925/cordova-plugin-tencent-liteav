var exec = require('cordova/exec');

// 视频流播放类型 
exports.PLAY_TYPE = {
  LIVE_RTMP:     0, // 传入的URL为RTMP直播地址
  LIVE_FLV:      1, // 传入的URL为FLV直播地址
  VOD_FLV:       2, // 传入的URL为RTMP点播地址
  VOD_HLS:       3, // 传入的URL为HLS(m3u8)点播地址
  VOD_MP4:       4, // 传入的URL为MP4点播地址
  LIVE_RTMP_ACC: 5, // 低延迟连麦链路直播地址（仅适合于连麦场景）
  LOCAL_VIDEO:   6  // 手机本地视频文件
}

// 开始播放
exports.startPlay = function(url, playType, success, error) {
  cordova.exec(success, error, 'CLiteAV', 'startPlay', [url, playType]);
};

// 停止播放
exports.stopPlay = function(success, error) {
  cordova.exec(success, error, 'CLiteAV', 'stopPlay', []);
};

// 播放模式 portrait/landscape
exports.setPlayMode = function(mode, success, error) {
  cordova.exec(success, error, 'CLiteAV', 'setPlayMode', [mode]);
};

// 退出播放
exports.exit = function(success, error) {
  cordova.exec(success, error, 'CLiteAV', 'exit', []);
};
