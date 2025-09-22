#ifndef OneSignalCBridge_h
#define OneSignalCBridge_h

#ifdef __cplusplus
extern "C" {
#endif

// C bridge functions for OneSignal
const char* getOneSignalPushId(void);
const char* getOneSignalUserId(void);

#ifdef __cplusplus
}
#endif

#endif /* OneSignalCBridge_h */