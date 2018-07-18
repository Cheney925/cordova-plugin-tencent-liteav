#import <Cordova/CDV.h>
#import <TXLiteAVSDK_Smart/TXLivePush.h>
#import <TXLiteAVSDK_Smart/TXLivePlayer.h>
#import <TXLiteAVSDK_Smart/TXVodPlayer.h>
#import <TXLiteAVSDK_Smart/TXLivePlayListener.h>

@interface CLiteAV : CDVPlugin <TXLivePlayListener>

@property UIView *videoView;
@property TXLivePlayer *livePlayer;
@property TXVodPlayer *vodPlayer;
@property TXLivePush *livePusher;
@property int playerWidth;
@property int playerHeight;
@property int playerMode;
@property int statusBarHeight;
@property NSDictionary *netStatus;

- (void) startPlay:(CDVInvokedUrlCommand*)command;
- (void) stopPlay:(CDVInvokedUrlCommand*)command;
- (void) pause:(CDVInvokedUrlCommand*)command;
- (void) resume:(CDVInvokedUrlCommand*)command;
- (void) setPlayMode:(CDVInvokedUrlCommand*)command;
- (void) getNetStatus:(CDVInvokedUrlCommand*)command;
- (void) startLinkMic:(CDVInvokedUrlCommand*)command;
- (void) stopLinkMic:(CDVInvokedUrlCommand*)command;
- (NSString*) convertToJSONData;

@end

