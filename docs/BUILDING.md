# Building

Split-Flap Drift uses Android platform APIs without application runtime dependencies.

## Requirements

- JDK 21
- Android SDK platform 36 and build-tools 36.0.0
- `openssl`, `keytool`, `zip`, and `shasum`

Set `ANDROID_HOME` if the SDK is not at `~/Library/Android/sdk`, then run:

```bash
./test.sh
./build.sh --unsigned
```

The unsigned build is suitable for inspection. A release build uses the
signing key at `~/.android/split-flap-drift.jks` and password at
`~/.android/split-flap-drift.pass`. The first signed build creates a unique local key
and password with owner-only permissions. Back up both files securely. They are
excluded from Git and cannot be recovered if lost. Do not generate a replacement
key for a repository that already has a public release.

Build outputs are written to the ignored `build/` directory. The script checks
the APK identity and rejects any runtime Internet permission.
