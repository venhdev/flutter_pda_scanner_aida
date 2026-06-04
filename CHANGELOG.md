## 1.0.0

* **Renamed:** `flutter_pda_scanner_v2` → `flutter_pda_scanner_aida`.
* **Android-only:** Removed iOS platform entry and the `ios/` directory.
* **Namespace:** Migrated from `com.ztt.*` to `com.rirong.*`.
* **Version:** First stable release of the renamed plugin. The plugin remains AIDA-only, Built-in-Kotlin, and ships a single `EventChannel` for Dart consumers.

## 0.0.10

* **Breaking:** Simplified plugin to AIDA-only scanner support.
* Native Android receiver now only listens for `com.android.scanner.broadcast` with extra `scandata`.
* Dart API reduced to a single `EventChannel` with a listener mixin (`PdaListenerMixin`); `onEvent` now takes `String` (was `Object`).
* Migrated to Flutter's Built-in Kotlin Gradle Plugin (no `apply plugin: 'kotlin-android'`, no `buildscript classpath`).
* Removed unused `plugin_platform_interface` dependency and obsolete unit tests.
* iOS plugin entry retained as a placeholder; no native iOS implementation is included in this release.

## 0.0.9

* Upgrade Kotlin version from 1.7.10 to 2.1.0.

## 0.0.8

* Add support for SUNMI scanner.

## 0.0.7

* Add support for Hikvision scanner.

## 0.0.6

* Fix the NEWLAND(新大陆)-PDA scanner that does not register the broadcast receiver.

## 0.0.5

* Update the `README.md` file.

## 0.0.4

* Update the `README.md` file.

## 0.0.3

* Remove the `this.` keyword from `initScanner()` and `disposeScanner()` methods.

## 0.0.2

* Minor tweaks to handle older Dart SDK versions.

## 0.0.1

* First initial release that supports Android V2 Embedding.
