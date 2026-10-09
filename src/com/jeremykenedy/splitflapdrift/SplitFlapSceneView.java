package com.jeremykenedy.splitflapdrift;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

final class SplitFlapSceneView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable frame =
            new Runnable() {
                @Override
                public void run() {
                    if (!running) return;
                    invalidate();
                    handler.postDelayed(this, 33L);
                }
            };
    private SplitFlapOptions options;
    private boolean running;
    private final long started = System.currentTimeMillis();
    private long changedAt;
    private String lastDigits = "";

    SplitFlapSceneView(Context context, SplitFlapOptions options) {
        super(context);
        this.options = options;
        setFocusable(true);
    }

    void configure(SplitFlapOptions value) {
        options = value;
        invalidate();
    }

    void startAnimation() {
        if (!running) {
            running = true;
            handler.post(frame);
        }
    }

    void stopAnimation() {
        running = false;
        handler.removeCallbacks(frame);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long now = System.currentTimeMillis();
        float w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        boolean night = options.lighting == 2 || (options.lighting == 0 && isNight());
        int bg = night ? Color.rgb(7, 10, 15) : Color.rgb(27, 31, 36);
        canvas.drawColor(bg);
        int glow = night ? Color.rgb(26, 34, 48) : Color.rgb(67, 70, 73);
        paint.setShader(
                new RadialGradient(
                        w * .5f,
                        h * .48f,
                        h * .8f,
                        new int[] {glow, bg},
                        null,
                        Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        float scale = Math.min(w / 1920f, h / 1080f) * (.72f + options.scale * .08f);
        float digitW = 174f * scale, digitH = 280f * scale, gap = 18f * scale;
        float colonW = 42f * scale;
        String clock = time(now, options.format == 0, options.seconds);
        String digits = clock.replace(":", "");
        if (!digits.equals(lastDigits)) {
            lastDigits = digits;
            changedAt = now;
        }
        int count = digits.length();
        float width =
                count * digitW + (count - 1) * gap + colonW * (options.seconds ? 2 : 1) + gap * 2;
        float paneW = width + 116f * scale;
        float paneH = digitH + (options.date ? 126f : 72f) * scale;
        float driftX = (float) Math.sin((now - started) / 9000.0) * w * .018f;
        float driftY = (float) Math.sin((now - started) / 13000.0) * h * .014f;
        float left = (w - paneW) / 2f + driftX;
        float top = (h - paneH) / 2f + driftY;
        RectF pane = new RectF(left, top, left + paneW, top + paneH);
        int panel =
                options.palette == 1
                        ? Color.rgb(222, 215, 194)
                        : options.palette == 2 ? Color.rgb(10, 28, 40) : Color.rgb(22, 25, 29);
        int ink =
                options.palette == 1
                        ? Color.rgb(27, 30, 31)
                        : options.palette == 2
                                ? Color.rgb(190, 220, 230)
                                : Color.rgb(237, 232, 218);
        float light = (night ? .62f : 1f) * (.45f + options.brightness * .11f);
        paint.setColor(Color.argb(130, 0, 0, 0));
        canvas.drawRoundRect(
                new RectF(
                        pane.left + 9 * scale,
                        pane.top + 16 * scale,
                        pane.right + 9 * scale,
                        pane.bottom + 16 * scale),
                22 * scale,
                22 * scale,
                paint);
        paint.setShader(
                new LinearGradient(
                        0,
                        pane.top,
                        0,
                        pane.bottom,
                        new int[] {tint(panel, light), panel, darken(panel)},
                        null,
                        Shader.TileMode.CLAMP));
        canvas.drawRoundRect(pane, 22 * scale, 22 * scale, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.2f * scale);
        paint.setColor(0x55ffffff);
        canvas.drawRoundRect(pane, 22 * scale, 22 * scale, paint);
        paint.setStyle(Paint.Style.FILL);
        float x = left + (paneW - width) / 2f;
        float y = top + 32 * scale;
        int digitIndex = 0;
        for (int i = 0; i < clock.length(); i++) {
            char ch = clock.charAt(i);
            if (ch == ':') {
                paint.setColor(ink);
                paint.setAlpha(145);
                canvas.drawCircle(x + colonW / 2f, y + digitH * .38f, 5 * scale, paint);
                canvas.drawCircle(x + colonW / 2f, y + digitH * .64f, 5 * scale, paint);
                paint.setAlpha(255);
                x += colonW + gap;
                continue;
            }
            drawDigit(canvas, x, y, digitW, digitH, ch, ink, panel, scale, now, digitIndex++);
            x += digitW + gap;
        }
        if (options.date) {
            paint.setColor(ink);
            paint.setAlpha(190);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            paint.setTextSize(20 * scale);
            String date =
                    new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
                            .format(new Date(now))
                            .toUpperCase(Locale.getDefault());
            canvas.drawText(date, pane.centerX(), pane.bottom - 38 * scale, paint);
        }
        float sweep = (float) ((now / 5200.0) % 1.0);
        paint.setShader(
                new LinearGradient(
                        left + paneW * (sweep - .22f),
                        0,
                        left + paneW * (sweep + .02f),
                        0,
                        new int[] {0x00ffffff, 0x13ffffff, 0x00ffffff},
                        null,
                        Shader.TileMode.CLAMP));
        canvas.drawRoundRect(pane, 22 * scale, 22 * scale, paint);
        paint.setShader(null);
    }

    private void drawDigit(
            Canvas canvas,
            float x,
            float y,
            float w,
            float h,
            char digit,
            int ink,
            int panel,
            float scale,
            long now,
            int index) {
        RectF tile = new RectF(x, y, x + w, y + h);
        paint.setColor(darken(panel));
        canvas.drawRoundRect(tile, 9 * scale, 9 * scale, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(scale);
        paint.setColor(0x77000000);
        canvas.drawRoundRect(tile, 9 * scale, 9 * scale, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x1effffff);
        canvas.drawRect(x + 5 * scale, y + 5 * scale, x + w - 5 * scale, y + h / 2 - 1, paint);
        paint.setColor(0x99000000);
        canvas.drawRect(x + 5 * scale, y + h / 2, x + w - 5 * scale, y + h / 2 + 2 * scale, paint);
        paint.setColor(ink);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        paint.setTextSize(h * .82f);
        float baseline = y + h * .79f;
        canvas.save();
        canvas.clipRect(x, y, x + w, y + h / 2 + 1);
        canvas.drawText(String.valueOf(digit), x + w / 2, baseline, paint);
        canvas.restore();
        canvas.save();
        canvas.clipRect(x, y + h / 2, x + w, y + h);
        canvas.drawText(String.valueOf(digit), x + w / 2, baseline, paint);
        canvas.restore();
        float duration = 600f - options.flipSpeed * 85f;
        float progress = (now - changedAt) / duration;
        if (progress >= 0 && progress < 1 && index == 0) {
            canvas.save();
            canvas.clipRect(x, y, x + w, y + h / 2);
            canvas.rotate(progress * 180, x + w / 2, y + h / 2);
            paint.setColor(darken(panel));
            canvas.drawRect(x, y, x + w, y + h / 2, paint);
            paint.setColor(ink);
            canvas.drawText(String.valueOf(digit), x + w / 2, baseline, paint);
            canvas.restore();
            paint.setColor(0x50000000);
            canvas.drawRect(
                    x + 4 * scale, y + h / 2 - scale, x + w - 4 * scale, y + h / 2 + scale, paint);
        }
    }

    private static boolean isNight() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return hour < 7 || hour >= 19;
    }

    private static int tint(int color, float amount) {
        return Color.rgb(
                (int) (Color.red(color) * amount),
                (int) (Color.green(color) * amount),
                (int) (Color.blue(color) * amount));
    }

    private static int darken(int color) {
        return Color.rgb(
                Color.red(color) * 3 / 5, Color.green(color) * 3 / 5, Color.blue(color) * 3 / 5);
    }

    private static String time(long millis, boolean twelveHour, boolean seconds) {
        String pattern =
                twelveHour ? (seconds ? "h:mm:ss" : "h:mm") : (seconds ? "HH:mm:ss" : "HH:mm");
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date(millis));
    }
}
