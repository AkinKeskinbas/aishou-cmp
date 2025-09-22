#import "OneSignalHelper.h"
#import <OneSignalFramework/OneSignalFramework.h>

@implementation OneSignalNativeHelper

+ (instancetype)shared {
    static OneSignalNativeHelper *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[OneSignalNativeHelper alloc] init];
    });
    return sharedInstance;
}

- (NSString * _Nullable)getRealPushSubscriptionId {
    NSLog(@"🔍 OneSignal: Getting REAL pushSubscription.id");

    NSString *pushId = OneSignal.User.pushSubscription.id;
    if (pushId != nil && pushId.length > 0) {
        NSLog(@"✅ OneSignal: Got REAL pushSubscription.id: %@", pushId);
        return pushId;
    }

    NSLog(@"⚠️ OneSignal: pushSubscription.id is nil/empty");
    return nil;
}

- (NSString * _Nullable)getRealOneSignalUserId {
    NSLog(@"🔍 OneSignal: Getting REAL onesignalId");

    NSString *userId = OneSignal.User.onesignalId;
    if (userId != nil && userId.length > 0) {
        NSLog(@"✅ OneSignal: Got REAL onesignalId: %@", userId);
        return userId;
    }

    NSLog(@"⚠️ OneSignal: onesignalId is nil/empty");
    return nil;
}

@end