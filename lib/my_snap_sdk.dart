import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class MySnapSdk {
  static const MethodChannel _channel =
      const MethodChannel('my_snap_sdk');

  static Future<Uint8List> getPicture() async {
    final Uint8List picture = await _channel.invokeMethod('getPicture');
    return picture;
  }
}
