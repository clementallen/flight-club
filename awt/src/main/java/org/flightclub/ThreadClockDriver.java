package org.flightclub;

/**
 * Desktop frame loop - a thread that ticks the clock and sleeps out the
 * rest of each frame.
 */
public class ThreadClockDriver implements Clock.Driver, Runnable {

    private volatile boolean running = false;
    private Clock clock = null;

    @Override
    public void start(Clock clock) {
        this.clock = clock;
        running = true;
        new Thread(this).start();
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            long now = System.currentTimeMillis();

            clock.tick();

            long timeLeft = clock.getSleepTime() + now - System.currentTimeMillis();
            if (timeLeft > 0) {
                try {
                    Thread.sleep(timeLeft);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
