package org.tendiwa.drawing;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.apache.log4j.Logger;

public class DrawingModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TestCanvas.class)
                .toProvider(DefaultTestCanvasProvider.class)
                .in(Scopes.SINGLETON);
        install(new FactoryModuleBuilder()
                .build(GifBuilderFactory.class));
        bind(Logger.class)
                .annotatedWith(Names.named("imageInfoLogger"))
                .toInstance(Logger.getLogger("imageInfoLogger"));
    }
}
