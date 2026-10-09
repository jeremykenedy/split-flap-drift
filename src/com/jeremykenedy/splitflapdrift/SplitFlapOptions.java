package com.jeremykenedy.splitflapdrift;

import java.util.Random;

final class SplitFlapOptions {
    static final String[] FORMATS = {"12 hour", "24 hour"};
    static final String[] PALETTES = {"Midnight", "Ivory", "Ocean"};
    static final String[] LIGHTING = {"Auto", "Day", "Night"};
    final int format, palette, flipSpeed, lighting, scale, brightness, randomMask;
    final boolean date, seconds;

    SplitFlapOptions(
            int format,
            int palette,
            int flipSpeed,
            boolean date,
            boolean seconds,
            int lighting,
            int scale,
            int brightness,
            int randomMask) {
        this.format = clamp(format, 0, FORMATS.length - 1);
        this.palette = clamp(palette, 0, PALETTES.length - 1);
        this.flipSpeed = clamp(flipSpeed, 1, 5);
        this.date = date;
        this.seconds = seconds;
        this.lighting = clamp(lighting, 0, LIGHTING.length - 1);
        this.scale = clamp(scale, 1, 5);
        this.brightness = clamp(brightness, 1, 5);
        this.randomMask = randomMask & 255;
    }

    static SplitFlapOptions defaults() {
        return new SplitFlapOptions(0, 0, 3, false, false, 0, 4, 4, 0);
    }

    SplitFlapOptions resolve(long seed) {
        if (randomMask == 0) return this;
        Random random = new Random(seed);
        return new SplitFlapOptions(
                pick(random, 1, format, 2),
                pick(random, 2, palette, 3),
                pick(random, 4, flipSpeed - 1, 5) + 1,
                (randomMask & 8) == 0 ? date : random.nextBoolean(),
                (randomMask & 16) == 0 ? seconds : random.nextBoolean(),
                pick(random, 32, lighting, 3),
                pick(random, 64, scale - 1, 5) + 1,
                pick(random, 128, brightness - 1, 5) + 1,
                0);
    }

    private int pick(Random random, int bit, int current, int count) {
        return (randomMask & bit) == 0 ? current : random.nextInt(count);
    }

    static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
