package tests;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.TestCanvas;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class WorldTest {
@Inject
@Named("default")
TestCanvas canvas;

@Test
public void world() {
	canvas.draw(new Rectangle(0, 0, 15, 17));

}
}
