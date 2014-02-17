package org.tendiwa.core;

import org.tendiwa.geometry.EnhancedRectangle;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.geometry.RectangleSystemBuilder;

public interface FindCriteria {
	boolean check(EnhancedRectangle rectangle, RectangleSystem rs, RectangleSystemBuilder builder);
}
