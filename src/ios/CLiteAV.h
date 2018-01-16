#import <Cordova/CDV.h>
#import <TXLiteAVSDK_Smart/TXLivePush.h>
#import <TXLiteAVSDK_Smart/TXLivePlayer.h>

@interface CLiteAV : CDVPlugin

@property UIView* videoView;
@property TXLivePush* livePusher;
@property TXLivePlayer* livePlayer;

- (void) getVersion:(CDVInvokedUrlCommand*)command;
- (void) startPlay:(CDVInvokedUrlCommand*)command;

@end