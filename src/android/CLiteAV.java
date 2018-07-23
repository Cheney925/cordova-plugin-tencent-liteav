package com.hoperun.cordova.tencent;
import com.hoperun.cordova.tencent.SuperPlayerGlobalConfig;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.*;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import com.tencent.rtmp.*;
import com.tencent.rtmp.ui.*;

/**
 * Created by ztl on 2018/1/17.
 */

public class CLiteAV extends CordovaPlugin implements ITXLivePlayListener,ITXLivePushListener{
    private static final String CAV = CLiteAV.class.getSimpleName();

    private Context context;
    private Activity activity;
    private CordovaInterface cordova;
    private CordovaWebView cordovaWebView;
    private ViewGroup rootView;
    private WebView webView;
    private WebSettings settings;
    private CallbackContext callbackContext;



    private TXCloudVideoView videoView = null;
    private TXLivePusher mLivePusher = null;
    private TXLivePlayer mLivePlayer = null;

    private TXVodPlayer mTxplayer = null;


    private TXLivePlayConfig mPlayConfig;
    private TXLivePushConfig mLivePushConfig;

    private static final int  CACHE_STRATEGY_FAST  = 1;  //极速
    private static final int  CACHE_STRATEGY_SMOOTH = 2;  //流畅
    private static final int  CACHE_STRATEGY_AUTO = 3;  //自动

    private static final float  CACHE_TIME_FAST = 1.0f;
    private static final float  CACHE_TIME_SMOOTH = 5.0f;

    private int              mCurrentRenderRotation;
    private int              mCurrentRenderMode;

    private int              driveHeight;
    private int              driveWidth;

    private double           density;

    private int              screenHeigh;
    private int              screenWidth;

    private int              mBeautyLevel = 5;
    private int              mWhiteningLevel = 3;
    private int              mRuddyLevel = 2;
    private int              mBeautyStyle = TXLiveConstants.BEAUTY_STYLE_SMOOTH;
    private TXVodPlayConfig mTPlayConfig;

    private boolean          mMainPublish = true;

    private String           netStatus;
    // 播放器
    private TXVodPlayer mVodPlayer;
    private TXVodPlayConfig mVodPlayConfig;

    private TXCloudVideoView mTXCloudVideoView;


