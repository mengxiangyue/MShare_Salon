//
//  ViewController.m
//  RSA_OC
//
//  Created by xiangyue on 15/11/18.
//  Copyright © 2015年 xiangyue. All rights reserved.
//

#import "ViewController.h"
#import "JSRSA.h"
#import <CommonCrypto/CommonDigest.h>

// header search path
// key pem

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // set the public/private key
    [JSRSA sharedInstance].publicKey = @"public_key.pem";
    [JSRSA sharedInstance].privateKey = @"private_key.pem";
    
    NSString *encryptText = [[JSRSA sharedInstance] publicEncrypt:@"中文"];
    NSLog(@"\n\n-------Encrypt String 中文:\n%@\n----------\n\n", encryptText);
//    NSLog(@"\n\n--------Decrypt String:\n%@\n----------\n\n", [[JSRSA sharedInstance] privateDecrypt:encryptText]);
//    
//    NSLog(@"\n\n\n\n--------------private encrypt public decrypt------------");
    encryptText = [[JSRSA sharedInstance] privateEncrypt:@"测试方法"];
    NSLog(@"-------Encrypt String 测试方法:\n%@\n----------", encryptText);
//    NSLog(@"--------Decrypt String:\n%@\n----------", [[JSRSA sharedInstance] publicDecrypt:encryptText]);
//    NSLog(@"--------------private encrypt public decrypt end------------\n\n\n\n");
    
    encryptText = [NSString stringWithFormat:@"%@%@%@%@%@",
                   @"deC3mLk/DWU3x0g7xVpKd6QVWEiRq+xvwDNrqOR4oNbLmChxsMwwTlvn45CK86CNlOt/NMQz4zq+",
                   @"BI3wKL5Wa24R6l/+E7RiwHt/PYXovk2aCWlokedIM5sSSkSxnAd7josRH1GWSiP6e1C7uBv2LR2Z",
                   @"t8Nvi7nD2CVoWu/u5ux56pG5xaBiWgICADttQkmuX59jpcjPDbGQcKsxs788aGa632vtpySsY8kb",
                   @"IBqcmk0EFOHEmo7dLaM3DXnHJbcioeonWhOf26nKHrIoZJkFv93G/1nULXNAXfjXi3tlx7VLc2/Y",
                   @"aCZbH6FNJyTTiVtV2vKQPmkx3eLpABaiv1mMjQ=="];
    NSString *decryptText = [[JSRSA sharedInstance] publicDecrypt:encryptText];
    NSLog(@"--------Decrypt String:\n%@\n----------", decryptText);
    
    encryptText = [NSString stringWithFormat:@"%@%@%@%@%@",
                   @"s0gGVqw9Wep8CmjvLOR+R/HtQ7pgKHUrsd3bAsSXFqalL87fOyXkk498lHc+sjaST7oWx0loN644",
                   @"XYeuH9POgjqp6t3CcxdTTRahtqmOXpzNCtZIZ1spiDkq4eDCAHe7UoNwkrx8sAcccsrz5n2JiIWN",
                   @"rVaA/QVW1sr82RZa53nAmdWPRzmeNZsM2VsuqK0B8sDVGA0vwSPnWGH2F1p6y4eedUohF8ElKzoT",
                   @"B/jFu/KoRtu7N3Df5K22+1SX1eSaC496o48lxmfyGTxLLBkMigQFYUzMI8Sz21C4lFWAZ2YET4uS",
                   @"vvGRGeBFROqGi/HSpS9S+5GQH7A+QcekSz+V2w=="];
   decryptText = [[JSRSA sharedInstance] privateDecrypt:encryptText];
   NSLog(@"--------Decrypt String:\n%@\n----------", decryptText);
    
    
    
//    NSLog(@"---------md5--------\n%@", [self md5:@"mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123mxy123"]);
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (NSString *) md5:(NSString *)str
{
    const char *cStr = [str UTF8String];
    unsigned char result[16];
    CC_MD5( cStr, strlen(cStr), result );
    return [NSString stringWithFormat:@"%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X",
            result[0], result[1], result[2], result[3],
            result[4], result[5], result[6], result[7],
            result[8], result[9], result[10], result[11],
            result[12], result[13], result[14], result[15]
            ];
}

@end
