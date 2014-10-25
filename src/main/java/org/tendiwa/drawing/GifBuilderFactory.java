package org.tendiwa.drawing;

import com.google.inject.assistedinject.Assisted;

public interface GifBuilderFactory {
	public GifBuilder create(@Assisted TestCanvas canvas, @Assisted("fps") int fps);
}
