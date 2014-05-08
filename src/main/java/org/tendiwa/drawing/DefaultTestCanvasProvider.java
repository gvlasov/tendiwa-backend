package org.tendiwa.drawing;

import com.google.inject.Provider;
import com.google.inject.name.Named;

public class DefaultTestCanvasProvider implements Provider<TestCanvas> {


    @Override
    public TestCanvas get() {
        return new TestCanvas(1, 800, 600);
    }
}
