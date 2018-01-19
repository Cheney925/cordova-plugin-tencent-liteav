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

// 准备放置视频的视图
- (void) prepareVideoView {
    if (self.videoView) return;
    
    self.videoView = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    
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
    if (self.livePlayer) return;
    
    NSDictionary *optionsDict = [command.arguments objectAtIndex:0];
    NSString *url = [optionsDict objectForKey:@"url"];
    int type = [[optionsDict valueForKey:@"playType"] intValue];
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

    [self prepareVideoView];

    self.livePlayer = [[TXLivePlayer alloc] init];
    [self.livePlayer setupVideoWidget:CGRectMake(0, 0, 0, 0) containView:videoView insertIndex:0];

    [self.livePlayer setRenderRotation:HOME_ORIENTATION_DOWN];
    [self.livePlayer setRenderMode:RENDER_MODE_FILL_EDGE];

    [self.livePlayer startPlay:url type:playType];
}

// 暂停播放
- (void) stopPlay:(CDVInvokedUrlCommand*)command {
    if (!self.livePlayer) return;
    [self.livePlayer stopPlay];
}

// 设置播放模式
- (void) setPlayMode:(CDVInvokedUrlCommand*)command {
    if (!self.livePlayer) return;
    
    NSInteger mode = (NSInteger)[command.arguments objectAtIndex:0];
    switch (mode) {
        case 0:
            [self.livePlayer setRenderRotation:HOME_ORIENTATION_RIGHT];
            break;
        case 1:
            [self.livePlayer setRenderRotation:HOME_ORIENTATION_DOWN];
            break;
        default:
            [self.livePlayer setRenderRotation:HOME_ORIENTATION_DOWN];
            break;
    }
}

// 退出播放
- (void) exit:(CDVInvokedUrlCommand*)command {
    if (!self.livePlayer) return;
    [self.livePlayer removeVideoWidget];
    [self destroyVideoView];
    
    [self.webView setBackgroundColor:[UIColor whiteColor]];
}

@end
