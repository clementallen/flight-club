package org.flightclub;

import org.flightclub.compat.KeyEvent;
import org.teavm.jso.dom.events.KeyboardEvent;

/**
 * Maps DOM KeyboardEvent.code values onto the game's key codes.
 */
final class WebKeys {
    private WebKeys() {
    }

    /**
     * @return null when the key is not one the game uses
     */
    static KeyEvent convert(KeyboardEvent e, int id) {
        int code = keyCode(e.getCode());
        if (code == 0) {
            return null;
        }
        return new KeyEvent(id, code);
    }

    private static int keyCode(String domCode) {
        if (domCode == null) {
            return 0;
        }
        switch (domCode) {
            case "Space": return KeyEvent.VK_SPACE;

            case "ArrowLeft": return KeyEvent.VK_LEFT;
            case "ArrowUp": return KeyEvent.VK_UP;
            case "ArrowRight": return KeyEvent.VK_RIGHT;
            case "ArrowDown": return KeyEvent.VK_DOWN;

            case "Digit1": return KeyEvent.VK_1;
            case "Digit2": return KeyEvent.VK_2;
            case "Digit3": return KeyEvent.VK_3;
            case "Digit4": return KeyEvent.VK_4;

            case "KeyA": return KeyEvent.VK_A;
            case "KeyD": return KeyEvent.VK_D;
            case "KeyG": return KeyEvent.VK_G;
            case "KeyH": return KeyEvent.VK_H;
            case "KeyK": return KeyEvent.VK_K;
            case "KeyL": return KeyEvent.VK_L;
            case "KeyM": return KeyEvent.VK_M;
            case "KeyN": return KeyEvent.VK_N;
            case "KeyP": return KeyEvent.VK_P;
            case "KeyQ": return KeyEvent.VK_Q;
            case "KeyS": return KeyEvent.VK_S;
            case "KeyW": return KeyEvent.VK_W;
            case "KeyY": return KeyEvent.VK_Y;

            default: return 0;
        }
    }
}
