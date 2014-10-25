package org.tendiwa.demos;

import com.google.inject.Inject;
import org.tendiwa.drawing.GifBuilder;
import org.tendiwa.drawing.GifBuilderFactory;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Rectangle;

import static java.awt.Color.*;

public class AnimationSaveToFileDemo implements Runnable {
	TestCanvas canvas = new TestCanvas(1, 100, 100);
	@Inject
	GifBuilderFactory gifBuilderFactory;

	public static void main(String[] args) {
		Demos.run(AnimationSaveToFileDemo.class);
	}

	@Override
	public void run() {
		GifBuilder gifBuilder = gifBuilderFactory.create(canvas, 5);
		String home = System.getProperty("user.home");

		int x = 0, y = 0;
		for (int i = 0; i < 3; i++) {
			canvas.drawRectangle(new Rectangle(x, y, 10, 24), RED);
			x++;
			y++;
			gifBuilder.saveFrame();
		}
		gifBuilder.saveAnimation(home + "/test.gif");
		canvas.clear();
		for (int i = 0; i < 8; i++) {
			canvas.drawRectangle(new Rectangle(x, y, 10, 24), BLUE);
			x++;
			y++;
			gifBuilder.saveFrame();
		}
		gifBuilder.saveAnimation(home + "/test2.gif");
	}
}
