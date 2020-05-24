import 'dart:async';

import 'package:flutter/services.dart';

class MySnapSdk {
  static const MethodChannel _channel =
      const MethodChannel('my_snap_sdk');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
