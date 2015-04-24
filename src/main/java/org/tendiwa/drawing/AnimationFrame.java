package org.tendiwa.drawing;

/**
 * When drawn, clears canvas and then on the clean canvas draws whatever {@code drawable} draws
 */
public final class AnimationFrame implements Drawable {
	private final Drawable drawable;

	public AnimationFrame(Drawable drawable) {
		this.drawable = drawable;
	}

	@Override
	public void drawIn(Canvas canvas) {
		canvas.clear();
		drawable.drawIn(canvas);
	}
}
