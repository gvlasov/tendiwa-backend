package tendiwa.core;


public class Container extends ItemCollection {
	private AspectContainer aspect;
	public Container(AspectContainer aspect) {
		this.aspect = aspect;
	}
	public double getVolume() {
		return aspect.getVolume();
	}
}
