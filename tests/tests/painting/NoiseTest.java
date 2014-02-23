package tests.painting;

import com.google.common.collect.ObjectArrays;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.core.EnhancedPoint;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.DrawingPoint;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.noise.Noise;

import java.awt.*;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class NoiseTest {
@Inject
@Named("default")
TestCanvas canvas;

@Test
public void draw() {
	int width = 1280;
	int height = 1024;
	for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {
			int noise;
//			if (y > height /2) {
//			noise = (noise(x, y, 1) + noise(x, y, 7)) / 2;
//			} else {
			noise = noise(x, y, 7);
//			}
			EnhancedPoint point = new EnhancedPoint(x, y);
			if (noise > 145) {
				canvas.draw(point, DrawingPoint.withColor(new Color((int)(noise*1.2), (int)(noise*1.2), (int)(noise*0.2))));
			} else if (noise > 125) {
				canvas.draw(point, DrawingPoint.withColor(Color.GREEN));
			} else {
				canvas.draw(point, DrawingPoint.withColor(new Color((int)(noise*0.3), (int)(noise*0.4), (int)(noise*0.4))));
			}
		}
	}
	try {
		Thread.sleep(100000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}

public int noise(int x, int y, int octave) {
	return Noise.noise(
		((double) x) / 32,
		((double) y) / 32,
		octave
	);
}
}
