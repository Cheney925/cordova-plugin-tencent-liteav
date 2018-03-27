#import "CLiteAV.h"
#import "MainViewController.h"
#import "TXLiteAVSDK_Smart/TXLiveBase.h"
#import <CoreGraphics/CGGeometry.h>

@implementation MainViewController(CDVViewController)

- (void) viewDidLoad {
    [super viewDidLoad];
    // Tencent LiteAV SDK Version
    NSLog(@"[CLiteAV] Tencent LiteAV SDK Version: %@", [TXLiveBase getSDKVersionStr]);
    // log setting
    [TXLiveBase setConsoleEnabled:YES];
    [TXLiveBase setLogLevel:LOGLEVEL_DEBUG];
    
    self.webView.opaque = NO;
}
@end

@implementation CLiteAV

@synthesize videoView;
@synthesize livePlayer;
@synthesize livePusher;
@synthesize playerWidth;
@synthesize playerHeight;
@synthesize playerMode;
@synthesize statusBarHeight;
@synthesize netStatus;

// 准备放置视频的视图
- (void) prepareVideoView {
    if (self.videoView) return;
    
    [self updateVideoView];
    
    [self.webView.superview addSubview:self.videoView];
    
    [self.webView.superview bringSubviewToFront:self.webView];
    
    // 因webView在videoView上层，需要将webView背景透明
    [self.webView setBackgroundColor:[UIColor clearColor]];
}

// 销毁视频所在视图
- (void) destroyVideoView {
    if (!self.videoView) return;
    [self.videoView removeFromSuperview];
    self.videoView = nil;
}

