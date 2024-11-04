import 'package:flutter/services.dart';

class DeviceInfo {
  static const platform = MethodChannel('com.example.device_info_app');

  static Future<String?> getSerialNumber() async {
    try {
      final serialNumber = await platform.invokeMethod('getSerialNumber');
      return serialNumber;
    } on PlatformException catch (e) {
      print("Failed to get serial number: ${e.message}");
      return null;
    }
  }
}
