package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.TestCanvas;

import java.util.Collection;

public class FakeCanvas implements DrawableInto {
	@Override
	public <T> void draw(T what, DrawingAlgorithm<? super T> how, TestCanvas.Layer where) {

	}

	@Override
	public <T> void draw(T what, DrawingAlgorithm<? super T> how) {

	}

	@Override
	public <T> void drawAll(Collection<T> what, DrawingAlgorithm<? super T> how) {

	}
}
