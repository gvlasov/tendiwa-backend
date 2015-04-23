package org.tendiwa.drawing.extensions;

import org.apache.log4j.Logger;
import org.tendiwa.drawing.Animation;
import org.tendiwa.drawing.Frame;
import org.tendiwa.drawing.GifBuilder;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Dimension;

import java.io.File;

public final class Gif implements Animation {
	private final GifBuilder gifBuilder;

	public Gif(
		Dimension size,
		int fps,
		Collection<Frame> frames
		) {
		this.gifBuilder = new GifBuilder(
			new TestCanvas(1, size.width(), size.height()),
			fps,
			Logger.getRootLogger()
		);
	}

	@Override
	public void save(File file) {

	}
}
