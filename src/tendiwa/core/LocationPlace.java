package tendiwa.core;

import tendiwa.geometry.EnhancedRectangle;

public class LocationPlace extends EnhancedRectangle {
Class<? extends Location> cls;
public LocationPlace(Class<? extends Location>cls, int x, int y, int width, int height) {
	super(x, y, width, height);
	this.cls = cls;
}


}
