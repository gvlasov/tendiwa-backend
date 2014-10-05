package org.tendiwa.drawing;

import java.util.Collection;

public interface DrawableInto {
	<T> void draw(T what, DrawingAlgorithm<? super T> how, TestCanvas.Layer where);

	<T> void draw(T what, DrawingAlgorithm<? super T> how);

	<T> void drawAll(Collection<T> what, DrawingAlgorithm<? super T> how);


}
