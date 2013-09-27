package tests.painting;

import java.awt.Rectangle;

import tendiwa.drawing.TestCanvas;

public class AnimationSaveToFileDemo {
	public static void main(String[] args) {
		TestCanvas canvas = TestCanvas.builder().setScale(3).setVisiblilty(false).setFps(56).build();
		int x = 0, y = 0;
		for (int i = 0; i < 3; i++) {
			canvas.draw(new Rectangle(x, y, 10, 24));
			x++;
			y++;
			canvas.saveFrame();
		}
		canvas.saveAnimation("/home/suseika/test.gif");
		for (int i = 0; i < 8; i++) {
			canvas.draw(new Rectangle(x, y, 10, 24));
			x++;
			y++;
			canvas.saveFrame();
		}
		canvas.saveAnimation("/home/suseika/test2.gif");
	}
}
