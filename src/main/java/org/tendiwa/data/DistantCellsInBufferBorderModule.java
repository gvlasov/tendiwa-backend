package org.tendiwa.data;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.CachedCellSet;
import org.tendiwa.geometry.extensions.ChebyshovDistanceBufferBorder;

import static com.google.inject.name.Names.named;
import static org.tendiwa.geometry.GeometryPrimitives.*;

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
			.toInstance(rectangle(40, 40, 20, 20));
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
			new ChebyshovDistanceBufferBorder(
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
