package com.hoperun.cordova.tencent;

import com.tencent.rtmp.TXLiveConstants;

/**
 * Created by yuejiaoli on 2018/7/4.
 * 超级播放器全局配置类
 */

public class SuperPlayerGlobalConfig {

    private static SuperPlayerGlobalConfig sInstance;

    private SuperPlayerGlobalConfig() {
    }

    public static SuperPlayerGlobalConfig getInstance() {
        if (sInstance == null) {
            sInstance = new SuperPlayerGlobalConfig();
        }
        return sInstance;
    }
    /**
     * 是否启用悬浮窗
     */
    public boolean enableFloatWindow = true;
    /**
     * 悬浮窗位置
     */
    public TXRect floatViewRect;
    /**
     * 是否开启硬件加速
     */
    public boolean enableHWAcceleration = true;
    /**
     * 默认播放填充模式
     */
    public int renderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
    /**
     * 播放器最大缓存个数
     */
    public int maxCacheItem;
    /**
     * 是否开启直播时移
     */
//    public boolean enableTimeShift;

    public final static class TXRect {
        public int x;
        public int y;
        public int width;
        public int height;
    }
}
