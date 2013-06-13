package tendiwa.core.terrain;
import java.util.Hashtable;

import tendiwa.core.AspectContainer;
import tendiwa.core.ItemCollection;


public class Container extends ItemCollection {
	private AspectContainer aspect;
	public Container(AspectContainer aspect) {
		this.aspect = aspect;
	}
	public double getVolume() {
		return aspect.getVolume();
	}
}
