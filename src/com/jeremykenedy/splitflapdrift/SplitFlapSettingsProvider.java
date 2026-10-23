package com.jeremykenedy.splitflapdrift;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public final class SplitFlapSettingsProvider extends ContentProvider {
    static final String AUTHORITY = "com.jeremykenedy.splitflapdrift.settings";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(
            Uri uri, String[] projection, String selection, String[] args, String order) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return "application/json";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Use set_setting.");
    }

    @Override
    public int delete(Uri uri, String selection, String[] args) {
        throw new UnsupportedOperationException("Settings cannot be deleted.");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] args) {
        throw new UnsupportedOperationException("Use set_setting.");
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if ("get_schema".equals(method)) return result(schema());
        SplitFlapPreferences preferences = new SplitFlapPreferences(getContext());
        if ("get_settings".equals(method))
            return result(settings(preferences.read(), preferences.showClock()));
        if ("set_random_all".equals(method) || "set_random_none".equals(method)) {
            SplitFlapOptions o = preferences.read();
            int mask = "set_random_all".equals(method) ? 255 : 0;
            preferences.write(
                    new SplitFlapOptions(
                            o.format,
                            o.palette,
                            o.flipSpeed,
                            o.date,
                            o.seconds,
                            o.lighting,
                            o.scale,
                            o.brightness,
                            mask));
            return result(settings(preferences.read(), preferences.showClock()));
        }
        if ("set_setting".equals(method)) {
            if (extras == null || !extras.containsKey("value"))
                throw new IllegalArgumentException("Missing setting value.");
            preferences.set(arg, extras.getString("value"));
            return result(settings(preferences.read(), preferences.showClock()));
        }
        throw new IllegalArgumentException("Unsupported settings method: " + method);
    }

    private Bundle result(String json) {
        Bundle b = new Bundle();
        b.putString("json", json);
        return b;
    }

    static String schema() {
        return "{\"schemaVersion\":1,\"provider\":\""
                + AUTHORITY
                + "\",\"fields\":["
                + choice("format", "12 hour", SplitFlapOptions.FORMATS)
                + ","
                + choice("palette", "Midnight", SplitFlapOptions.PALETTES)
                + ","
                + number("flipSpeed", 3, 1, 5)
                + ","
                + bool("date", false)
                + ","
                + bool("seconds", false)
                + ","
                + choice("lighting", "Auto", SplitFlapOptions.LIGHTING)
                + ","
                + number("scale", 4, 1, 5)
                + ","
                + number("brightness", 4, 1, 5)
                + ",{\"key\":\"show_clock\",\"type\":\"boolean\",\"default\":true,\"random\":false}"
                + "]}";
    }

    private static String choice(String key, String value, String[] options) {
        StringBuilder json =
                new StringBuilder("{\"key\":\"")
                        .append(key)
                        .append("\",\"type\":\"choice\",\"default\":\"")
                        .append(value)
                        .append("\",\"random\":true,\"choices\":[");
        for (int i = 0; i < options.length; i++) {
            if (i > 0) json.append(',');
            json.append('"').append(options[i]).append('"');
        }
        return json.append("]}").toString();
    }

    private static String number(String key, int value, int min, int max) {
        return "{\"key\":\""
                + key
                + "\",\"type\":\"integer\",\"default\":"
                + value
                + ",\"random\":true,\"minimum\":"
                + min
                + ",\"maximum\":"
                + max
                + "}";
    }

    private static String bool(String key, boolean value) {
        return "{\"key\":\""
                + key
                + "\",\"type\":\"boolean\",\"default\":"
                + value
                + ",\"random\":true}";
    }

    static String settings(SplitFlapOptions o, boolean showClock) {
        String scene =
                "{\"schemaVersion\":1,\"format\":\""
                        + val(o, 1, SplitFlapOptions.FORMATS[o.format])
                        + "\",\"palette\":\""
                        + val(o, 2, SplitFlapOptions.PALETTES[o.palette])
                        + "\",\"flipSpeed\":\""
                        + val(o, 4, Integer.toString(o.flipSpeed))
                        + "\",\"date\":\""
                        + val(o, 8, Boolean.toString(o.date))
                        + "\",\"seconds\":\""
                        + val(o, 16, Boolean.toString(o.seconds))
                        + "\",\"lighting\":\""
                        + val(o, 32, SplitFlapOptions.LIGHTING[o.lighting])
                        + "\",\"scale\":\""
                        + val(o, 64, Integer.toString(o.scale))
                        + "\",\"brightness\":\""
                        + val(o, 128, Integer.toString(o.brightness))
                        + "\"}";
        return scene.substring(0, scene.length() - 1) + ",\"show_clock\":" + showClock + "}";
    }

    private static String val(SplitFlapOptions o, int bit, String value) {
        return (o.randomMask & bit) == 0 ? value : "Random";
    }
}
