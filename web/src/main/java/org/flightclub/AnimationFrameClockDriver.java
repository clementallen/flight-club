package org.flightclub;

import org.teavm.jso.browser.Window;

/**
 * Browser frame loop.
 *
 * The simulation is not frame rate independent. FlyingDot and CameraMan
 * advance by a fixed amount per tick rather than by the delta they are
 * handed, so the tick rate is the simulation's speed - while XCGame.time,
 * Cloud and Variometer do use the delta and run on real seconds. The
 * desktop driver keeps the two in step by sleeping to a fixed frame time.
 *
 * requestAnimationFrame instead runs at the display's refresh rate, which
 * is faster than the model expects and varies from frame to frame. So
 * rather than tick once per animation frame, accumulate real time and tick
 * at the clock's own rate.
 */
public class AnimationFrameClockDriver implements Clock.Driver {

    /**
     * How much unspent time to carry, as a multiple of the frame time. A
     * backgrounded tab stops getting frames entirely; when it comes back
     * the debt is written off rather than run at speed.
     */
    private static final double MAX_CATCH_UP = 5;

    private boolean running = false;
    private Clock clock = null;
    private int pending = 0;
    private double lastTimestamp = -1;
    private double unspent = 0;

    @Override
    public void start(Clock clock) {
        this.clock = clock;
        running = true;
        lastTimestamp = -1;
        unspent = 0;
        schedule();
    }

    @Override
    public void stop() {
        running = false;
        if (pending != 0) {
            Window.cancelAnimationFrame(pending);
            pending = 0;
        }
    }

    private void schedule() {
        pending = Window.requestAnimationFrame(this::onFrame);
    }

    private void onFrame(double timestamp) {
        pending = 0;
        if (!running) {
            return;
        }
        schedule();

        if (lastTimestamp < 0) {
            lastTimestamp = timestamp;
            return;
        }

        double frameTime = clock.getSleepTime();
        unspent += timestamp - lastTimestamp;
        lastTimestamp = timestamp;

        if (unspent < frameTime) {
            return;
        }
        unspent -= frameTime;

        if (unspent > frameTime * MAX_CATCH_UP) {
            // came back from a stall. drop the backlog, and don't hand the
            // clock the whole gap as one enormous delta
            unspent = 0;
            clock.last = System.currentTimeMillis() - (long) frameTime;
        }

        // at most one tick per animation frame, so the delta the clock
        // works out stays close to the frame time
        clock.tick();
    }
}
