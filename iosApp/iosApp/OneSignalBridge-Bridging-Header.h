//
//  OneSignalBridge-Bridging-Header.h
//  Aishou
//
//  Bridge header for OneSignal Kotlin-Swift interop
//

#ifndef OneSignalBridge_Bridging_Header_h
#define OneSignalBridge_Bridging_Header_h

// Import OneSignal Bridge for Kotlin interop
@interface OneSignalBridge : NSObject

+ (NSString * _Nullable)getPushSubscriptionId;
+ (BOOL)isPushOptedIn;
+ (NSString * _Nullable)getOneSignalPlayerId;
+ (void)waitForPushSubscriptionIdWithCompletion:(void (^)(NSString * _Nullable))completion;

@end

#endif /* OneSignalBridge_Bridging_Header_h */