package org.tendiwa.drawing.extensions;

import com.google.common.io.Files;
import org.tendiwa.core.meta.GifSequenceWriter;
import org.tendiwa.drawing.AnimationFrame;
import org.tendiwa.drawing.AwtCanvas;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.files.TempFile;
import org.tendiwa.geometry.Dimension;

import javax.imageio.stream.FileImageOutputStream;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public final class Gif {
	private final File destinationFile;
	private final Dimension size;
	private final int fps;
	private final Stream<AnimationFrame> frames;
	private BufferedImage rgbImage;
	private TempFile tempGifFile;

	public Gif(
		File destinationFile,
		Dimension size,
		int fps,
		Stream<AnimationFrame> frames
	) {
		this.destinationFile = destinationFile;
		this.size = size;
		this.fps = fps;
		this.frames = frames;
		this.rgbImage = initRGBImage(size);
		this.tempGifFile = new TempFile(
			"tendiwa_animation",
			String.valueOf(hashCode())
		);
	}

	public void save() {
		AwtCanvas canvas = new TestCanvas(1, size);
		GifSequenceWriter writer = createWriter();
		frames.forEach(frame -> {
			canvas.draw(frame);
			writer.writeToSequence(convertARGBToRGB(canvas.getImage()));
		});
		writer.close();
		try {
			Files.move(tempGifFile, destinationFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private BufferedImage initRGBImage(Dimension size) {
		BufferedImage image = new BufferedImage(
			size.width(),
			size.height(),
			BufferedImage.TYPE_INT_RGB
		);
		image.getGraphics().setColor(Color.white);
		return image;
	}

	private BufferedImage convertARGBToRGB(BufferedImage argbImage) {
		rgbImage.getGraphics().fillRect(
			0,
			0,
			rgbImage.getWidth(),
			rgbImage.getHeight()
		);
		rgbImage.getGraphics().drawImage(argbImage, 0, 0, null);
		return rgbImage;
	}

	private GifSequenceWriter createWriter() {
		try {
			return new GifSequenceWriter(
				new FileImageOutputStream(tempGifFile),
				BufferedImage.TYPE_INT_ARGB,
				1000 / fps,
				true
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
