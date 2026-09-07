package org.flightclub.compat;

/**
 * Platform neutral key event.
 *
 * Key codes and event ids mirror the values used by java.awt.event.KeyEvent so
 * that the AWT front end can pass its codes straight through.
 */
public class KeyEvent {
    public static final int KEY_PRESSED = 401;
    public static final int KEY_RELEASED = 402;

    public static final int VK_SPACE = 32;

    public static final int VK_LEFT = 37;
    public static final int VK_UP = 38;
    public static final int VK_RIGHT = 39;
    public static final int VK_DOWN = 40;

    public static final int VK_1 = 49;
    public static final int VK_2 = 50;
    public static final int VK_3 = 51;
    public static final int VK_4 = 52;

    public static final int VK_A = 65;
    public static final int VK_D = 68;
    public static final int VK_G = 71;
    public static final int VK_H = 72;
    public static final int VK_K = 75;
    public static final int VK_L = 76;
    public static final int VK_M = 77;
    public static final int VK_N = 78;
    public static final int VK_P = 80;
    public static final int VK_Q = 81;
    public static final int VK_S = 83;
    public static final int VK_W = 87;
    public static final int VK_Y = 89;

    private final int id;
    private final int keyCode;

    public KeyEvent(int id, int keyCode) {
        this.id = id;
        this.keyCode = keyCode;
    }

    public int getID() {
        return id;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
