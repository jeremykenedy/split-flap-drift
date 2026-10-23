package com.jeremykenedy.splitflapdrift;

import android.content.Context;
import android.content.SharedPreferences;

final class SplitFlapPreferences {
    static final String FILE = "split_flap_preferences";
    private final SharedPreferences values;

    SplitFlapPreferences(Context context) {
        values = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    SplitFlapOptions read() {
        SplitFlapOptions defaults = SplitFlapOptions.defaults();
        return new SplitFlapOptions(
                values.getInt("format", defaults.format),
                values.getInt("palette", defaults.palette),
                values.getInt("flip_speed", defaults.flipSpeed),
                values.getBoolean("date", defaults.date),
                values.getBoolean("seconds", defaults.seconds),
                values.getInt("lighting", defaults.lighting),
                values.getInt("scale", defaults.scale),
                values.getInt("brightness", defaults.brightness),
                values.getInt("random_mask", 0));
    }

    void write(SplitFlapOptions options) {
        values.edit()
                .putInt("format", options.format)
                .putInt("palette", options.palette)
                .putInt("flip_speed", options.flipSpeed)
                .putBoolean("date", options.date)
                .putBoolean("seconds", options.seconds)
                .putInt("lighting", options.lighting)
                .putInt("scale", options.scale)
                .putInt("brightness", options.brightness)
                .putInt("random_mask", options.randomMask)
                .apply();
    }

    boolean showClock() {
        return values.getBoolean("show_clock", true);
    }

    void setShowClock(boolean visible) {
        values.edit().putBoolean("show_clock", visible).apply();
    }

    void set(String key, String value) {
        if ("show_clock".equals(key)) {
            if (!"true".equals(value) && !"false".equals(value))
                throw new IllegalArgumentException("Clock visibility must be true or false");
            setShowClock(Boolean.parseBoolean(value));
            return;
        }
        SplitFlapOptions old = read();
        int format = old.format, palette = old.palette, speed = old.flipSpeed;
        int lighting = old.lighting,
                scale = old.scale,
                brightness = old.brightness,
                mask = old.randomMask;
        boolean date = old.date, seconds = old.seconds;
        switch (key) {
            case "format":
                format = choice(value, SplitFlapOptions.FORMATS, format);
                mask = random(mask, 1, value);
                break;
            case "palette":
                palette = choice(value, SplitFlapOptions.PALETTES, palette);
                mask = random(mask, 2, value);
                break;
            case "flipSpeed":
                speed = number(value, 1, 5, speed);
                mask = random(mask, 4, value);
                break;
            case "date":
                date = bool(value, date);
                mask = random(mask, 8, value);
                break;
            case "seconds":
                seconds = bool(value, seconds);
                mask = random(mask, 16, value);
                break;
            case "lighting":
                lighting = choice(value, SplitFlapOptions.LIGHTING, lighting);
                mask = random(mask, 32, value);
                break;
            case "scale":
                scale = number(value, 1, 5, scale);
                mask = random(mask, 64, value);
                break;
            case "brightness":
                brightness = number(value, 1, 5, brightness);
                mask = random(mask, 128, value);
                break;
            default:
                throw new IllegalArgumentException("Unsupported setting: " + key);
        }
        write(
                new SplitFlapOptions(
                        format, palette, speed, date, seconds, lighting, scale, brightness, mask));
    }

    private static int choice(String value, String[] choices, int fallback) {
        if ("random".equalsIgnoreCase(value)) return fallback;
        for (int i = 0; i < choices.length; i++) if (choices[i].equalsIgnoreCase(value)) return i;
        throw new IllegalArgumentException("Unsupported choice: " + value);
    }

    private static int number(String value, int low, int high, int fallback) {
        if ("random".equalsIgnoreCase(value)) return fallback;
        try {
            return SplitFlapOptions.clamp(Integer.parseInt(value), low, high);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Expected a number", error);
        }
    }

    private static boolean bool(String value, boolean fallback) {
        if ("random".equalsIgnoreCase(value)) return fallback;
        if ("true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value)) return false;
        throw new IllegalArgumentException("Expected on, off, or random");
    }

    private static int random(int mask, int bit, String value) {
        return "random".equalsIgnoreCase(value) ? mask | bit : mask & ~bit;
    }
}
