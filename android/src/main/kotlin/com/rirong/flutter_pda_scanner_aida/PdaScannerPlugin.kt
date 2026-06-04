package com.rirong.flutter_pda_scanner_aida

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel

class PdaScannerPlugin: FlutterPlugin, EventChannel.StreamHandler, ActivityAware {
  private companion object {
    private const val CHANNEL = "com.rirong.flutter_pda_scanner_aida/plugin"
    private const val AIDA_SCAN_ACTION = "com.android.scanner.broadcast"
    private const val AIDA_SCAN_EXTRA = "scandata"
  }

  private var activity: Activity? = null
  private var eventChannel: EventChannel? = null
  private var eventSink: EventChannel.EventSink? = null

  private val scanReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      intent?.let { intentNonNull ->
        Log.d("PdaScannerPlugin", "onReceive action=${intentNonNull.action}")
        if (intentNonNull.action == AIDA_SCAN_ACTION) {
          Log.d("PdaScannerPlugin", "AIDA action matched, extracting extra=$AIDA_SCAN_EXTRA")
          val result = intentNonNull.getStringExtra(AIDA_SCAN_EXTRA)?.trim()
            ?: intentNonNull.getByteArrayExtra(AIDA_SCAN_EXTRA)?.let { String(it).trim() }

          if (result != null) {
            Log.d("PdaScannerPlugin", "AIDA scan result='$result', sending to Flutter")
            eventSink?.success(result)
          } else {
            Log.w("PdaScannerPlugin", "AIDA scan extra missing or empty")
          }
        } else {
          Log.d("PdaScannerPlugin", "Ignored action: ${intentNonNull.action}")
        }
      }
    }
  }

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    Log.d("PdaScannerPlugin", "onAttachedToEngine")
    eventChannel = EventChannel(binding.binaryMessenger, CHANNEL).apply {
      setStreamHandler(this@PdaScannerPlugin)
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    Log.d("PdaScannerPlugin", "onDetachedFromEngine")
    eventChannel?.setStreamHandler(null)
    eventChannel = null
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    Log.d("PdaScannerPlugin", "onAttachedToActivity")
    activity = binding.activity
    registerReceivers(binding.activity)
  }

  override fun onDetachedFromActivity() {
    Log.d("PdaScannerPlugin", "onDetachedFromActivity")
    try {
      activity?.unregisterReceiver(scanReceiver)
    } catch (e: Exception) {
      // ignore
    }
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    Log.d("PdaScannerPlugin", "onListen: Flutter side subscribed to EventChannel")
    eventSink = events
  }

  override fun onCancel(arguments: Any?) {
    Log.d("PdaScannerPlugin", "onCancel: Flutter side unsubscribed from EventChannel")
    eventSink = null
  }

  private fun registerReceivers(activity: Activity) {
    try {
      activity.unregisterReceiver(scanReceiver)
    } catch (e: Exception) {
      // ignore
    }

    val filter = IntentFilter().apply {
      addAction(AIDA_SCAN_ACTION)
      priority = IntentFilter.SYSTEM_HIGH_PRIORITY
    }

    Log.d("PdaScannerPlugin", "registerReceivers: registering AIDA action=$AIDA_SCAN_ACTION")
    activity.registerReceiver(scanReceiver, filter)
  }
}