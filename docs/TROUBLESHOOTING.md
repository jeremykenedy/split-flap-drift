# Troubleshooting

## The screensaver does not appear in device settings

Confirm that the APK installed successfully and the device runs Android API 23
or newer. Some TV vendors hide third-party DreamService selection or limit idle
activation. The standalone installer reports whether its settings writes were
accepted, but vendor behavior still needs a device test.

## Installation fails checksum verification

Download the APK and checksum file from the same release. Do not edit or
repackage either file. If the checksum still fails, discard both files and
download them again.

## The preview is blank or stops moving

Leave the preview open for several seconds while the graphics surface starts.
If it remains blank, report the TV model, OS/API, resolution, and app version in
the GitHub issue tracker.

## Restore reports no saved settings

Use the same `--device` and `--state-file` values that were used during install.
The installer refuses to apply a backup associated with a different device.
