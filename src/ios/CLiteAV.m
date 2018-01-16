#import <CLiteAV.h>

#import <MainViewController.h>

@implementation MainViewController(CDVViewController)
- (void) viewDidLoad {
    [super viewDidLoad];
    self.webView.backgroundColor = [UIColor clearColor];
    self.webView.opaque = NO;
}
@end

@implementation CLiteAV

@synthesize videoView;
@synthesize livePusher;
@synthesize livePlayer;

//- (void) greet:(CDVInvokedUrlCommand*)command {
//    NSString* name = [[command arguments] objectAtIndex:0];
//    NSString* msg = [NSString stringWithFormat: @"Hello, %@", name];
//    CDVPluginResult* result = [CDVPluginResult
//                               resultWithStatus:CDVCommandStatus_OK
//                               messageAsString:msg];
//    [self alert:msg];
//    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
//}

- (void) prepareVideoView {
    if (self.videoView) return;
    self.videoView = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame]];
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

- (void) getVersion:(CDVInvokedUrlCommand*)command {
    NSString* version = [[TXLivePush getSDKVersion] componentsJoinedByString:@"."];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:version];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) startPlay:(CDVInvokedUrlCommand*)command {
    if (self.livePlayer) return;
    NSString* url = [command.arguments objectAtIndex:0];
//    TX_Enum_PlayType playUrlType = (TX_Enum_PlayType)[command.arguments objectAtIndex:1];
//    NSInteger playUrlType = (NSInteger)[command.arguments objectAtIndex:1];

    [self prepareVideoView];

    self.livePlayer = [[TXLivePlayer alloc] init];
    [self.livePlayer setupVideoWidget:CGRectMake(0, 0, 0, 0) containView:videoView insertIndex:0];
    [self.livePlayer startPlay:url type:PLAY_TYPE_LIVE_FLV];
}

@end
