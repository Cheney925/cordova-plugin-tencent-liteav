var exec = require('cordova/exec');
var channel = require('cordova/channel');

function CLiteAV() {
  // 视频流播放类型 
  this.PLAY_TYPE = {
    LIVE_RTMP:     0, // 传入的URL为RTMP直播地址
    LIVE_FLV:      1, // 传入的URL为FLV直播地址
    VOD_FLV:       2, // 传入的URL为RTMP点播地址
    VOD_HLS:       3, // 传入的URL为HLS(m3u8)点播地址
    VOD_MP4:       4, // 传入的URL为MP4点播地址
    LIVE_RTMP_ACC: 5, // 低延迟连麦链路直播地址（仅适合于连麦场景）
    LOCAL_VIDEO:   6  // 手机本地视频文件
  };

  // 播放模式 
  this.PLAY_MODE = {
    LANDSCAPE:     0, // 横屏
    PORTRAIT:      1  // 竖屏
  };
}

/*
 * 开始播放
 * @params options  {Object}  选项
 *           |- url        {String}  播放地址
 *           |- playType   {Number}  播放类型，参考PLAY_TYPE
 *           |- playMode   {Number}  播放模式，1为竖屏，0为横屏
 */
CLiteAV.prototype.startPlay = function(options, success, error) {
  cordova.exec(success, error, 'CLiteAV', 'startPlay', [options]);
};

// 停止播放
CLiteAV.prototype.stopPlay = function(success, error) {
  cordova.exec(success, error, 'CLiteAV', 'stopPlay', []);
};

// 暂停播放
CLiteAV.prototype.pause = function(success, error) {
  cordova.exec(success, error, 'CLiteAV', 'pause', []);
};

// 恢复播放
CLiteAV.prototype.resume = function(success, error) {
  cordova.exec(success, error, 'CLiteAV', 'resume', []);
};

// 播放模式 portrait/landscape
CLiteAV.prototype.setPlayMode = function(mode, success, error) {
  cordova.exec(success, error, 'CLiteAV', 'setPlayMode', [mode]);
};

// 开启连麦
CLiteAV.prototype.startLinkMic = function(options, success, error) {
  cordova.exec(success, error, 'CLiteAV', 'startLinkMic', [options]);
};

// 关闭连麦
CLiteAV.prototype.stopLinkMic = function(success, error) {
  cordova.exec(success, error, 'CLiteAV', 'stopLinkMic', []);
};

// 获取当前网络状况和视频信息
CLiteAV.prototype.getNetStatus = function(success, error) {
  cordova.exec(success, error, 'CLiteAV', 'getNetStatus', []);
};

// 监听网络状况变化
CLiteAV.prototype.onNetStatusChange = function(data) {
  cordova.fireDocumentEvent('CLiteAV.onNetStatusChange', data);
};

// 监听播放事件变化
CLiteAV.prototype.onPlayEvent = function(eventID, params) {
  cordova.fireDocumentEvent('CLiteAV.onPlayEvent', {
    eventID: eventID,
    params: params
  });
};

if (!window.CLiteAV) {
  window.CLiteAV = new CLiteAV();
}

module.exports = new CLiteAV();
