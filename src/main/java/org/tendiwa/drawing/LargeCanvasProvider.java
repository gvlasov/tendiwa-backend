package org.tendiwa.drawing;


import javax.inject.Provider;

public class LargeCanvasProvider implements Provider<TestCanvas> {


	@Override
	public TestCanvas get() {
		return new TestCanvas(2, 800, 600);
	}
}
