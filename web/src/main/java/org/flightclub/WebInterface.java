package org.flightclub;

import org.teavm.jso.dom.html.HTMLAudioElement;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;

class WebInterface implements Interface {

    private final HTMLCanvasElement canvas;

    WebInterface(HTMLCanvasElement canvas) {
        this.canvas = canvas;
    }

    @Override
    public int getWidth() {
        return canvas.getWidth();
    }

    @Override
    public int getHeight() {
        return canvas.getHeight();
    }

    @Override
    public void play(String s) {
        HTMLAudioElement audio = (HTMLAudioElement) HTMLDocument.current().createElement("audio");
        audio.setSrc(s);
        audio.play();
    }
}
