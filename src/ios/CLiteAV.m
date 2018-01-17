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
    [self.webView.superview bringSubviewToFront:self.webView];
}

- (void) destroyVideoView {
    if (!self.videoView) return;
    [self.videoView removeFromSuperview];
    self.videoView = nil;
    // 把 webView 变回白色
    // [self.webView setBackgroundColor:[UIColor whiteColor]];
}

- (void) startPlay:(CDVInvokedUrlCommand*)command {
    if (self.livePlayer) return;
    
    NSString* url = [command.arguments objectAtIndex:0];
    NSInteger playType = (NSInteger)[command.arguments objectAtIndex:1];

    [self prepareVideoView];

    self.livePlayer = [[TXLivePlayer alloc] init];
    [self.livePlayer setupVideoWidget:CGRectMake(0, 0, 0, 0) containView:videoView insertIndex:0];
    [self.livePlayer startPlay:url type:playType];
}

@end
