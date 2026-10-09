# Architecture

The app is a native Android application with no runtime libraries. `SplitFlapDreamService` presents `SplitFlapSceneView` when the system starts the screensaver. The view draws the clock, card faces, lighting, and ambient motion with Android Canvas at the surface size supplied by the system.

Frame requests run at about 30 frames per second only while a preview or dream is visible. The handler stops when the activity pauses or the DreamService stops. Clock and date values come from the device's local time and locale.

`SplitFlapOptions` validates and resolves choices. `SplitFlapPreferences` persists them in app-private storage. `SplitFlapSettingsProvider` exposes the stable schema documented in [Settings provider](SETTINGS_PROVIDER.md). The settings activity uses the same preference model and supports a TV remote.

The DreamService component is `com.jeremykenedy.splitflapdrift/.SplitFlapDreamService`. The manifest includes the required bind permission, service action, metadata, preview drawable, and settings activity. It does not request Internet access.
