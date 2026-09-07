package org.flightclub;

import org.teavm.jso.browser.Window;

/**
 * Browser frame loop. The browser decides the frame rate, so unlike the
 * desktop driver there is nothing to sleep off.
 */
public class AnimationFrameClockDriver implements Clock.Driver {

    private boolean running = false;
    private Clock clock = null;
    private int pending = 0;

    @Override
    public void start(Clock clock) {
        this.clock = clock;
        running = true;
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
        pending = Window.requestAnimationFrame(timestamp -> {
            pending = 0;
            if (!running) {
                return;
            }
            clock.tick();
            schedule();
        });
    }
}
