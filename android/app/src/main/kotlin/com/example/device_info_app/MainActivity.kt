package com.example.device_info_app

import android.os.Build
import android.provider.Settings
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel


class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.deviceInfo"
    private val REQUEST_PHONE_STATE = 1

    override fun configureFlutterEngine(flutterEngine: io.flutter.embedding.engine.FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "getDeviceIdentifier") {
                if (Build.VERSION.SDK_INT in Build.VERSION_CODES.O..Build.VERSION_CODES.P) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        result.success(getDeviceIdentifier())
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_PHONE_STATE)
                        result.success("Permission required")
                    }
                } else {
                    result.success(getDeviceIdentifier())
                }
            } else {
                result.notImplemented()
            }
        }
    }

    private fun getDeviceIdentifier(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } else {
            try {
                Build.getSerial()
            } catch (e: SecurityException) {
                "Permission denied"
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PHONE_STATE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
               flutterEngine?.dartExecutor?.binaryMessenger?.let { messenger ->
    MethodChannel(messenger, CHANNEL).invokeMethod("getDeviceIdentifier", getDeviceIdentifier())
}
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}