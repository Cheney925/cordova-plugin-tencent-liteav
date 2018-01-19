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
};

// 播放模式 
exports.PLAY_MODE = {
  LANDSCAPE:     0, // 横屏
  PORTRAIT:      1  // 竖屏
};

/*
 * 开始播放
 * @params options  {Object}  选项
 *           |- url        {String}  播放地址
 *           |- playType   {Number}  播放类型，参考PLAY_TYPE
 *           |- width      {Number}  播放器宽度
 *           |- height     {Number}  播放器高度
 */
exports.startPlay = function(options, success, error) {
  cordova.exec(success, error, 'CLiteAV', 'startPlay', [options]);
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
