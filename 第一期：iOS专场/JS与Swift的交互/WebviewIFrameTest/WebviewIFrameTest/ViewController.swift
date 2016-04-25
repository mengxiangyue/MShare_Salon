//
//  ViewController.swift
//  WebviewIFrameTest
//
//  Created by xiangyue on 16/3/22.
//  Copyright © 2016年 xiangyue. All rights reserved.
//

import UIKit
import JavaScriptCore

class ViewController: UIViewController {
    
    lazy var webView = UIWebView()

    override func viewDidLoad() {
        super.viewDidLoad()
        webView.frame = self.view.frame
        webView.delegate = self
        self.view.addSubview(webView)
        let url = NSURL(fileURLWithPath: NSBundle.mainBundle().pathForResource("test", ofType: "html")!)
        let request = NSURLRequest(URL: url);
        self.webView.loadRequest(request)
        let button = UIButton(frame: CGRect(x: 300, y: 60, width: 60, height: 40))
        button.setTitle("后退", forState: .Normal)
        button.backgroundColor = UIColor.redColor()
        self.view.addSubview(button)
        button.addTarget(self, action: #selector(goBack), forControlEvents: .TouchUpInside)
        
        let reloadButton = UIButton(frame: CGRect(x: 300, y: 120, width: 60, height: 40))
        reloadButton.setTitle("刷新", forState: .Normal)
        reloadButton.backgroundColor = UIColor.redColor()
        self.view.addSubview(reloadButton)
        reloadButton.addTarget(self, action: #selector(refresh), forControlEvents: .TouchUpInside)
        
        let callJSButton = UIButton(frame: CGRect(x: 300, y: 180, width: 60, height: 40))
        callJSButton.setTitle("Call JS", forState: .Normal)
        callJSButton.backgroundColor = UIColor.redColor()
        self.view.addSubview(callJSButton)
        callJSButton.addTarget(self, action: #selector(callJS), forControlEvents: .TouchUpInside)
        
    }
    func goBack() {
        self.webView.goBack()
    }
    
    func refresh() {
        self.webView.reload()
    }
    
    func callJS() {
        self.webView.stringByEvaluatingJavaScriptFromString("alert('执行js函数')")
        
//        let context = JSContext()
        let context = webView.valueForKeyPath("documentView.webView.mainFrame.javaScriptContext") as! JSContext
        context.evaluateScript("var x = 2; var y = 3;")
        let result = context.evaluateScript("x + y")
        print("调用context获取的返回值:\(result)")
        context.evaluateScript("alert('通过context调用的函数')")
    }

    
}
extension ViewController : UIWebViewDelegate {
    func webView(webView: UIWebView, shouldStartLoadWithRequest request: NSURLRequest, navigationType: UIWebViewNavigationType) -> Bool {
        print("\(#file) \(#function)")
        return true
    }
    
    func webViewDidStartLoad(webView: UIWebView) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, Int64(1 * Double(NSEC_PER_SEC))), dispatch_get_main_queue()) {}
        
        struct Static {
            static var onceToken : dispatch_once_t = 0
        }
        
        dispatch_once(&Static.onceToken) {}
        
        let jsContext = webView.valueForKeyPath("documentView.webView.mainFrame.javaScriptContext") as! JSContext
        jsContext.setObject(JSBridegeObject(), forKeyedSubscript: "mxy")
        jsContext.exceptionHandler = {(context: JSContext!, exceptionValue: JSValue!) -> Void in
            print("异常了:\(exceptionValue)")
        }
        self.webView.stringByEvaluatingJavaScriptFromString("alert('webViewDidStartLoad-mxy')")
        print("\(#file) \(#function)")
        
        
    }
    
    func webViewDidFinishLoad(webView: UIWebView) {
        print("\(#file) \(#function)")
        let jsContext = webView.valueForKeyPath("documentView.webView.mainFrame.javaScriptContext") as! JSContext
        jsContext.evaluateScript("$('#label').text('这是OC文字')")
    }
    
    func webView(webView: UIWebView, didFailLoadWithError error: NSError?) {
        print("\(#file) \(#function)")
    }
    
    func share() {
        print("\(#file) \(#function)")
    }
}


// 必须使用@objc 标注出来 必须继承自JSExport协议
@objc protocol JavaScriptObjectiveCDelegate: JSExport {
    func share()
    func printLog(log: String)
    func getId() -> String
}

@objc class JSBridegeObject : NSObject, JavaScriptObjectiveCDelegate {
    func share() {
        print("这是JS->OC方法的调用")
    }
    func printLog(log: String) {
        print(log)
    }
    
    func getId() -> String {
        return "id:mengxiangyue"
    }
    
}

