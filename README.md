# cordova-plugin-tencent-liteav
腾讯小直播LiteAV SDK Cordova插件

![npm](https://img.shields.io/npm/v/cordova-plugin-tencent-liteav.svg)
![taobaonpm](https://npm.taobao.org/badge/v/cordova-plugin-tencent-liteav.svg)

## 安装 Installation
`cordova plugin add cordova-plugin-tencent-liteav`

## 使用 Usage
### 直播/播放视频 CLiteAV.startPlay()

`CLiteAV.startPlay(options, successCallback, errorCallback)`

* `options` 播放参数，包含url, playType, playMode
* `options.url` 视频流播放地址
* `options.playType` 视频流类型，参考`CLiteAV.PLAY_TYPE`

	```
	PLAY_TYPE = {
	  LIVE_RTMP:     0, // 传入的URL为RTMP直播地址
	  LIVE_FLV:      1, // 传入的URL为FLV直播地址
	  VOD_FLV:       2, // 传入的URL为RTMP点播地址
	  VOD_HLS:       3, // 传入的URL为HLS(m3u8)点播地址
	  VOD_MP4:       4, // 传入的URL为MP4点播地址
	  LIVE_RTMP_ACC: 5, // 低延迟连麦链路直播地址（仅适合于连麦场景）
	  LOCAL_VIDEO:   6  // 手机本地视频文件
	}
	```
* `options.playMode` 播放模式，0为横屏，1为竖屏
* `successCallback` 播放成功回调
* `errorCallback` 播放失败回调

> 调用startPlay方法时，需要将前端网页的背景颜色设置为透明，并保证没有多余的元素，否则会遮挡住播放器。这是因为WebView层在原生播放器层的上层，如果WebView有背景色，就会挡住下面的层。这么做也是为了方便前端可以在上层添加一些播放控件，如播放、暂停、全屏、进度条。可以参考Ionic Demo：[https://github.com/Cheney925/ionic-for-cordova-plugin-tencent-liteav](https://github.com/Cheney925/ionic-for-cordova-plugin-tencent-liteav)

> 参照以下设置：
```
body.video-play {
	background-color: transparent !important;
	ion-app {
		background-color: transparent !important;
		.app-root {
			opacity: 0;
		}
		.ion-page.show-page ~ .nav-decor {
			background-color: transparent !important;
		}
		.content {
			background-color: transparent !important;
		}
	}
}
```

### 停止播放 CLiteAV.stopPlay()

停止播放，退出播放器，播放器会被销毁。

`CLiteAV.stopPlay(successCallback, errorCallback)`

* `successCallback` 成功回调
* `errorCallback` 失败回调

### 暂停播放 CLiteAV.pause()

暂停播放，播放器仍然存在，如需销毁，调用stopPlay()。

`CLiteAV.pause(successCallback, errorCallback)`

* `successCallback` 成功回调
* `errorCallback` 失败回调

### 恢复播放 CLiteAV.resume()

`CLiteAV.resume(successCallback, errorCallback)`

* `successCallback` 成功回调
* `errorCallback` 失败回调

### 设置播放模式 CLiteAV.setPlayMode()

`CLiteAV.setPlayMode(mode, successCallback, errorCallback)`

* `mode` 播放模式，可选值： `portrait`、`landscape`，前端可通过监听设备横竖屏模式变化时调用这个方法，也可以添加一个控制按钮进行调用
* `successCallback` 成功回调 
* `errorCallback` 失败回调

### 开启连麦 CLiteAV.startLinkMic()
`CLiteAV.startLinkMic(options, successCallback, errorCallback)`

* `options` options中必须包含连麦推流的地址url参数，暂无其他参数
* `successCallback` 成功回调 
* `errorCallback` 失败回调

### 关闭连麦 CLiteAV.stopLinkMic()
`CLiteAV.stopLinkMic(successCallback, errorCallback)`

* `successCallback` 成功回调 
* `errorCallback` 失败回调

### 获取视频信息 CLiteAV.getNetStatus()
`CLiteAV.getNetStatus(successCallback, errorCallback)`

* `successCallback` 成功回调 
* `errorCallback` 失败回调

`successCallback`有回调参数`netStatus`，包含了播放的视频状态信息

**示例如下：**

```
{
	"isTrusted": false,
	"AV_PLAY_INTERVAL": -22,
	"CPU_USAGE_DEVICE": 0.026000000536441803,
	"SERVER_IP": "112.65.220.64:1935", // 连接的服务器IP
	"NET_JITTER": 0,
	"CODEC_CACHE": 1621,
	"AUDIO_INFO": "1|48000,2|48000,1",
	"AUDIO_BITRATE": 32, // 当前流媒体的音频码率，单位 kbps
	"NET_SPEED": 318, // 当前网络数据接收速度
	"VIDEO_HEIGHT": 288, // 视频分辨率 - 高
	"VIDEO_BITRATE": 286, // 当前流媒体的视频码率，单位 kbps
	"V_DEC_CACHE_SIZE": 4,
	"CPU_USAGE": 0.10025062412023544, // 当前瞬时CPU使用率
	"CACHE_SIZE": 1680, // 缓冲区（jitterbuffer）大小，缓冲区当前长度为 0，说明离卡顿就不远了
	"VIDEO_WIDTH": 480, // 视频分辨率 - 宽
	"VIDEO_GOP": 1,
	"AV_RECV_INTERVAL": -41,
	"VIDEO_CACHE_SIZE": 42,
	"VIDEO_FPS": 25, 、// 当前流媒体的视频帧率
	"AUDIO_PLAY_SPEED": 5
}
```

### 监听视频信息变化 CLiteAV.onNetStatusChange
`document.addEventListener('CLiteAV.onNetStatusChange', function(netStatus) {})`

* `netStatus`与`CLiteAV.getNetStatus`获取的一致，不同的是通过事件监听可以自动拿到最新的状态信息

### 播放事件 CLiteAV.onPlayEvent
`document.addEventListener('CLiteAV.onPlayEvent', function(data) {})`

* `data.eventID` 播放事件ID，参考 [文档](https://cloud.tencent.com/document/product/454/7880)
* `data.params` 附加数据

**常用播放事件**

事件ID|数值|含义说明
---|---|---
`PLAY_EVT_CONNECT_SUCC` |2001 |已经连接服务器
`PLAY_EVT_RTMP_STREAM_BEGIN` |2002 |已经连接服务器，开始拉流（仅播放RTMP地址时会抛送）
`PLAY_EVT_RCV_FIRST_I_FRAME` |2003 |已经网络接收到首个可渲染的视频数据包(IDR)服务器
`PLAY_EVT_PLAY_BEGIN` |2004 |视频播放开始，如果有转菊花什么的这个时候该停了
`PLAY_EVT_PLAY_PROGRESS` |2005 |视频播放进度，会通知当前播放进度、加载进度 和总体时长
`PLAY_EVT_PLAY_END ` |2006 |视频播放结束
`PLAY_EVT_PLAY_LOADING` |2007 |视频播放loading，如果能够恢复，之后会有BEGIN事件
`PLAY_EVT_GET_MESSAGE` |2012 |用于接收夹在音视频流中的消息
`PLAY_ERR_NET_DISCONNECT` |-2301 |网络断连,且经多次重连亦不能恢复,更多重试请自行重启播放

## Change Log
* **v0.3.1** 解决cordova-android@7.0.0以上版本lib目录问题
* **v0.3.0** 修复android下播放画面不正常的问题
* **v0.2.2** 删除依赖插件cordova-plugin-compat
* **v0.2.0** 添加视频信息监听`CLiteAV.onNetStatusChange`和播放事件`CLiteAV.onPlayEvent`

## Demo
基于Ionic 3的Demo：[https://github.com/Cheney925/ionic-for-cordova-plugin-tencent-liteav](https://github.com/Cheney925/ionic-for-cordova-plugin-tencent-liteav)

## 相关文档 Documents
腾讯云小直播官方文档：[https://cloud.tencent.com/document/product/454](https://cloud.tencent.com/document/product/454)
