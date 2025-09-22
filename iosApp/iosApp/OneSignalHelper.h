#import <Foundation/Foundation.h>

@interface OneSignalNativeHelper : NSObject

+ (instancetype)shared;
- (NSString * _Nullable)getRealPushSubscriptionId;
- (NSString * _Nullable)getRealOneSignalUserId;

@end