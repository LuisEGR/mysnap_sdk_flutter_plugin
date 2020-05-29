import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:my_snap_sdk/my_snap_sdk.dart';
import 'package:http/http.dart' as http;
import 'package:transparent_image/transparent_image.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Uint8List _picture = kTransparentImage;

  @override
  void initState() {
    super.initState();
  }

  Future<void> startGettingPicture() async {
    Uint8List picture;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      picture = await MySnapSdk.getPicture();
      //print(picture);
    } on PlatformException {
      picture = null;
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _picture = picture;
    });
  }

  // Test Convert image to Base64 for JSON Request
  Future<void> sendViaPost(bytes) async {
    var base64img = base64.encode(bytes);
    //print(base64img); // WARNING - NO PRINT, it's slow!

    // Test to view full base64 data
    // runing with: docker run -p 8080:80 -p 8443:443 --rm -t mendhak/http-https-echo
    http.post(
      'http://192.168.100.31:8080/test',
      headers: <String, String>{
        'Content-Type': 'application/json; charset=UTF-8',
      },
      body: jsonEncode(<String, String>{'title': "Prueba", 'image': base64img}),
    );
  }

  @override
  Widget build(BuildContext context) {
    Uint8List bytes = _picture;
    if (bytes != null) {
      print("Total Bytes:" + bytes.length.toString());
      //toBase64IMG(bytes);
    }
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              new Image.memory(bytes, width: 200, height: 400,), // Image to draw
              RaisedButton(
                onPressed: () => {startGettingPicture()},
                child: Text("START!"),
              )
            ],
          ),
        ),
      ),
    );
  }
}
