/**
 This code is covered by the GNU General Public License
 detailed at http://www.gnu.org/copyleft/gpl.html

 Flight Club docs located at http://www.danb.dircon.co.uk/hg/hg.htm
 Dan Burton , Nov 2001
 */

package org.flightclub;

import java.util.Vector;

/**
 * Clock ticks, driven by whatever frame loop the platform provides
 */
public class Clock {

    public interface Observer {
        public void tick(float delta);
    }

    /**
     * the platform's frame loop - a thread on the desktop, animation
     * frames in the browser. It calls tick() once per frame.
     */
    public interface Driver {
        void start(Clock clock);
        void stop();
    }

    final int sleepTime;
    final Vector<Observer> observers = new Vector<>();
    public long last = 0;

    boolean paused = false;
    private Driver driver = null;

    Clock(int t) {
        sleepTime = t;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    void addObserver(Observer observer) {
        observers.addElement(observer);
    }

    void removeObserver(Observer observer) {
        observers.removeElement(observer);
    }

    public void start() {
        last = System.currentTimeMillis();
        if (driver != null) {
            driver.start(this);
        }
    }

    public void stop() {
        if (driver != null) {
            driver.stop();
        }
    }

    /**
     * called by the driver once per frame
     */
    public void tick() {
        long now = System.currentTimeMillis();
        float delta = (now - last) / 1000.0f;
        last = now;

        for (int i = 0; i < observers.size(); i++) {
            /*
                hack - when paused still tick the modelviewer so
                we can change our POV and unpause
            */
            if (i == 0 || !paused) {
                Observer c = observers.elementAt(i);
                c.tick(delta);
            }
        }
    }
}
