import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

/// Mixin for handling PDA scanner events.
mixin PdaListenerMixin<T extends StatefulWidget> on State<T> {
  bool _isListening = false;

  /// Whether this mixin is currently subscribed to the scanner stream.
  bool get isListening => _isListening;

  void initScanner() {
    PdaScanner.instance.registerListener(this);
    _isListening = true;
  }

  void disposeScanner() {
    PdaScanner.instance.unregisterListener(this);
    _isListening = false;
  }

  @override
  void initState() {
    super.initState();
    initScanner();
  }

  @override
  void dispose() {
    disposeScanner();
    super.dispose();
  }

  void onEvent(String data);
  void onError(Object error);
}

/// Main scanner class implementing singleton pattern.
class PdaScanner {
  static const String _channelName = 'com.rirong.flutter_pda_scanner_aida/plugin';

  PdaScanner._();
  static final PdaScanner instance = PdaScanner._();

  EventChannel? _scannerPlugin;
  StreamSubscription? _subscription;
  final List<PdaListenerMixin> _listeners = [];
  bool _isInitialized = false;

  /// Initialize the scanner. Safe to call multiple times; only the first
  /// call creates the native subscription.
  Future<void> initialize() async {
    if (_isInitialized) return;

    try {
      _scannerPlugin = const EventChannel(_channelName);
      _subscription = _scannerPlugin!.receiveBroadcastStream().listen(
            _onEvent,
            onError: _onError,
          );
      _isInitialized = true;
    } catch (_) {
      _isInitialized = false;
      rethrow;
    }
  }

  void registerListener(PdaListenerMixin listener) {
    if (!_listeners.contains(listener)) _listeners.add(listener);
  }

  void unregisterListener(PdaListenerMixin listener) {
    _listeners.remove(listener);
  }

  /// Tear down the native subscription. After this, [initialize] can be
  /// called again.
  Future<void> dispose() async {
    _listeners.clear();
    await _subscription?.cancel();
    _subscription = null;
    _scannerPlugin = null;
    _isInitialized = false;
  }

  void _onEvent(dynamic data) {
    if (data is! String) return;
    for (final listener in _listeners) {
      if (!listener.isListening) continue;
      try {
        listener.onEvent(data);
      } catch (_) {
        // Swallow listener errors so one bad listener doesn't break the rest.
      }
    }
  }

  void _onError(Object error) {
    for (final listener in _listeners) {
      if (!listener.isListening) continue;
      try {
        listener.onError(error);
      } catch (_) {
        // Swallow listener errors so one bad listener doesn't break the rest.
      }
    }
  }
}
