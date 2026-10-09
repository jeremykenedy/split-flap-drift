package com.jeremykenedy.splitflapdrift;

public final class SplitFlapOptionsTest {
    public static void main(String[] args) {
        defaultsStayReadable();
        invalidRangesAreClamped();
        randomizationIsStableAndScoped();
        System.out.println("Split-Flap Drift option tests passed");
    }

    private static void defaultsStayReadable() {
        SplitFlapOptions o = SplitFlapOptions.defaults();
        require(o.format == 0 && o.palette == 0 && !o.seconds && !o.date, "Readable default clock");
        require(
                o.lighting == 0 && o.brightness == 4 && o.randomMask == 0,
                "Stable auto-lit default");
    }

    private static void invalidRangesAreClamped() {
        SplitFlapOptions o = new SplitFlapOptions(-1, 99, 0, false, true, 8, 8, 8, 511);
        require(o.format == 0 && o.palette == 2 && o.flipSpeed == 1, "Choice and speed bounds");
        require(o.lighting == 2 && o.scale == 5 && o.brightness == 5, "Display bounds");
        require(o.randomMask == 255, "Mask only contains supported settings");
    }

    private static void randomizationIsStableAndScoped() {
        SplitFlapOptions o = new SplitFlapOptions(1, 2, 4, true, false, 1, 3, 2, 1 | 4 | 128);
        SplitFlapOptions first = o.resolve(8472), second = o.resolve(8472);
        require(first.palette == 2 && first.date, "Unselected settings stay fixed");
        require(
                first.format == second.format
                        && first.flipSpeed == second.flipSpeed
                        && first.brightness == second.brightness,
                "A showing uses repeatable choices");
        require(
                first.format >= 0
                        && first.format < 2
                        && first.flipSpeed >= 1
                        && first.flipSpeed <= 5
                        && first.brightness >= 1
                        && first.brightness <= 5,
                "Random values stay valid");
        require(first.randomMask == 0, "Resolved settings cannot randomize again mid-showing");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
