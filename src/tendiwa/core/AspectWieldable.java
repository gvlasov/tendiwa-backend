package tendiwa.core;

public class AspectWieldable extends Aspect {
private final Handedness handedness;

public AspectWieldable(Handedness handedness) {
	super(AspectName.WIELDABLE);
	this.handedness = handedness;
}

public Handedness getHandedness() {
	return handedness;
}
}
