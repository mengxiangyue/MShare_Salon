//
//  WebViewJavascriptBridgeBase.h
//
//  Created by @LokiMeyburg on 10/15/14.
//  Copyright (c) 2014 @LokiMeyburg. All rights reserved.
//

#import <Foundation/Foundation.h>

#define kCustomProtocolScheme @"wvjbscheme"
#define kQueueHasMessage      @"__WVJB_QUEUE_MESSAGE__"
#define kBridgeLoaded         @"__BRIDGE_LOADED__"

typedef void (^WVJBResponseCallback)(id responseData);
typedef void (^WVJBHandler)(id data, WVJBResponseCallback responseCallback);
typedef NSDictionary WVJBMessage;

@protocol WebViewJavascriptBridgeBaseDelegate <NSObject>
- (NSString*) _evaluateJavascript:(NSString*)javascriptCommand;
@end

// 提供了与js类似的一套交互的工具类
@interface WebViewJavascriptBridgeBase : NSObject


@property (assign) id <WebViewJavascriptBridgeBaseDelegate> delegate;
// 在jsbridge没有注入到HTML之前发送的消息都会保存在该队列，注入成功后会被执行，然后该队列设置为nil
@property (strong, nonatomic) NSMutableArray* startupMessageQueue;

// OC调用js之后 返回数据 应该调用的block
@property (strong, nonatomic) NSMutableDictionary* responseCallbacks;

// 注册给js使用的Handler
@property (strong, nonatomic) NSMutableDictionary* messageHandlers;

// 没有用
@property (strong, nonatomic) WVJBHandler messageHandler;

+ (void)enableLogging;
+ (void)setLogMaxLength:(int)length;
- (void)reset;

// 发送数据给js也就是OC执行js中的方法
- (void)sendData:(id)data responseCallback:(WVJBResponseCallback)responseCallback handlerName:(NSString*)handlerName;

// 刷新js队列中的消息 即执行js对于OC的调用
- (void)flushMessageQueue:(NSString *)messageQueueString;

// 注入jsbridge
- (void)injectJavascriptFile;

// 判断js调用的scheme是否为 wvjbscheme
- (BOOL)isCorrectProcotocolScheme:(NSURL*)url;

// 判断是否为js发送的消息的url
- (BOOL)isQueueMessageURL:(NSURL*)urll;

// 判断是否为注入jsbridge的url
- (BOOL)isBridgeLoadedURL:(NSURL*)urll;

// url未知
- (void)logUnkownMessage:(NSURL*)url;

// 判断WebViewJavascriptBridge是否为object 没有地方调用
- (NSString *)webViewJavascriptCheckCommand;

// 返回获取js队列中消息的js代码 WebViewJavascriptBridge._fetchQueue();
- (NSString *)webViewJavascriptFetchQueyCommand;

@end