package org.flightclub;

import org.flightclub.compat.KeyEvent;

/**
 * compat.KeyEvent mirrors the codes of java.awt.event.KeyEvent, so this is
 * just a change of type.
 */
public final class AwtKeys {
    private AwtKeys() {
    }

    public static KeyEvent convert(java.awt.event.KeyEvent e) {
        return new KeyEvent(e.getID(), e.getKeyCode());
    }
}
