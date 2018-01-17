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
### 观看直播 CLiteAV.startPlay

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

## TODO
* 播放器自适应
* 播放控制

## 相关文档 Documents
腾讯云小直播官方文档：[https://cloud.tencent.com/document/product/454](https://cloud.tencent.com/document/product/454)

开源项目参考：[https://github.com/EaseCloud/cordova-plugin-tencent-mlvb](https://github.com/EaseCloud/cordova-plugin-tencent-mlvb)
