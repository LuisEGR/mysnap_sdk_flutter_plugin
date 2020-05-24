import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:my_snap_sdk/my_snap_sdk.dart';

void main() {
  const MethodChannel channel = MethodChannel('my_snap_sdk');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await MySnapSdk.platformVersion, '42');
  });
}
