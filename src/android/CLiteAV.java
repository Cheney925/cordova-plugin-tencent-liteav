package com.hoperun.cordova.tencent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

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

public class CLiteAV extends CordovaPlugin {

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

    private TXLivePlayConfig mPlayConfig;

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
        WindowManager wm = (WindowManager) cordova.getActivity()
                .getSystemService(Context.WINDOW_SERVICE);

        driveWidth = wm.getDefaultDisplay().getWidth();
        driveHeight = wm.getDefaultDisplay().getHeight();
        screenHeigh = (9*driveWidth/16);
        screenWidth = (16*driveWidth/9);
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
            final int width = jsonRsp.optInt("width");
            final int height = jsonRsp.optInt("height");
            return startPlay(url, playType, width, height,callbackContext);
        } else if (action.equals("stopPlay")) {
            return stopPlay(callbackContext);
        }else if(action.equals("setPlayMode")){
            final int playMode = args.getInt(0);
            return setPlayMode(playMode, callbackContext);
        }else if(action.equals("pause")){
            return pause();
        }else if(action.equals("resume")){
            return resume();
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

    private void prepareVideoView(final int width,final int heigh) {

        if (videoView != null) return;
        // 通过 layout 文件插入 videoView
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        videoView = (TXCloudVideoView) layoutInflater.inflate(_R("layout", "layout_video"), null);
        // 设置 webView 透明
        System.out.println(screenHeigh);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                driveWidth,
                screenHeigh,
                Gravity.TOP
        );
        lp.setMargins(0,0,0,0);
        videoView.setLayoutParams(lp);
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
        int[] sdkver = TXLivePusher.getSDKVersion();
        if (sdkver != null && sdkver.length > 0) {
            String ver = "" + sdkver[0];
            for (int i = 1; i < sdkver.length; ++i) {
                ver += "." + sdkver[i];
            }
            callbackContext.success(ver);
            return true;
        }
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
    private boolean startPlay(final String url, final int playType,final int width,final int heigh, final CallbackContext callbackContext) {
        if (mLivePlayer != null) {
            callbackContext.error("10004");
            return false;
        }
        // 准备 videoView，没有的话生成
        activity.runOnUiThread(new Runnable() {
            public void run() {
                prepareVideoView(width,heigh);
                // 开始推流
                mLivePlayer = new TXLivePlayer(activity);
                // 设置自动配置
                setCacheStrategy(CACHE_STRATEGY_AUTO);
                mLivePlayer.setConfig(mPlayConfig);

                // 设置图像渲染角度
                mLivePlayer.setRenderRotation(mCurrentRenderRotation);
                // 设置横屏、竖屏
                mLivePlayer.setRenderMode(mCurrentRenderMode);

                // 将视频绑定到 videoView
                mLivePlayer.setPlayerView(videoView);
                mLivePlayer.startPlay(url, playType);
                callbackContext.success("播放成功");
                callbackContext.error("播放失败");
            }
        });
        return true;
    }
    private boolean setPlayMode(final int playMode, final CallbackContext callbackContext){
        if (mLivePlayer == null) {
            callbackContext.error("切换失败");
            return false;
        }
//        mCurrentRenderMode = playMode;
//        mLivePlayer.setRenderMode(mCurrentRenderMode);
        if(playMode == 0){
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                            screenWidth,
                            driveWidth,
                            Gravity.TOP
                    );
                    lp.setMargins(driveHeight-screenWidth, 0, driveHeight-screenWidth, 0);
                    videoView.setLayoutParams(lp);
                }
            });
        }else{
            System.out.println(screenHeigh);
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                            driveWidth,
                            screenHeigh,
                            Gravity.TOP
                    );
                    lp.setMargins(0, 0, 0, 0);
                    videoView.setLayoutParams(lp);
                }
            });
        }
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
                mLivePlayer.stopPlay(true);
                // 销毁 videoView
                destroyVideoView();
                // 移除 pusher 引用
                mLivePlayer = null;
            }
        });
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
}
