package tests.painting;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.junit.runner.RunWith;
import org.tendiwa.drawing.TestCanvas;

import java.awt.*;

@RunWith(JukitoRunner.class)
public class AnimationSaveToFileDemo {
@Inject
TestCanvas canvas;

@org.junit.Test
void draw() {

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
