package org.tendiwa.core;

import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.geometry.RecTreeBuilder;

public interface FindCriteria {
	boolean check(Rectangle rectangle, RectangleSystem rs, RecTreeBuilder builder);
}
