package com.jayeen.driver.utills;

/**
 * Created by pandian on 22/3/17.
 */

import android.os.Handler;
import android.widget.TextView;

import com.jayeen.driver.DriverApplication;

/**
 * Turns a TextView widget into a timer, with full stop-watch
 * functionality. Uses a Handler to update at a given time
 * interval.
 * <p>
 * The constructor takes a TextView and a time in milliseconds,
 * specifying the update interval. This implementation is
 * independent of the TextView widget.
 *
 * @author Euan Freeman
 * @see TextView
 * @see Handler
 */
public class StopWatchTextView implements Runnable {
    public enum TimerState {STOPPED, PAUSED, RUNNING}

    private TextView widget;
    private long updateInterval;
    private long time;
    private long startTime;
    private TimerState state;
    private Handler handler;
    public static long finalTime = 0;
    public static long finalSec = 0;

    public StopWatchTextView(TextView widget, long updateInterval) {
        this.widget = widget;
        this.updateInterval = updateInterval;
        time = 0;
        startTime = 0;
        state = TimerState.STOPPED;
        handler = new Handler();
    }

    @Override
    public void run() {
        long seconds = 0;
        long millis = 0;
        time = System.currentTimeMillis();
        millis = time - startTime;
        seconds = (long) (millis / 1000);
        DriverApplication.preferenceHelper.putTimerPause(time);
        this.finalTime = seconds / 60;
        this.finalSec = seconds % 60;
        widget.setText(String.format("%02d Min : %02d sec", seconds / 60, seconds % 60));
//        }

        if (state == TimerState.RUNNING) {
            handler.postDelayed(this, updateInterval);
        }
    }

    /**
     * Sets the timer into a running state and
     * initialises all time values.
     */
    public void start() {
//        **************
        if (DriverApplication.preferenceHelper.getTimerPause() > 0 && DriverApplication.preferenceHelper.getStartTime() > 0)
            startTime = System.currentTimeMillis() - (DriverApplication.preferenceHelper.getTimerPause() - DriverApplication.preferenceHelper.getStartTime());
        else
            startTime = time = System.currentTimeMillis();
        DriverApplication.preferenceHelper.putStartTime(startTime);
        DriverApplication.preferenceHelper.putIsWaitDialogVisible(true);
        state = TimerState.RUNNING;
        handler.post(this);
    }

    /**
     * Resets the timer.
     */
    public void reset() {
        start();
    }

    /**
     * Puts the timer into a paused state.
     */
    public void pause() {
        handler.removeCallbacks(this);

        state = TimerState.PAUSED;
    }

    /**
     * Resumes the timer.
     */
    public void resume(TextView txt) {
        this.widget = txt;
        finalTime = 0;
        finalSec = 0;
        state = TimerState.RUNNING;
        startTime = System.currentTimeMillis() - (time - startTime);
        DriverApplication.preferenceHelper.putStartTime(startTime);
        DriverApplication.preferenceHelper.putIsWaitDialogVisible(true);
        handler.post(this);
    }

    /**
     * Stops the timer and resets all time values.
     */
    public void stop() {
        handler.removeCallbacks(this);
        finalTime = 0;
        finalSec = 0;
        time = 0;
        startTime = 0;
        state = TimerState.STOPPED;
        DriverApplication.preferenceHelper.putTimerPause(time);
        DriverApplication.preferenceHelper.putStartTime(startTime);
        DriverApplication.preferenceHelper.putIsWaitDialogVisible(false);
        widget.setText("00 Min : 00 Sec");
    }

    /**
     * Returns the interval (in ms) at which
     * the timer widget is updated.
     *
     * @return Time in milliseconds
     */
    public long getUpdateInterval() {
        return updateInterval;
    }

    /**
     * Sets the update interval for the
     * timer widget.
     *
     * @param updateInterval Interval in milliseconds
     */
    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * Returns the current state of the stop-watch.
     *
     * @return State of stop-watch
     */
    public TimerState getState() {
        return state;
    }
}
