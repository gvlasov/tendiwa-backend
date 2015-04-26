package org.tendiwa.drawing;

public interface Drawable {
	void drawIn(Canvas canvas);

	default Drawable andThen(Drawable drawable) {
		return new Drawable() {
			@Override
			public void drawIn(Canvas canvas) {
				this.drawIn(canvas);
				drawable.drawIn(canvas);
			}
		};
	}
}
