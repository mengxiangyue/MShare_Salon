//
//  ExampleUIWebViewController.h
//  ExampleApp-iOS
//
//  Created by Marcus Westin on 1/13/14.
//  Copyright (c) 2014 Marcus Westin. All rights reserved.
//

#import <UIKit/UIKit.h>
/*
 [Log] url:wvjbscheme://__BRIDGE_LOADED__ (ExampleApp.html, line 1)
 [Log] url:wvjbscheme://__WVJB_QUEUE_MESSAGE__ (ExampleApp.html, line 1)
 [Log] OC获取JS中的消息（_fetchQueue） – [] (0) (ExampleApp.html, line 1)
 [Log] _callWVJBCallbacks (ExampleApp.html, line 1)
 
 [Log] JS调用OC方法" (ExampleApp.html, line 58)
 [Log] url:wvjbscheme://__WVJB_QUEUE_MESSAGE__ (ExampleApp.html, line 1)
 [Log] OC获取JS中的消息（_fetchQueue） – [{handlerName: "testObjcCallback", data: {foo: "bar"}, callbackId: "JS->OC_cb_1_1461168751007"}] (1) (ExampleApp.html, line 1)
 [Log] JS接收到的OC的数据(_dispatchMessageFromObjC) – "{\"responseId\":\"JS->OC_cb_1_1461168751007\",\"responseData\":\"Response from testObjcCallback\"}" (ExampleApp.html, line 1)
 
 [Log] OC调用js方法 (ExampleApp.html, line 1)
 [Log] JS接收到的OC的数据(_dispatchMessageFromObjC) – "{\"callbackId\":\"OC->JS_cb_2\",\"data\":{\"greetingFromObjC\":\"Hi there, JS!\"},\"handlerName\":\"testJavascriptHandler\"}" (ExampleApp.html, line 1)
 [Log] url:wvjbscheme://__WVJB_QUEUE_MESSAGE__ (ExampleApp.html, line 1)
 [Log] OC获取JS中的消息（_fetchQueue） – [{responseId: "OC->JS_cb_2", responseData: {Javascript Says: "Right back atcha!"}}] (1) (ExampleApp.html, line 1)
 */

@interface ExampleUIWebViewController : UINavigationController <UIWebViewDelegate>

@end