package org.tendiwa.drawing;

public abstract class Drawable_Wr implements Drawable {
	private final Drawable drawable;

	protected Drawable_Wr(Drawable drawable) {
		this.drawable = drawable;
	}

	@Override
	public void drawIn(Canvas canvas) {
		drawable.drawIn(canvas);
	}
}