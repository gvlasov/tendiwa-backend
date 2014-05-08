package org.tendiwa.drawing;


import com.google.inject.name.Named;

import javax.inject.Provider;

public class LargeCanvasProvider implements Provider<TestCanvas> {


    @Override
    public TestCanvas get() {
        return new TestCanvas(2, 800, 600);
    }
}
