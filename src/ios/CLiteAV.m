#import "CLiteAV.h"
#import "MainViewController.h"
#import "TXLiteAVSDK_Smart/TXLiveBase.h"

@implementation MainViewController(CDVViewController)
- (void) viewDidLoad {
    [super viewDidLoad];
    // Tencent LiteAV SDK Version
    NSLog(@"[CLiteAV] Tencent LiteAV SDK Version: %@", [TXLiveBase getSDKVersionStr]);
    // log setting
    [TXLiveBase setConsoleEnabled:YES];
    [TXLiveBase setLogLevel:LOGLEVEL_DEBUG];
}
@end

@implementation CLiteAV

@synthesize videoView;
@synthesize livePlayer;

- (void) prepareVideoView {
    if (self.videoView) return;
    self.videoView = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    [self.webView.superview addSubview:self.videoView];
    [self.webView.superview bringSubviewToFront:self.videoView];
}

- (void) destroyVideoView {
    if (!self.videoView) return;
    [self.videoView removeFromSuperview];
    self.videoView = nil;
}

- (void) startPlay:(CDVInvokedUrlCommand*)command {
    if (self.livePlayer) return;
    
    NSString* url = [command.arguments objectAtIndex:0];
    NSInteger type = (NSInteger)[command.arguments objectAtIndex:1];
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
    [self.livePlayer startPlay:url type:playType];
}

@end
