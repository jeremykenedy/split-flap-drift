package com.jeremykenedy.splitflapdrift;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class SplitFlapOptionsCoverageTest {
    @Test
    public void defaultsAndBoundsAreStable() {
        SplitFlapOptions defaults = SplitFlapOptions.defaults();
        assertEquals(0, defaults.format);
        assertEquals(0, defaults.palette);
        assertEquals(3, defaults.flipSpeed);
        assertEquals(4, defaults.scale);
        assertEquals(4, defaults.brightness);
        assertEquals(0, defaults.randomMask);
        assertEquals(defaults, defaults.resolve(17));

        SplitFlapOptions bounded = new SplitFlapOptions(-1, 9, -2, false, false, 8, 8, 8, 511);
        assertEquals(0, bounded.format);
        assertEquals(2, bounded.palette);
        assertEquals(1, bounded.flipSpeed);
        assertEquals(2, bounded.lighting);
        assertEquals(5, bounded.scale);
        assertEquals(5, bounded.brightness);
        assertEquals(255, bounded.randomMask);
        assertEquals(1, SplitFlapOptions.clamp(-4, 1, 5));
        assertEquals(5, SplitFlapOptions.clamp(9, 1, 5));
        assertEquals(3, SplitFlapOptions.clamp(3, 1, 5));
    }

    @Test
    public void randomSelectionsResolveOnceWithinTheirRanges() {
        SplitFlapOptions all = new SplitFlapOptions(0, 0, 1, false, false, 0, 1, 1, 255);
        SplitFlapOptions resolved = all.resolve(8192);
        assertEquals(0, resolved.randomMask);
        assertTrue(resolved.format >= 0 && resolved.format < 2);
        assertTrue(resolved.palette >= 0 && resolved.palette < 3);
        assertTrue(resolved.flipSpeed >= 1 && resolved.flipSpeed <= 5);
        assertTrue(resolved.lighting >= 0 && resolved.lighting < 3);
        assertTrue(resolved.scale >= 1 && resolved.scale <= 5);
        assertTrue(resolved.brightness >= 1 && resolved.brightness <= 5);
        assertNotSame(all, resolved);
        assertEquals(resolved.format, all.resolve(8192).format);
        assertEquals(resolved.date, all.resolve(8192).date);
        assertEquals(resolved.seconds, all.resolve(8192).seconds);
    }

    @Test
    public void fixedValuesSurviveRandomResolution() {
        SplitFlapOptions fixed =
                new SplitFlapOptions(1, 2, 5, true, false, 2, 3, 2, 1 | 4 | 64 | 128);
        SplitFlapOptions resolved = fixed.resolve(912);
        assertEquals(2, resolved.palette);
        assertEquals(true, resolved.date);
        assertEquals(false, resolved.seconds);
        assertEquals(2, resolved.lighting);
        assertEquals(0, resolved.randomMask);
    }
}
