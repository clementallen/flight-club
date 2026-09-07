package org.flightclub.compat;

import org.teavm.jso.canvas.CanvasRenderingContext2D;

/**
 * Draws the model onto an HTML canvas.
 */
public class CanvasGraphics implements Graphics {

    private final CanvasRenderingContext2D ctx;

    public CanvasGraphics(CanvasRenderingContext2D ctx) {
        this.ctx = ctx;
    }

    @Override
    public void setColor(Color color) {
        String css = "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
        ctx.setFillStyle(css);
        ctx.setStrokeStyle(css);
    }

    @Override
    public void setFont(Font font) {
        StringBuilder css = new StringBuilder();
        if ((font.getStyle() & Font.ITALIC) != 0) {
            css.append("italic ");
        }
        if ((font.getStyle() & Font.BOLD) != 0) {
            css.append("bold ");
        }
        css.append(font.getSize()).append("px ").append(cssFamily(font.getName()));
        ctx.setFont(css.toString());
    }

    /**
     * awt's logical font names mean nothing to a browser
     */
    private static String cssFamily(String name) {
        switch (name) {
            case "SansSerif":
            case "Dialog":
            case "DialogInput":
                return "sans-serif";
            case "Serif":
                return "serif";
            case "Monospaced":
                return "monospace";
            default:
                return name;
        }
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        // half a pixel keeps a one pixel line on the pixel grid rather than
        // smeared over two rows
        ctx.beginPath();
        ctx.moveTo(x1 + 0.5, y1 + 0.5);
        ctx.lineTo(x2 + 0.5, y2 + 0.5);
        ctx.stroke();
    }

    @Override
    public void drawString(String str, int x, int y) {
        ctx.fillText(str, x, y);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        if (nPoints <= 0) {
            return;
        }
        ctx.beginPath();
        ctx.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            ctx.lineTo(xPoints[i], yPoints[i]);
        }
        ctx.closePath();
        ctx.fill();
    }

    @Override
    public void fillCircle(int x, int y, int diameter) {
        // awt takes the top left of the bounding box, canvas takes the centre
        double r = diameter / 2.0;
        ctx.beginPath();
        ctx.arc(x + r, y + r, r, 0, Math.PI * 2);
        ctx.fill();
    }
}
