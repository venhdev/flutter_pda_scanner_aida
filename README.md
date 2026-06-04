# Flutter PDA Scanner AIDA

Flutter plugin for **AIDA** PDA handheld scanner devices on **Android only**.
The plugin listens for AIDA's `Intent Output` broadcasts and forwards the
decoded barcode to your Dart code via an `EventChannel`.

[![License][license-image]][license-url]

> **Android only.** This plugin has no iOS implementation. AIDA devices run
> Android; the iOS platform entry has been removed from `pubspec.yaml` and
> the `ios/` directory deleted.

## Installation

```yaml
dependencies:
  flutter_pda_scanner_aida: ^1.0.0
```

For development against a local checkout:

```yaml
dependencies:
  flutter_pda_scanner_aida:
    path: ../refs/flutter_pda_scanner_aida
```

## AIDA device setup

On the AIDA device, open the **Scanner** app and configure:

| Setting | Value |
| --- | --- |
| Decode mode | `Intent Output` (not `Direct Fill`) |
| Broadcast Intent action | `com.android.scanner.broadcast` |
| Broadcast Intent extra | `scandata` |
| Barcode end character | `ENTER` |

The plugin trims whitespace and line terminators before delivering the value
to Dart, so the `ENTER` suffix is removed automatically.

## Usage

```dart
import 'package:flutter/material.dart';
import 'package:flutter_pda_scanner_aida/flutter_pda_scanner_aida.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await PdaScanner.instance.initialize();
  runApp(const MyApp());
}
```

In your screen's `State`, mix in `PdaListenerMixin` and implement
`onEvent` / `onError`:

```dart
class _HomeScreenState extends State<HomeScreen>
    with PdaListenerMixin<HomeScreen> {
  String _code = '';

  @override
  void onEvent(String data) => setState(() => _code = data);

  @override
  void onError(Object error) {
    // Show a toast, log, etc.
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(body: Center(child: Text('Last scan: $_code')));
  }
}
```

## API summary

| Symbol | Purpose |
| --- | --- |
| `PdaScanner.instance.initialize()` | One-time setup. Must be awaited before `runApp`. |
| `PdaListenerMixin<T>` | Mix into a `State<T>` to receive scans. |
| `void onEvent(String data)` | Called on every scan. |
| `void onError(Object error)` | Called on stream errors. |
| `bool isListening` | `true` between `initState` and `dispose`. |

## License

Distributed under the MIT license. See `LICENSE` for more information.

[license-image]: https://img.shields.io/badge/License-MIT-blue.svg
[license-url]: LICENSE
