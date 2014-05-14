package org.tendiwa.demos;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.DistantCellsFinder;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.extensions.CachedCellSet;
import org.tendiwa.geometry.extensions.ChebyshevDistanceCellBufferBorder;

import static com.google.inject.name.Names.named;
import static org.tendiwa.geometry.DSL.rectangle;

/**
 * Config for {@link org.tendiwa.geometry.DistantCellsInBufferBorderTest}.
 */
@SuppressWarnings("unused")
public class DistantCellsInBufferBorderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Integer.class)
                .annotatedWith(named("minDistanceBetweenCells"))
                .toInstance(9);
        bind(Integer.class)
                .annotatedWith(named("bufferDepth"))
                .toInstance(5);
        bind(Rectangle.class)
                .annotatedWith(named("waterRectangle"))
                .toInstance(new Rectangle(40, 40, 20, 20));
        bind(Rectangle.class)
                .annotatedWith(named("worldRectangle"))
                .toInstance(rectangle(100, 100));
    }

    @Provides
    BoundedCellSet bufferBorder(
            @Named("waterRectangle") Rectangle waterRectangle,
            @Named("worldRectangle") Rectangle worldRectangle,
            @Named("bufferDepth") int bufferDepth
    ) {
        return new CachedCellSet(
                new ChebyshevDistanceCellBufferBorder(
                        bufferDepth,
                        waterRectangle::contains
                ),
                worldRectangle
        );
    }

    @Provides
    DistantCellsFinder distanceCells(
            BoundedCellSet bufferBorder,
            @Named("minDistanceBetweenCells") int minDistanceBetweenCells
    ) {
        return new DistantCellsFinder(bufferBorder, minDistanceBetweenCells);
    }
}
