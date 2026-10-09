# Installation and restoration

Download the signed APK and its matching checksum file from the
[latest release](https://github.com/jeremykenedy/split-flap-drift/releases/latest).
Enable ADB debugging on the TV and connect:

```bash
adb connect TV_IP:5555
python3 install.py --device TV_IP:5555
```

When installing an APK downloaded from a release, provide its path with
`--apk /path/to/split-flap-drift.apk`. The matching `.sha256` file must be next
to the APK. Without `--apk`, the installer uses the local signed build output.

The installer requires an available ADB device, verifies the APK checksum,
describes the screensaver selection and APK installation, and asks before
changing the TV. It stores the first observed selection under the ignored
`device-state.json`; use a separate `--state-file` path for each TV.

```bash
python3 install.py --device TV_IP:5555 --state-file device-states/living-room.json
```

Updates use Android's signed in-place install and retain settings. The package
and signing identity must remain unchanged. To restore the prior screensaver:

```bash
python3 install.py --device TV_IP:5555 --state-file device-states/living-room.json --restore
```

To restore and then remove Split-Flap Drift:

```bash
python3 install.py --device TV_IP:5555 --state-file device-states/living-room.json --uninstall
```

`--yes` accepts the listed operation without prompting. Keep the backup file
until restoration has succeeded. The installer does not change idle or sleep
timeouts and does not claim to prevent vendor software from changing device
settings.
