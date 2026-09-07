package org.flightclub;

import org.flightclub.compat.CanvasGraphics;
import org.flightclub.compat.Color;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.html.HTMLCanvasElement;

/**
 * Browser counterpart of ModelCanvas - draws the world and lets a drag on
 * the canvas move the camera.
 */
class WebCanvas implements Clock.Observer {

    private static final Color BACK_COLOR = Color.WHITE;

    private final XCGame app;
    private final HTMLCanvasElement canvas;
    private final CanvasRenderingContext2D ctx;
    private final CanvasGraphics graphics;
    private final MouseTracker mouseTracker = new MouseTracker();

    WebCanvas(XCGame app, HTMLCanvasElement canvas) {
        this.app = app;
        this.canvas = canvas;
        this.ctx = (CanvasRenderingContext2D) canvas.getContext("2d");
        this.graphics = new CanvasGraphics(ctx);

        app.clock.addObserver(this);
    }

    void init() {
        canvas.listenMouseDown(e -> mouseTracker.pressed(e.getOffsetX(), e.getOffsetY()));
        canvas.listenMouseUp(e -> mouseTracker.released());
        canvas.listenMouseOut(e -> mouseTracker.released());
        canvas.listenMouseMove(e -> {
            if (mouseTracker.isDragging()) {
                mouseTracker.dragged(e.getOffsetX(), e.getOffsetY());
            }
        });
    }

    @Override
    public void tick(float delta) {
        if (mouseTracker.isDragging()) {
            float dtheta = 0;
            float dz = 0;
            float unitStep = (float) Math.PI * delta / 8;//4 seconds to 90 - sloow!

            if (mouseTracker.getDeltaX() > 20) {
                dtheta = -unitStep;
            }

            if (mouseTracker.getDeltaX() < -20) {
                dtheta = unitStep;
            }

            if (mouseTracker.getDeltaY() > 20) {
                dz = delta / 4;
            }

            if (mouseTracker.getDeltaY() < -20) {
                dz = -delta / 4;
            }

            app.cameraMan.rotateEyeAboutFocus(-dtheta);
            app.cameraMan.translateZ(-dz);
        }

        draw();
    }

    private void draw() {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        graphics.setColor(BACK_COLOR);
        ctx.fillRect(0, 0, width, height);

        app.draw(graphics, width, height);
    }
}
