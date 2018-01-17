#import <Cordova/CDV.h>
#import <TXLiteAVSDK_Smart/TXLivePush.h>
#import <TXLiteAVSDK_Smart/TXLivePlayer.h>

@interface CLiteAV : CDVPlugin

@property UIView* videoView;
@property TXLivePlayer* livePlayer;

- (void) startPlay:(CDVInvokedUrlCommand*)command;

@end