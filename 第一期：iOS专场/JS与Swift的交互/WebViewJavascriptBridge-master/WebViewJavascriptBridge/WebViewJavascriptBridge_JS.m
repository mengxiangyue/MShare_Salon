// This file contains the source for the Javascript side of the
// WebViewJavascriptBridge. It is plaintext, but converted to an NSString
// via some preprocessor tricks.
//
// Previous implementations of WebViewJavascriptBridge loaded the javascript source
// from a resource. This worked fine for app developers, but library developers who
// included the bridge into their library, awkwardly had to ask consumers of their
// library to include the resource, violating their encapsulation. By including the
// Javascript as a string resource, the encapsulation of the library is maintained.

#import "WebViewJavascriptBridge_JS.h"

NSString * WebViewJavascriptBridge_js() {
	#define __wvjb_js_func__(x) #x
	
	// BEGIN preprocessorJSCode
	static NSString * preprocessorJSCode = @__wvjb_js_func__(
;(function() {
	if (window.WebViewJavascriptBridge) {
		return;
	}
	window.WebViewJavascriptBridge = {
		registerHandler: registerHandler,
		callHandler: callHandler,
		_fetchQueue: _fetchQueue,
		_handleMessageFromObjC: _handleMessageFromObjC
	};

    // 用于刷新界面的iframe
	var messagingIframe;
    // 向OC传递数据的队列
	var sendMessageQueue = [];
    // 注册的供OC调用的handler
	var messageHandlers = {};
	
	var CUSTOM_PROTOCOL_SCHEME = 'wvjbscheme';
	var QUEUE_HAS_MESSAGE = '__WVJB_QUEUE_MESSAGE__';
	
    // 存储数据从OC返回以后的回调方法
	var responseCallbacks = {};
	var uniqueId = 1;

    // 注册Handler
	function registerHandler(handlerName, handler) {
		messageHandlers[handlerName] = handler;
	}
	
    // 调用OC中注册的方法
	function callHandler(handlerName, data, responseCallback) {
        // 调用无参数方法
		if (arguments.length == 2 && typeof data == 'function') {
			responseCallback = data;
			data = null;
		}
		_doSend({ handlerName:handlerName, data:data }, responseCallback);
	}
	
	function _doSend(message, responseCallback) {
        // 存在回调方法的时候 需要生成callbackId 传入OC OC返回的数据中返回responseId（值等于callbackId） js中通过该responseId决定调用哪个callback方法
		if (responseCallback) {
			var callbackId = 'JS->OC_cb_'+(uniqueId++)+'_'+new Date().getTime();
			responseCallbacks[callbackId] = responseCallback;
			message['callbackId'] = callbackId;
		}
        // 将消息放入队列，刷新iframe，触发OC执行
		sendMessageQueue.push(message);
		messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;
	}

    // 该方法只是服务器OC OC通过调用该方法获取JS环境中的调用命令
	function _fetchQueue() {
		var messageQueueString = JSON.stringify(sendMessageQueue);
        console.log('OC获取JS中的消息（_fetchQueue）', sendMessageQueue);
		sendMessageQueue = [];
		return messageQueueString;
	}

    // 处理从OC对js的调用
	function _dispatchMessageFromObjC(messageJSON) {
        console.log('JS接收到的OC的数据(_dispatchMessageFromObjC)', messageJSON);
		setTimeout(function _timeoutDispatchMessageFromObjC() {
			var message = JSON.parse(messageJSON);
			var messageHandler;
			var responseCallback;
            // 返回的数据中有responseId 表示是js调用OC返回的数据 直接调用对应的callback
			if (message.responseId) {
				responseCallback = responseCallbacks[message.responseId];
				if (!responseCallback) {
					return;
				}
				responseCallback(message.responseData);
                // 调用完成callback后删除
				delete responseCallbacks[message.responseId];
			} else {
                // OC主动调用js中的方法
                // 如果包含callbackId 表示需要js返回数据
				if (message.callbackId) {
					var callbackResponseId = message.callbackId;
                    // 构造一个回调方法 主要就是js执行完后向OC发送消息
					responseCallback = function(responseData) {
                        // 同样返回的数据包含responseId
						_doSend({ responseId:callbackResponseId, responseData:responseData });
					};
				}
				
                // 获取js注册的Handler
				var handler = messageHandlers[message.handlerName];
				try {
                    // 调用js中的Handler Handler执行完成后就会调用responseCallback 在responseCallback又调用了_doSend方法
					handler(message.data, responseCallback);
				} catch(exception) {
					console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
				}
				if (!handler) {
					console.log("WebViewJavascriptBridge: WARNING: no handler for message from ObjC:", message);
				}
			}
		});
	}
	
    // OC中直接调用的是该方法
	function _handleMessageFromObjC(messageJSON) {
        _dispatchMessageFromObjC(messageJSON);
	}

    // 创建一个iframe用于刷新数据
	messagingIframe = document.createElement('iframe');
	messagingIframe.style.display = 'none';
	messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;
	document.documentElement.appendChild(messagingIframe);

	setTimeout(_callWVJBCallbacks, 0);
	function _callWVJBCallbacks() {
        console.log('_callWVJBCallbacks');
        // 如果前端存在未执行的js 在这里执行
		var callbacks = window.WVJBCallbacks;
		delete window.WVJBCallbacks;
		for (var i=0; i<callbacks.length; i++) {
			callbacks[i](WebViewJavascriptBridge);
		}
	}
})();
	); // END preprocessorJSCode

	#undef __wvjb_js_func__
	return preprocessorJSCode;
};