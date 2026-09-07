package org.flightclub;

import org.flightclub.compat.KeyEvent;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;

/**
 * Browser entry point.
 */
public class XCGameWeb {

    private XCGameWeb() {
    }

    public static void main(String[] args) {
        HTMLDocument document = HTMLDocument.current();
        HTMLCanvasElement canvas = (HTMLCanvasElement) document.getElementById("flightclub");

        final XCGame app = new XCGame();

        WebCanvas panel = new WebCanvas(app, canvas);
        panel.init();

        app.init(new WebInterface(canvas));
        app.clock.setDriver(new AnimationFrameClockDriver());

        Window.current().listenKeyDown(e -> {
            KeyEvent event = WebKeys.convert(e, KeyEvent.KEY_PRESSED);
            if (event != null) {
                app.eventManager.addEvent(event);
                e.preventDefault();
            }
        });

        Window.current().listenKeyUp(e -> {
            KeyEvent event = WebKeys.convert(e, KeyEvent.KEY_RELEASED);
            if (event != null) {
                app.eventManager.addEvent(event);
                e.preventDefault();
            }
        });

        app.start();
    }
}
