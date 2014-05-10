package org.tendiwa.drawing;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;
import org.tendiwa.core.meta.GifSequenceWriter;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.io.IOException;

public class GifBuilder {

    private GifSequenceWriter gifSequenceWriter;
    private TestCanvas canvas;
    private final int fps;
    private final Logger logger;
    private File tempFile;
    private ImageOutputStream imageOutput;

    @Inject
    public GifBuilder(
            @Assisted TestCanvas canvas,
            @Assisted("fps") int fps,
            @Named("imageInfoLogger") Logger logger
    ) {
        this.canvas = canvas;
        this.fps = fps;
        this.logger = logger;
        initGifWriter();
    }

    private void initGifWriter() {
        imageOutput = null;

        try {
            tempFile = File
                    .createTempFile("tendiwa_animation", String.valueOf(hashCode()));
            imageOutput = new FileImageOutputStream(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int imageType = canvas.DEFAULT_LAYER.image.getType();
        try {
            this.gifSequenceWriter = new GifSequenceWriter(imageOutput, imageType, 1000 / fps, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFrame() {
        try {
            gifSequenceWriter.writeToSequence(canvas.image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves animation of frames made with {@link #saveFrame()} to a file with a specified destination.
     * <p>
     * This method does not create a file, but rather moves a previously formed temporary file to the destination
     * location.
     *
     * @param path
     *         Save path.
     */
    public void saveAnimation(String path) {
        if (tempFile == null) {
            throw new IllegalStateException("Before saving animation you need to create one");
        }
        try {
            gifSequenceWriter.close();
            imageOutput.close();
            Files.move(tempFile, new File(path));
        } catch (IOException e) {
            throw new RuntimeException("Could not save animation");
        }
        initGifWriter();

        logger.info("Animation saved to " + path);
    }
}
