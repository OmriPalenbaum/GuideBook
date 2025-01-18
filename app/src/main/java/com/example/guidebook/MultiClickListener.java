package com.example.guidebook;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

//listener for detecting multiple clicks in a row (in this case, 5 clicks)
public abstract class MultiClickListener implements View.OnClickListener {
    private static final long CLICK_TIME_THRESHOLD = 2000; // Time in milliseconds (2 seconds)
    private static final int REQUIRED_CLICKS = 5; // Number of clicks to detect

    private int clickCount = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onClick(View view) {
        clickCount++;

        // Check if the required number of clicks is reached
        if (clickCount == REQUIRED_CLICKS) {
            onMultipleClicks(view);
            resetClickCount();
        } else {
            // Reset click count after the threshold time
            handler.removeCallbacks(resetRunnable);
            handler.postDelayed(resetRunnable, CLICK_TIME_THRESHOLD);
        }
    }

    private final Runnable resetRunnable = this::resetClickCount;

    private void resetClickCount() {
        clickCount = 0;
    }

    // Abstract method for handling the specific action
    public abstract void onMultipleClicks(View view);
}

