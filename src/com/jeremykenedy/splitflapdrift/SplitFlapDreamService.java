package com.jeremykenedy.splitflapdrift;

import android.service.dreams.DreamService;

public final class SplitFlapDreamService extends DreamService {
    private SplitFlapSceneView scene;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);
        setFullscreen(true);
        setScreenBright(false);
        scene =
                new SplitFlapSceneView(
                        this, new SplitFlapPreferences(this).read().resolve(System.nanoTime()));
        setContentView(scene);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        if (scene != null) {
            scene.configure(new SplitFlapPreferences(this).read().resolve(System.nanoTime()));
            scene.startAnimation();
        }
    }

    @Override
    public void onDreamingStopped() {
        stopScene();
        super.onDreamingStopped();
    }

    @Override
    public void onDetachedFromWindow() {
        stopScene();
        scene = null;
        super.onDetachedFromWindow();
    }

    private void stopScene() {
        if (scene != null) {
            scene.stopAnimation();
        }
    }
}
