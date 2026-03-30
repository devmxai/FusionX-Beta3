import 'package:flutter/services.dart';

class NativeMediaThumbnailer {
  NativeMediaThumbnailer._();

  static const MethodChannel _methodChannel = MethodChannel(
    'fusionx.media/thumbnailer',
  );

  static Future<List<Uint8List>> generateVideoThumbnails({
    required String path,
    required List<double> timestampsSeconds,
    int targetWidth = 80,
    int targetHeight = 48,
  }) async {
    if (timestampsSeconds.isEmpty) {
      return const <Uint8List>[];
    }

    final raw = await _methodChannel.invokeMethod<List<dynamic>>(
      'generateVideoThumbnails',
      <String, Object?>{
        'path': path,
        'timestampsSeconds': timestampsSeconds,
        'targetWidth': targetWidth,
        'targetHeight': targetHeight,
      },
    );

    if (raw == null) {
      return const <Uint8List>[];
    }

    return raw.whereType<Uint8List>().toList(growable: false);
  }
}
