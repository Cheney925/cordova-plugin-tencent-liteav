var exec = require('cordova/exec');

// 视频流播放类型 
exports.PLAY_URL_TYPE = {
  PLAY_TYPE_LIVE_RTMP:     0, // 传入的URL为RTMP直播地址
  PLAY_TYPE_LIVE_FLV:      1, // 传入的URL为FLV直播地址
  PLAY_TYPE_VOD_FLV:       2, // 传入的URL为RTMP点播地址
  PLAY_TYPE_VOD_HLS:       3, // 传入的URL为HLS(m3u8)点播地址
  PLAY_TYPE_VOD_MP4:       4, // 传入的URL为MP4点播地址
  PLAY_TYPE_LIVE_RTMP_ACC: 5, // 低延迟连麦链路直播地址（仅适合于连麦场景）
  PLAY_TYPE_LOCAL_VIDEO:   6  // 手机本地视频文件
}

exports.startPlay = function(url, playType, success, error) {
  cordova.exec(success, error, 'CLiteAV', 'startPlay', [url, playType]);
};
