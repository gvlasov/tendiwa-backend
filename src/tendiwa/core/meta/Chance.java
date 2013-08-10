package tendiwa.core.meta;

/**
 * An object used to return true with particular probability. For example,
 * {@code new Chance(30).roll()} will return {@core true} with 30% chance.
 */
public class Chance {
	private final int value;

	/**
	 * 
	 * @param val
	 *            A chance to return true.
	 * @throws IllegalArgumentException
	 *             if {@literal value < 0}.
	 */
	public Chance(int val) {
		if (val < 0) {
			throw new IllegalArgumentException();
		}
		value = val;
	}

	/**
	 * Get true of false with a chance defined by constructor.
	 * 
	 * @return true with probability this.value%
	 */
	public boolean roll() {
		return Math.random() * 100 < value;
	}

	public static boolean roll(int value) {
		return Math.random() * 100 < value;
	}

	/**
	 * Returns a random int from between two ints inclusive.
	 * 
	 * @param min
	 *            min value <= max
	 * @param man
	 *            max value
	 * @return true with probability {@code this.value}
	 * @throws IllegalArgumentException
	 *             if min is lesser or equal to max.
	 */
	public static int rand(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException(
				"min ("+min+") must be lesser or equal to max ("+max+")");
		}
		return Math.min(min, max) + (int) Math.round(Math.random() * Math
			.abs(max - min));
	}
	public static int rand(Range range) {
		return rand(range.min, range.max);
	}
	/**
	 * Returns a random long from between two longs inclusive.
	 * 
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 * @return true with probability this.value%
	 * @throws IllegalArgumentException
	 *             if min is lesser or equal to max.
	 */
	public static long rand(long min, long max) {
		if (min > max) {
			throw new IllegalArgumentException(
				"min must be lesser or equal to max");
		}
		return min + (long) Math.round(Math.random() * Math.abs(max - min));
	}
}
