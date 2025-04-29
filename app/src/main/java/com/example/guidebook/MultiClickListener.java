package com.example.guidebook;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

// Listener for detecting multiple consecutive clicks
public abstract class MultiClickListener implements View.OnClickListener {

    private static final long CLICK_TIME_THRESHOLD = 2000; // 2 seconds time threshold
    private static final int REQUIRED_CLICKS = 5; // Number of clicks to detect

    private int clickCount = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onClick(View view) {
        clickCount++;

        // Trigger action if the required number of clicks is reached
        if (clickCount == REQUIRED_CLICKS) {
            onMultipleClicks(view);
            resetClickCount();
        } else {
            // Reset click count if threshold time passes
            handler.removeCallbacks(resetRunnable);
            handler.postDelayed(resetRunnable, CLICK_TIME_THRESHOLD);
        }
    }

    // Runnable to reset click count
    private final Runnable resetRunnable = this::resetClickCount;

    // Reset click count to 0
    private void resetClickCount() {
        clickCount = 0;
    }

    // Abstract method for handling the action after multiple clicks
    public abstract void onMultipleClicks(View view);
}