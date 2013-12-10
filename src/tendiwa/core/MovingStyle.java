package tendiwa.core;

public enum MovingStyle {
	/**
	 * When character moves by being pushed to the air and landing in the destination point
	 */
	LEAP,
	/**
	 * When character moves by stepping his legs or whatever he steps with
	 */
	STEP,
	/**
	 * When character disappears in one place and appears in another.
	 */
	BLINK;
}
