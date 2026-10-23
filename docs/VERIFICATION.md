# Verification

## Build and local tests

The app built as a signed APK with Android SDK platform 36 and build-tools 36.0.0. `./test.sh` passed 16 Python installer tests and the Java options tests. The APK manifest was checked for package identity, the launcher, DreamService permission/action/component, settings provider, and absence of Internet permission.

## Emulator evidence

| Device | Platform | Resolution | Result |
|---|---|---|---|
| Google Android TV emulator `sdk_google_atv64_arm64` | Android 12, API 31 | 1920 x 1080 | Installed signed build, opened the clock preview, observed the rendered clock and ambient motion, and captured the README screenshot after more than 45 seconds. |
| Fire TV hardware | Untested; no device was available | Not measured | Looking for a Fire TV owner to check activation, settings with a remote, returning from the dream, and prolonged display behavior. Please report the model, Fire OS/API, resolution, tests, and results in the repository issue tracker. |
| Android TV hardware | Untested; no device was available | Not measured | Looking for an Android TV owner to test and report the model, OS/API, resolution, tested behavior, and results in the repository issue tracker. |
| Google TV hardware | Untested; no device was available | Not measured | Looking for a Google TV owner to test and report the model, OS/API, resolution, tested behavior, and results in the repository issue tracker. |

The emulator run does not verify OEM selection menus, idle activation, physical remote behavior, 4K composition, or sustained thermal performance. Native 4K output is not claimed; the view uses Android's provided surface size.

## Optional clock regression, 2026-10-22

An isolated Android TV emulator fixture compiled the production scene and provider classes from this change. The combined five-app run passed 123 actual Canvas/provider assertions. It checked the existing appearance default (ON), explicit ON/OFF under scene randomization, portable boolean storage and schema, and invalid-write retention. OFF produced zero time/date/seconds text draws while the scene remained drawn. Seconds, date and colon-dot paths were explicitly exercised.

This isolated regression does not establish production DreamService binding, physical-TV performance or acceptance of the existing artwork. Signed release and both-TV installation gates remain pending.
