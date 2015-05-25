package org.tendiwa.drawing;

public interface Drawable {
	void drawIn(Canvas canvas);

	default Drawable andThen(Drawable drawable) {
		return canvas -> {
			Drawable.this.drawIn(canvas);
			drawable.drawIn(canvas);
		};
	}
}