    private String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.cordovaWebView = webView;
        this.cordova = cordova;
        this.activity = cordova.getActivity();
        this.context = this.activity.getApplicationContext();
        this.rootView = (ViewGroup) activity.findViewById(android.R.id.content);
        this.webView = (WebView) rootView.getChildAt(0);
        mCurrentRenderMode     = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
        mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_PORTRAIT;
        mPlayConfig = new TXLivePlayConfig();
        mTPlayConfig = new TXVodPlayConfig();
        mTPlayConfig.setCacheFolderPath(getInnerSDCardPath() + "/txcache");
        mTPlayConfig.setMaxCacheItems(5);
        WindowManager wm = (WindowManager) cordova.getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        driveWidth =  dm.widthPixels;
        driveHeight = dm.heightPixels;
        screenHeigh = (9*driveHeight/16);
        screenWidth = (16*driveHeight/9);
        initVodPlayer();
    }

    /**
     * 初始化点播播放器
     *
     *
     */
    private void initVodPlayer() {
        mVodPlayer = new TXVodPlayer(activity);

        SuperPlayerGlobalConfig config = SuperPlayerGlobalConfig.getInstance();

        mVodPlayConfig = new TXVodPlayConfig();
        mVodPlayConfig.setCacheFolderPath(Environment.getExternalStorageDirectory().getPath() + "/txcache");
        mVodPlayConfig.setMaxCacheItems(config.maxCacheItem);

        mVodPlayer.setConfig(mVodPlayConfig);
        mVodPlayer.setRenderMode(config.renderMode);
        mVodPlayer.enableHardwareDecode(config.enableHWAcceleration);
    }
    public String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }
    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArry of arguments for the plugin.
     * @param callbackContext The callback id used when calling back into JavaScript.
     * @return True if the action was valid, false if not.
     */
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        if (!hasPermisssion()) {
            requestPermissions(0);
        }

        this.callbackContext = callbackContext;

        if (action.equals("getVersion")) {
            return getVersion(callbackContext);
        } else if (action.equals("startPlay")) {
            final String option = args.getString(0);
            JSONObject jsonRsp = new JSONObject(option);
            final String url = jsonRsp.optString("url");
            final int playType = jsonRsp.optInt("playType");
            final int playMode = jsonRsp.optInt("playMode");
            return startPlay(url, playType, playMode,callbackContext);
        } else if (action.equals("stopPlay")) {
            return stopPlay(callbackContext);
        }else if(action.equals("setPlayMode")){
            final int playMode = args.getInt(0);
            return setPlayMode(playMode, callbackContext);
        }else if(action.equals("pause")){
            return pause();
        }else if(action.equals("resume")){
            return resume();
        }else if(action.equals("startLinkMic")){
            final String option =  args.getString(0);
            JSONObject jsonRsp = new JSONObject(option);
            final String linkMicUrl = jsonRsp.optString("url");
            return startLinkMic(linkMicUrl,callbackContext);
        }else if(action.equals("stopLinkMic")){
            return stopLinkMic(callbackContext);
        }else if(action.equals("getNetStatus")){
            if(netStatus!=null){
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,netStatus));
            }else {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR,"未获得网络状态"));
            }
        }
        callbackContext.error("Undefined action: " + action);
        return true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        String statusCode;
        switch (requestCode) {
            case 990:  // demoPush
                if (resultCode == 1) {
                    statusCode = "success";
                    callbackContext.success(statusCode);
                }
                break;
            default:
                break;
        }
    }

    private void prepareVideoView() {

        if (videoView != null) return;
        // 通过 layout 文件插入 videoView
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        videoView = (TXCloudVideoView) layoutInflater.inflate(_R("layout", "layout_video"), null);
        // 设置 webView 透明

        // 插入视图
        rootView.addView(videoView);
        videoView.setVisibility(View.VISIBLE);
        // 设置 webView 透明
        webView.setBackgroundColor(Color.TRANSPARENT);
        // 关闭 webView 的硬件加速（否则不能透明）
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        // 将 webView 提到顶层
        webView.bringToFront();
    }

    /**
     * 销毁 ideoView
     */
    private void destroyVideoView() {
        if (videoView == null) return;
        videoView.onDestroy();
        rootView.removeView(videoView);
        videoView = null;
        // 把 webView 变回白色
        webView.setBackgroundColor(Color.WHITE);
    }

    /**
     * 返回 MLVB SDK 版本字符串
     *
     * @param callbackContext
     * @return
     */
    private boolean getVersion(final CallbackContext callbackContext) {

        callbackContext.error("Cannot get rtmp sdk version.");
        return false;
    }

    /**
     * 开始播放，在垫底的 videoView 显示视频
     * 会在当前对象上下文注册一个 TXLivePlayer
     *
     * @param url             播放URL
     * @param playType        播放类型，参见 mlvb.js 相关的枚举定义
     * @param callbackContext
     * @return
     */
    private boolean startPlay(final String url, final int playType,final int playMode,final CallbackContext callbackContext) {
        if (mLivePlayer != null) {
            callbackContext.error("10004");
            return false;
        }
        if(playType==2){
            mVodPlayer.stopPlay(true);
            mVodPlayer.setAutoPlay(true);
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    prepareVideoView();
                    setPlayMode(playMode,callbackContext);
                    // 将视频绑定到 videoView
                    mVodPlayer.setPlayerView(videoView);
                    mVodPlayer.startPlay(url);
                    callbackContext.success("播放成功");
                    callbackContext.error("播放失败");
                }
            });
        }else{
           // 开始推流
           mLivePlayer = new TXLivePlayer(activity);
           // 设置自动配置
           setCacheStrategy(CACHE_STRATEGY_AUTO);
           mPlayConfig.setConnectRetryCount(50);
           mPlayConfig.setConnectRetryInterval(3);
           mLivePlayer.setConfig(mPlayConfig);

           // 设置图像渲染角度
           mLivePlayer.setRenderRotation(mCurrentRenderRotation);
           // 设置横屏、竖屏
           mLivePlayer.setRenderMode(mCurrentRenderMode);
           mLivePlayer.setPlayListener(this);
           // 准备 videoView，没有的话生成
           activity.runOnUiThread(new Runnable() {
               public void run() {
                   prepareVideoView();
                   setPlayMode(playMode,callbackContext);
                   // 将视频绑定到 videoView
                   mLivePlayer.setPlayerView(videoView);
                   mLivePlayer.startPlay(url, playType);
                   callbackContext.success("播放成功");
                   callbackContext.error("播放失败");
               }
           });
       }
        return true;
    }
    private boolean setPlayMode(final int playMode, final CallbackContext callbackContext){
        if (mLivePlayer == null) {
            callbackContext.error("切换失败,当前未在播放");
            return false;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(playMode == 0){
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                    screenWidth,
                                    driveHeight,
                                    Gravity.TOP
                            );
                            lp.setMargins(driveWidth-screenWidth, 0, driveWidth-screenWidth, 0);
                            videoView.setLayoutParams(lp);
                        }
                    });
                }else{
                    System.out.println(screenHeigh);
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                    driveHeight,
                                    screenHeigh,
                                    Gravity.TOP
                            );
                            lp.setMargins(0, 0, 0, 0);
                            videoView.setLayoutParams(lp);
                        }
                    });
                }
            }
        });
        callbackContext.success("切换成功");
        return true;
    }
    /**
     * 停止推流，并且注销 mLivePlay 对象
     *
     * @param callbackContext
     * @return
     */
    private boolean stopPlay(final CallbackContext callbackContext) {
        if (mLivePlayer == null) {
            callbackContext.error("10005");
            return false;
        }
        activity.runOnUiThread(new Runnable() {
            public void run() {
                // 停止播放
                mLivePlayer.stopPlay(false);
                // 销毁 videoView
                destroyVideoView();
                // 移除 pusher 引用
                mLivePlayer = null;
            }
        });
        callbackContext.success("停止播放视频");
        return true;
    }

    /**
     * check application's permissions
     */
    public boolean hasPermisssion() {
        for (String p : permissions) {
            if (!PermissionHelper.hasPermission(this, p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * We override this so that we can access the permissions variable, which no longer exists in
     * the parent class, since we can't initialize it reliably in the constructor!
     *
     * @param requestCode The code to get request action
     */
    public void requestPermissions(int requestCode) {
        PermissionHelper.requestPermissions(this, requestCode, permissions);
    }

    public String jsonEncode(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public int _R(String defType, String name) {
        return activity.getApplication().getResources().getIdentifier(
                name, defType, activity.getApplication().getPackageName());
    }
    public void setCacheStrategy(int nCacheStrategy) {
        switch (nCacheStrategy) {
            case CACHE_STRATEGY_FAST:
                mPlayConfig.setAutoAdjustCacheTime(true);
                mPlayConfig.setCacheTime(CACHE_TIME_FAST);
                mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_FAST);
                mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            case CACHE_STRATEGY_SMOOTH:
                mPlayConfig.setAutoAdjustCacheTime(false);
                mPlayConfig.setCacheTime(CACHE_TIME_SMOOTH);
                mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_SMOOTH);
                mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            case CACHE_STRATEGY_AUTO:
                mPlayConfig.setAutoAdjustCacheTime(true);
                mPlayConfig.setCacheTime(CACHE_TIME_SMOOTH);
                mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_SMOOTH);
                mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            default:
                break;
        }
    }
    public boolean pause(){
        mLivePlayer.pause();
        return true;
    }
    public boolean resume(){
        mLivePlayer.resume();
        return true;
    }
    // 获取状态栏高度
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = this.activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.activity.getResources().getDimensionPixelSize(resourceId);
        }
        System.out.println("获取状态栏高度=======>"+result);
        return result;
    }

    // 开启连麦
    private  boolean startLinkMic(final String url,final CallbackContext callbackContext) {
        mLivePusher = new TXLivePusher(this.context);
        mLivePusher.setBeautyFilter(mBeautyStyle, mBeautyLevel, mWhiteningLevel, mRuddyLevel);
        mLivePushConfig = new TXLivePushConfig();
        // 回声消除
        mLivePushConfig.enableAEC(true);
        // 开启纯音频推流
        mLivePushConfig.enablePureAudioPush(true);
        // 关闭高清摄像头
        mLivePushConfig.enableHighResolutionCaptureMode(false);

        mLivePushConfig.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
        mLivePushConfig.setConnectRetryCount(10);
        mLivePushConfig.setConnectRetryInterval(2);
        mLivePushConfig.setRtmpChannelType(TXLiveConstants.RTMP_CHANNEL_TYPE_PRIVATE);

        mLivePusher.setConfig(mLivePushConfig);

        mLivePusher.setVideoQuality(mMainPublish ? TXLiveConstants.VIDEO_QUALITY_LINKMIC_MAIN_PUBLISHER : TXLiveConstants.VIDEO_QUALITY_LINKMIC_SUB_PUBLISHER,false,false);
        mLivePusher.setPushListener(this);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLivePusher.startPusher(url.trim());
            }
        });
        callbackContext.success("开始连麦");
        return true;
    }
    // 关闭连麦
    private boolean stopLinkMic(final CallbackContext callbackContext) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLivePusher.stopCameraPreview(true);
                mLivePusher.stopScreenCapture();
                mLivePusher.setPushListener(null);
                mLivePusher.stopPusher();
            }
        });
        callbackContext.success("停止连麦");
        return true;
    }

    // 播放状态监听
    @Override
    public void onPlayEvent(int event, Bundle param) {
        String playEventLog = "receive event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION);
        Log.d(CAV, playEventLog);

        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            Log.d("AutoMonitor", "PlayFirstRender,cost=" +(System.currentTimeMillis()));
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT || event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            stopPlay(callbackContext);
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING){
        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
        } else if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
        }

        String jsStr = String.format("window.CLiteAV.onPlayEvent(%d, %s)", event, param.toString());
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instance.webView.loadUrl("javascript:" + jsStr);
            }
        });
    }

    // 推流状态监听
    @Override
    public void onPushEvent(int event, Bundle param) {
        String msg = param.getString(TXLiveConstants.EVT_DESCRIPTION);

        //错误还是要明确的报一下
        if (event < 0) {
            Toast.makeText(this.context, param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
            if(event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL){
                stopLinkMic(callbackContext);
            }
        }

        if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {
            stopLinkMic(callbackContext);
        }
        else if (event == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            Toast.makeText(this.context, param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
            mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
            mLivePusher.setConfig(mLivePushConfig);
        }
        else if (event == TXLiveConstants.PUSH_ERR_SCREEN_CAPTURE_UNSURPORT) {
            stopLinkMic(callbackContext);
        }
        else if (event == TXLiveConstants.PUSH_ERR_SCREEN_CAPTURE_START_FAILED) {
            stopLinkMic(callbackContext);
        } else if (event == TXLiveConstants.PUSH_EVT_CHANGE_RESOLUTION) {
            Log.d(CAV, "change resolution to " + param.getInt(TXLiveConstants.EVT_PARAM2) + ", bitrate to" + param.getInt(TXLiveConstants.EVT_PARAM1));
        } else if (event == TXLiveConstants.PUSH_EVT_CHANGE_BITRATE) {
            Log.d(CAV, "change bitrate to" + param.getInt(TXLiveConstants.EVT_PARAM1));
        }
    }

    // 网络状况监听
    @Override
    public void onNetStatus(Bundle status) {
        String str = getNetStatusString(status);
        Log.d(CAV, "Current status, CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE)+
                ", RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)+
                ", SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps"+
                ", FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS)+
                ", ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps"+
                ", VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps");

        String jsStr = String.format("window.CLiteAV.onNetStatusChange(%s)", status.toString());
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instance.webView.loadUrl("javascript:" + jsStr);
            }
        });
    }

    // 公用打印辅助函数
    protected String getNetStatusString(Bundle status) {
        String str = String.format("%-14s %-14s %-12s\n%-8s %-8s %-8s %-8s\n%-14s %-14s\n%-14s %-14s",
                "CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE),
                "RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT),
                "SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps",
                "JIT:"+status.getInt(TXLiveConstants.NET_STATUS_NET_JITTER),
                "FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS),
                "GOP:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_GOP)+"s",
                "ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps",
                "QUE:"+status.getInt(TXLiveConstants.NET_STATUS_CODEC_CACHE)
                        +"|"+status.getInt(TXLiveConstants.NET_STATUS_CACHE_SIZE)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_CACHE_SIZE)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_V_DEC_CACHE_SIZE)
                        +"|"+status.getInt(TXLiveConstants.NET_STATUS_AV_RECV_INTERVAL)
                        +","+status.getInt(TXLiveConstants.NET_STATUS_AV_PLAY_INTERVAL)
                        +","+status.getFloat(TXLiveConstants.NET_STATUS_AUDIO_PLAY_SPEED),
                "VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps",
                "SVR:"+status.getString(TXLiveConstants.NET_STATUS_SERVER_IP),
                "AUDIO:"+status.getString(TXLiveConstants.NET_STATUS_AUDIO_INFO));
        netStatus = "{\"CPU\":\""+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE)+
                "\",\"RES\":\""+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)+
                "\",\"SPD\":\""+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps"+
                "\",\"JIT\":\""+status.getInt(TXLiveConstants.NET_STATUS_NET_JITTER)+
                "\",\"FPS\":\""+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS)+
                "\",\"GOP\":\""+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_GOP)+"s"+
                "\",\"ARA\":\""+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps"+
                "\",\"QUE\":\""+status.getInt(TXLiveConstants.NET_STATUS_CODEC_CACHE)
                +"|"+status.getInt(TXLiveConstants.NET_STATUS_CACHE_SIZE)
                +","+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_CACHE_SIZE)
                +","+status.getInt(TXLiveConstants.NET_STATUS_V_DEC_CACHE_SIZE)
                +"|"+status.getInt(TXLiveConstants.NET_STATUS_AV_RECV_INTERVAL)
                +","+status.getInt(TXLiveConstants.NET_STATUS_AV_PLAY_INTERVAL)
                +","+status.getFloat(TXLiveConstants.NET_STATUS_AUDIO_PLAY_SPEED)+
                "\",\"VRA\":\""+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps"+
                "\",\"SVR\":\""+status.getString(TXLiveConstants.NET_STATUS_SERVER_IP)+
                "\",\"AUDIO\":\""+status.getString(TXLiveConstants.NET_STATUS_AUDIO_INFO)+"\"}";
        return str;
    }
}