// 开始播放
- (void) startPlay:(CDVInvokedUrlCommand*)command {
    NSDictionary* optionsDict = [command.arguments objectAtIndex:0];
    NSString* url = [optionsDict objectForKey:@"url"]; // 播放地址
    int type = [[optionsDict valueForKey:@"playType"] intValue]; // 播放类型
    TX_Enum_PlayType playType;
    switch (type) {
        case 0:
            playType = PLAY_TYPE_LIVE_RTMP;
            break;
        case 1:
            playType = PLAY_TYPE_LIVE_FLV;
            break;
        case 2:
            playType = PLAY_TYPE_VOD_FLV;
            break;
        case 3:
            playType = PLAY_TYPE_VOD_HLS;
            break;
        case 4:
            playType = PLAY_TYPE_VOD_MP4;
            break;
        case 5:
            playType = PLAY_TYPE_LIVE_RTMP_ACC;
            break;
        case 6:
            playType = PLAY_TYPE_LOCAL_VIDEO;
            break;
        default:
            playType = PLAY_TYPE_LIVE_RTMP;
            break;
    }
    
    
    // 设置播放器大小
    int mode = [[optionsDict valueForKey:@"playMode"] intValue]; // 播放模式
    [self updatePlayerMode:mode];

    // 播放视图准备
    [self prepareVideoView];

    // 播放器准备
    self.livePlayer = [[TXLivePlayer alloc] init];
    [self.livePlayer setupVideoWidget:CGRectMake(0, 0, 0, 0) containView:videoView insertIndex:0];

    [self.livePlayer setRenderRotation:HOME_ORIENTATION_DOWN];
    [self.livePlayer setRenderMode:RENDER_MODE_FILL_EDGE];

    CDVPluginResult *pluginResult;
    @try {
        [self.livePlayer startPlay:url type:playType];
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[CLiteAV] Played successful!"];
    } @catch (NSException *ex) {
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"[CLiteAV] Played Fail!"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
    // 绑定事件
    [self.livePlayer setDelegate:self];
}

// 暂停播放
- (void) pause:(CDVInvokedUrlCommand*)command {
    if (!self.livePlayer) return;
    
    CDVPluginResult *pluginResult;
    @try {
        [self.livePlayer pause];
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[CLiteAV] Player paused successful!"];
    } @catch (NSException *ex) {
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"[CLiteAV] Player paused fail!"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

// 恢复播放
- (void) resume:(CDVInvokedUrlCommand*)command {
    if (!self.livePlayer) return;
    
    CDVPluginResult *pluginResult;
    @try {
        [self.livePlayer resume];
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[CLiteAV] Player resumed successful!"];
    } @catch (NSException *ex) {
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"[CLiteAV] Player resumed fail!"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

// 存储播放模式，获取高宽
- (void) updatePlayerMode:(int)mode {
    CGRect screenBounds = [[UIScreen mainScreen] bounds];
    switch (mode) {
        case 0:
            self.playerWidth = screenBounds.size.width;
            self.playerHeight = screenBounds.size.height;
            break;
        case 1:
            self.playerWidth = screenBounds.size.width;
            self.playerHeight = self.playerWidth * 9/16;
            break;
    }
    self.playerMode = mode;
}

// 更新播放器高宽
- (void) updateVideoView {
    CGRect rectStatus = [[UIApplication sharedApplication] statusBarFrame];
    if (rectStatus.size.height != 0) {
        self.statusBarHeight = rectStatus.size.height;
    }
    
    if (self.playerMode == 0) {
        if (!self.videoView) {
            self.videoView = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
        } else {
            [self.videoView setFrame:[[UIScreen mainScreen] bounds]];
        }
    } else {
        if (!self.videoView) {
            self.videoView = [[UIView alloc] initWithFrame:CGRectMake(0.0, self.statusBarHeight, self.playerWidth, self.playerHeight)];
        } else {
            [self.videoView setFrame:CGRectMake(0.0, self.statusBarHeight, self.playerWidth, self.playerHeight)];
        }
    }
}

// 设置播放模式
- (void) setPlayMode:(CDVInvokedUrlCommand*)command {
    if (!self.livePlayer) return;
    
    int mode = [[command.arguments objectAtIndex:0] intValue];
    [self updatePlayerMode:mode];
    
    CDVPluginResult *pluginResult;
    @try {
        [self updateVideoView];
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[CLiteAV] Play mode setted successful!"];
    } @catch (NSException *ex) {
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"[CLiteAV] Play mode setted fail!"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

// 退出播放
- (void) stopPlay:(CDVInvokedUrlCommand*)command {
    if (!self.livePlayer) return;
    
    CDVPluginResult *pluginResult;
    @try {
        [self.livePlayer stopPlay];
        [self.livePlayer removeVideoWidget];
        
        if (livePusher) {
            [livePusher stopPush];
        };
        
        [self destroyVideoView];
        
        [self.webView setBackgroundColor:[UIColor whiteColor]];
        
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[CLiteAV] Player stopped successful!"];
    } @catch (NSException *ex) {
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"[CLiteAV] Player stopped fail!"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

// 开始连麦
- (void) startLinkMic:(CDVInvokedUrlCommand*)command {
    NSDictionary* optionsDict = [command.arguments objectAtIndex:0];
    NSString* url = [optionsDict objectForKey:@"url"]; // 播放地址
    
    TXLivePushConfig* _config = [[TXLivePushConfig alloc] init];
    livePusher = [[TXLivePush alloc] initWithConfig:_config];
    
    [livePusher setVideoQuality:VIDEO_QUALITY_LINKMIC_MAIN_PUBLISHER adjustBitrate:YES adjustResolution:NO];
    
    CDVPluginResult *pluginResult;
    @try {
        [livePusher startPush:url];
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[CLiteAV] Link mic successful!"];
    } @catch (NSException *ex) {
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"[CLiteAV] Link mic fail!"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

// 停止连麦
- (void) stopLinkMic:(CDVInvokedUrlCommand*)command {
    if (!livePusher) return;
    
    CDVPluginResult *pluginResult;
    @try {
        [livePusher stopPush];
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[CLiteAV] Link mic successful!"];
    } @catch (NSException *ex) {
        // 设置播放成功回调
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"[CLiteAV] Link mic fail!"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

// 监听播放事件
- (void) onPlayEvent:(int)EvtID withParam:(NSDictionary*)param {
    if (EvtID == PLAY_EVT_PLAY_LOADING) {
        NSLog(@"[CLiteAV] 加载中...");
    }
    if (EvtID == PLAY_EVT_RTMP_STREAM_BEGIN) {
        NSLog(@"[CLiteAV] 已经连接服务器，开始拉流");
    }
    if (EvtID == PLAY_EVT_PLAY_BEGIN) {
        NSLog(@"[CLiteAV] 开始播放");
    }
}

// 获取当前网络状况和视频信息
- (void) getNetStatus:(CDVInvokedUrlCommand*)command {
    CDVPluginResult *pluginResult;
    if (!self.netStatus) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:nil];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:self.netStatus];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) onNetStatus:(NSDictionary*) param {
    if (param && param != nil) {
        self.netStatus = param;
    }
}

@end
