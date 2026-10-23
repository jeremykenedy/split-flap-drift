package com.jeremykenedy.splitflapdrift;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class SettingsActivity extends Activity {
    private static final String[] KEYS = {
        "format", "palette", "flipSpeed", "date", "seconds", "lighting", "scale", "brightness"
    };
    private static final String[] LABELS = {
        "Time format",
        "Clock finish",
        "Flip pace",
        "Show date",
        "Show seconds",
        "Lighting",
        "Clock size",
        "Brightness"
    };
    private static final int[] BITS = {1, 2, 4, 8, 16, 32, 64, 128};
    private static final String[][] VALUES = {
        {"12 hour", "24 hour"},
        {"Midnight", "Ivory", "Ocean"},
        {"1", "2", "3", "4", "5"},
        {"false", "true"},
        {"false", "true"},
        {"Auto", "Day", "Night"},
        {"1", "2", "3", "4", "5"},
        {"1", "2", "3", "4", "5"}
    };
    private SplitFlapPreferences preferences;
    private LinearLayout rows;
    private int focusedRow = -1;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        preferences = new SplitFlapPreferences(this);
        ScrollView scroll = new ScrollView(this);
        rows = new LinearLayout(this);
        rows.setOrientation(LinearLayout.VERTICAL);
        rows.setPadding(36, 24, 36, 24);
        TextView title = new TextView(this);
        title.setText("Split-Flap Drift settings");
        title.setTextSize(28);
        title.setPadding(0, 0, 0, 14);
        rows.addView(title);
        Button randomAll = button("Randomize clock style settings");
        randomAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SplitFlapOptions old = preferences.read();
                        preferences.write(
                                new SplitFlapOptions(
                                        old.format,
                                        old.palette,
                                        old.flipSpeed,
                                        old.date,
                                        old.seconds,
                                        old.lighting,
                                        old.scale,
                                        old.brightness,
                                        255));
                        renderRows();
                        rows.getChildAt(1).requestFocus();
                    }
                });
        rows.addView(randomAll);
        Button fixedAll = button("Use fixed settings");
        fixedAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SplitFlapOptions old = preferences.read();
                        preferences.write(
                                new SplitFlapOptions(
                                        old.format,
                                        old.palette,
                                        old.flipSpeed,
                                        old.date,
                                        old.seconds,
                                        old.lighting,
                                        old.scale,
                                        old.brightness,
                                        0));
                        renderRows();
                        rows.getChildAt(2).requestFocus();
                    }
                });
        rows.addView(fixedAll);
        scroll.addView(rows);
        setContentView(scroll);
        renderRows();
    }

    private void renderRows() {
        while (rows.getChildCount() > 3) rows.removeViewAt(3);
        SplitFlapOptions o = preferences.read();
        String[] active = {
            SplitFlapOptions.FORMATS[o.format],
            SplitFlapOptions.PALETTES[o.palette],
            Integer.toString(o.flipSpeed),
            Boolean.toString(o.date),
            Boolean.toString(o.seconds),
            SplitFlapOptions.LIGHTING[o.lighting],
            Integer.toString(o.scale),
            Integer.toString(o.brightness)
        };
        Button[] buttons = new Button[KEYS.length];
        for (int i = 0; i < KEYS.length; i++)
            buttons[i] = addRow(i, active[i], (o.randomMask & BITS[i]) != 0);
        android.widget.CheckBox clock = new android.widget.CheckBox(this);
        clock.setText("Show clock");
        clock.setTextSize(20);
        clock.setChecked(preferences.showClock());
        clock.setOnCheckedChangeListener(
                new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            android.widget.CompoundButton button, boolean checked) {
                        preferences.setShowClock(checked);
                    }
                });
        rows.addView(clock);
        if (focusedRow >= 0 && focusedRow < buttons.length) buttons[focusedRow].requestFocus();
    }

    private Button addRow(int index, String active, boolean random) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView label = new TextView(this);
        label.setText(LABELS[index]);
        label.setTextSize(20);
        row.addView(label, new LinearLayout.LayoutParams(0, -2, 1f));
        Button value = button(random ? "Random" : displayValue(index, active));
        value.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusedRow = index;
                        preferences.set(KEYS[index], nextValue(index, active, random));
                        renderRows();
                    }
                });
        row.addView(value, new LinearLayout.LayoutParams(320, -2));
        rows.addView(row, new LinearLayout.LayoutParams(-1, -2));
        return value;
    }

    private String nextValue(int index, String active, boolean random) {
        if (random) return VALUES[index][0];
        for (int i = 0; i < VALUES[index].length; i++)
            if (VALUES[index][i].equalsIgnoreCase(active))
                return i + 1 == VALUES[index].length ? "random" : VALUES[index][i + 1];
        return VALUES[index][0];
    }

    private String displayValue(int index, String value) {
        if (index == 3 || index == 4) return Boolean.parseBoolean(value) ? "On" : "Off";
        if (index == 2)
            return new String[] {"Slow", "Relaxed", "Balanced", "Quick", "Very quick"}
                    [Integer.parseInt(value) - 1];
        if (index == 6)
            return new String[] {"Very small", "Small", "Medium", "Large", "Very large"}
                    [Integer.parseInt(value) - 1];
        if (index == 7)
            return new String[] {"Dim", "Low", "Balanced", "Bright", "Very bright"}
                    [Integer.parseInt(value) - 1];
        return value;
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(18);
        b.setFocusable(true);
        return b;
    }
}
