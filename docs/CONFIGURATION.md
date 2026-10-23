# Configuration

Open Split-Flap Drift from the TV app list and choose **Settings**. The large buttons can be changed with a D-pad remote. Each field cycles through its fixed choices and then Random. Random values are picked once when the screensaver starts; saved selections remain unchanged.

| Setting | Choices | Default |
|---|---|---|
| Time format | 12 hour, 24 hour | 12 hour |
| Clock finish | Midnight, Ivory, Ocean | Midnight |
| Flip pace | Slow, Relaxed, Balanced, Quick, Very quick | Balanced |
| Show date | Off, On | Off |
| Show seconds | Off, On | Off |
| Lighting | Auto, Day, Night | Auto |
| Clock size | Very small, Small, Medium, Large, Very large | Large |
| Brightness | Dim, Low, Balanced, Bright, Very bright | Bright |

**Auto** uses local device time and dims the scene from 7 p.m. to 7 a.m. **Day** and **Night** keep one lighting mode. Brightness adjusts the renderer's light level. Randomize supported settings and Use fixed settings apply to all fields at once.

Settings are stored in app-private preferences. Host apps may inspect the setting schema through the local provider documented in [Settings provider](SETTINGS_PROVIDER.md).

## Clock visibility

`show_clock` is a boolean with default `true` and `random=false`. It is stored independently of randomized style fields. Explicit ON and OFF survive randomize-all and fixed-all. OFF suppresses all time, date and seconds text.
