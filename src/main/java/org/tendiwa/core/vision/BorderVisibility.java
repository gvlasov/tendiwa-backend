package org.tendiwa.core.vision;

import org.tendiwa.core.Border;
import org.tendiwa.core.CardinalDirection;

/**
 * Holds Border position and visibility of that border.
 */
public class BorderVisibility extends Border {
public final Visibility visibility;

BorderVisibility(int x, int y, CardinalDirection side, Visibility visibility) {
	super(x, y, side);
	this.visibility = visibility;
}
}
