# cordova-plugin-tencent-LiteAV
腾讯小直播LiteAV SDK Cordova插件

## 安装 Installation
### 方法一
`cordova plugin add https://github.com/Cheney925/cordova-plugin-tencent-LiteAV.git`
### 方法二
下载git源码包，手动放入plugins目录
### 方法三
在config.xml中添加

`<plugin name="cordova-plugin-tencent-LiteAV" spec="https://github.com/Cheney925/cordova-plugin-tencent-LiteAV.git" />`

然后运行

`cordova prepare`

## 使用 Usage
### 直播/播放视频 CLiteAV.startPlay()

`CLiteAV.startPlay(url, playType, successCallback, errorCallback)`

* `url` 视频流地址
* `playType` 视频流类型，参考`CLiteAV.PLAY_TYPE`

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
* `successCallback` 播放成功回调
* `errorCallback` 播放失败回调

> 调用startPlay方法时，需要将前端网页的背景颜色设置为透明，并保证没有多余的元素，否则会遮挡住播放器。这是因为WebView层在原生播放器层的上层，如果WebView有背景色，就会挡住下面的层。这么做也是为了方便前端可以在上层添加一些播放控件，如播放、暂停、全屏、进度条。可以参考Ionic Demo：[https://github.com/Cheney925/ionic-for-cordova-plugin-tencent-LiteAV](https://github.com/Cheney925/ionic-for-cordova-plugin-tencent-LiteAV)

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


## Demo
基于Ionic 3的Demo：[https://github.com/Cheney925/ionic-for-cordova-plugin-tencent-LiteAV](https://github.com/Cheney925/ionic-for-cordova-plugin-tencent-LiteAV)

## TODO
* 播放器自适应
* 播放控制

## 相关文档 Documents
腾讯云小直播官方文档：[https://cloud.tencent.com/document/product/454](https://cloud.tencent.com/document/product/454)

开源项目参考：[https://github.com/EaseCloud/cordova-plugin-tencent-mlvb](https://github.com/EaseCloud/cordova-plugin-tencent-mlvb)
