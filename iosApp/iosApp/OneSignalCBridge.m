#import "OneSignalCBridge.h"
#import "OneSignalHelper.h"
#import <Foundation/Foundation.h>

const char* getOneSignalPushId(void) {
    NSString *pushId = [[OneSignalNativeHelper shared] getRealPushSubscriptionId];
    if (pushId != nil) {
        return [pushId UTF8String];
    }
    return NULL;
}

const char* getOneSignalUserId(void) {
    NSString *userId = [[OneSignalNativeHelper shared] getRealOneSignalUserId];
    if (userId != nil) {
        return [userId UTF8String];
    }
    return NULL;
}