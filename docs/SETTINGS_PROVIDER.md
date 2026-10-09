# Settings provider

Host applications can discover and edit app preferences through the exported local content provider. It has no network behavior.

- Authority: `com.jeremykenedy.splitflapdrift.settings`
- Provider: `com.jeremykenedy.splitflapdrift.SplitFlapSettingsProvider`
- Schema version: `1`

Call `ContentResolver.call()` with a URI for this authority. `get_schema` and `get_settings` return JSON in a Bundle field named `json`. Use `set_setting` with the setting name as the method argument and a string `value` in extras. Supported values and bounds are defined by the schema. `set_random_all` and `set_random_none` control all supported fields. Random choices resolve once when a screensaver showing starts.

| Setting key | Type | Values |
|---|---|---|
| `format` | choice | `12 hour`, `24 hour` |
| `palette` | choice | `Midnight`, `Ivory`, `Ocean` |
| `flipSpeed` | integer | 1 through 5 |
| `date` | boolean | `true`, `false` |
| `seconds` | boolean | `true`, `false` |
| `lighting` | choice | `Auto`, `Day`, `Night` |
| `scale` | integer | 1 through 5 |
| `brightness` | integer | 1 through 5 |

Each setting also accepts `Random`. Unknown keys, choices, and methods fail with `IllegalArgumentException`. Changes apply the next time the scene is opened.
