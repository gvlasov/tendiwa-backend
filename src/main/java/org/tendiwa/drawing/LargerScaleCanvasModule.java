package org.tendiwa.drawing;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class LargerScaleCanvasModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TestCanvas.class)
                .annotatedWith(Names.named("scale2"))
                .toProvider(LargeCanvasProvider.class);
    }
}
