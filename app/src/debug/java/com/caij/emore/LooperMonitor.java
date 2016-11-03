package com.caij.emore;

import android.os.SystemClock;
import android.util.Printer;

class LooperMonitor implements Printer {

    private static final int DEFAULT_BLOCK_THRESHOLD_MILLIS = 3000;

    private long mBlockThresholdMillis = DEFAULT_BLOCK_THRESHOLD_MILLIS;
    private long mStartTimestamp = 0;
    private long mStartThreadTimestamp = 0;
    private BlockListener mBlockListener = null;
    private boolean mPrintingStarted = false;

    public interface BlockListener {
        void onBlockEvent(long realStartTime,
                          long realTimeEnd,
                          long threadTimeStart,
                          long threadTimeEnd);
    }

    public LooperMonitor(BlockListener blockListener, long blockThresholdMillis) {
        if (blockListener == null) {
            throw new IllegalArgumentException("blockListener should not be null.");
        }
        mBlockListener = blockListener;
        mBlockThresholdMillis = blockThresholdMillis;
    }

    @Override
    public void println(String x) {
        if (!mPrintingStarted) {
            mStartTimestamp = System.currentTimeMillis();
            mStartThreadTimestamp = SystemClock.currentThreadTimeMillis();
            mPrintingStarted = true;
        } else {
            final long endTime = System.currentTimeMillis();
            mPrintingStarted = false;
            if (isBlock(endTime)) {
                notifyBlockEvent(endTime);
            }
        }
    }

    private boolean isBlock(long endTime) {
        return endTime - mStartTimestamp > mBlockThresholdMillis;
    }

    private void notifyBlockEvent(final long endTime) {
        final long startTime = mStartTimestamp;
        final long startThreadTime = mStartThreadTimestamp;
        final long endThreadTime = SystemClock.currentThreadTimeMillis();
        mBlockListener.onBlockEvent(startTime, endTime, startThreadTime, endThreadTime);
    }
}
