package tendiwa.drawing;

import java.awt.Color;
import java.awt.Rectangle;

public abstract class DrawingAlgorithm<T> {
	TestCanvas canvas;

	public abstract void draw(T shape);
	protected void drawPoint(int x, int y, Color color) {
		if (canvas.scale == 1) {
			canvas.image.setRGB(x, y, color.getRGB());
		} else {
			canvas.graphics.setColor(color);
			canvas.graphics.fillRect(
				x * canvas.scale,
				y * canvas.scale,
				canvas.scale,
				canvas.scale);
		}
	}
	protected void drawRectangle(Rectangle r, Color color) {
		canvas.graphics.setColor(color);
		canvas.graphics.fillRect(
			r.x * canvas.scale,
			r.y * canvas.scale,
			r.width * canvas.scale,
			r.height * canvas.scale);
	}
	void setCanvas(TestCanvas canvas) {
		this.canvas = canvas;
	}
	void setLayer(TestCanvas.Layer zIndex) {
		canvas.graphics = zIndex.graphics;
		canvas.image = zIndex.image;
	}
	protected void drawObject(Object shape) {
		canvas.draw(shape, canvas.currentLayer);
	}
}
