import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class DownloadManagerPlugin {
  static const MethodChannel _channel =
      const MethodChannel('download_manager_plugin');

  static Future startDownload({@required String url ,@required String fileName ,@required String mime}) async {
    await _channel.invokeMethod(
      'downloadManager',
      {
        "url": "$url",
        "name": "$fileName",
        "mime": "$mime",
      },
    );
  }
}
