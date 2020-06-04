#import "SdkPlugin.h"
#import <MiSnapSDK/MiSnapSDK.h>
#import "MiSnapSDKViewControllerUX2.h"

@implementation SdkPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"my_snap_sdk"
            binaryMessenger:[registrar messenger]];
  SdkPlugin* instance = [[SdkPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}


FlutterResult resultGlobal;


- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([@"getPicture" isEqualToString:call.method]){
    // openCamera();
    // [self openCamera];
    // result(@"Todo bien");
      resultGlobal = result;
    UIViewController *viewController = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    if ( viewController.presentedViewController && !viewController.presentedViewController.isBeingDismissed ) {
        viewController = viewController.presentedViewController;
    }


// [viewController presentViewController:alert animated:YES completion:^{}]
        MiSnapSDKViewControllerUX2 *mvc = (MiSnapSDKViewControllerUX2 *)[[UIStoryboard storyboardWithName:@"MiSnapUX2" bundle:nil] instantiateViewControllerWithIdentifier:@"MiSnapSDKViewControllerUX2"];
    mvc.delegate=self;
    mvc.modalPresentationStyle = UIModalPresentationFullScreen;
    [mvc setupMiSnapWithParams:[MiSnapSDKViewControllerUX2 defaultParametersForIdCardFront]];
    [ viewController presentViewController:mvc animated:TRUE completion:^{}];
    // result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  }else{
    result(FlutterMethodNotImplemented);
  }
}

// Delegates

- (void)miSnapCancelledWithResults:(NSDictionary *)results {
    printf("miSnapCancelledWithResults");
}


- (void)miSnapCapturedOriginalImage:(UIImage *)originalImage andResults:(NSDictionary *)results {
    
//    results['']
    resultGlobal([FlutterStandardTypedData typedDataWithBytes:UIImagePNGRepresentation(originalImage)]);
}


@end
