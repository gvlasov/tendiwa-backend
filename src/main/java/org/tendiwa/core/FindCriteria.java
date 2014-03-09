package org.tendiwa.core;

import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.geometry.RectangleSystemBuilder;

public interface FindCriteria {
	boolean check(Rectangle rectangle, RectangleSystem rs, RectangleSystemBuilder builder);
}
