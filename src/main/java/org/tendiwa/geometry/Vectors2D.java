package org.tendiwa.geometry;

public class Vectors2D {
	public static final double EPSILON = 1e-10;

	/**
	 * Computes perp dot product.
	 *
	 * @param a
	 * 	A vector.
	 * @param b
	 * 	Another vector.
	 * @return Perp dot product of vectors a and b.
	 * @see <a href="http://mathworld.wolfram.com/PerpDotProduct.html">Perp Dot Product</a>
	 */
	public static double perpDotProduct(double[] a, double[] b) {
		return a[0] * b[1] - a[1] * b[0];
	}

	public static double[] subtract(double[] a, double[] b) {
		return new double[]{a[0] - b[0], a[1] - b[1]};
	}

	public static boolean areParallel(double[] a, double[] b) {
//		return Math.abs(dotProduct(a, b) / (magnitude(a) * magnitude(b))) > 1 - Vectors2D.EPSILON;
		a = normalize(a);
		b = normalize(b);
		double[] sub = subtract(a, b);
		if (Math.abs(sub[0]) < Vectors2D.EPSILON && Math.abs(sub[1]) < Vectors2D.EPSILON) {
			return true;
		}
		double[] subNeg = subtract(a, new double[]{-b[0], -b[1]});
		if (Math.abs(subNeg[0]) < Vectors2D.EPSILON && Math.abs(subNeg[1]) < Vectors2D.EPSILON) {
			return true;
		}
		return false;
	}

//	public static boolean areShitParallel(double[] a, double[] b) {
//		boolean zeroByY = a[1] == 0 || b[1] == 0;
//		if (zeroByY) {
//			boolean zeroByX = a[0] == 0 || b[0] == 0;
//			if (zeroByX) {
//				return false;
//			}
//			return Math.abs(a[1] / a[0] - b[1] / b[0]) < Vectors2D.EPSILON;
//		}
//		return Math.abs(a[0] / a[1] - b[0] / b[1]) < Vectors2D.EPSILON;
//	}

	public static double[] normalize(double[] v) {
		double magnitude = magnitude(v);
		return new double[]{v[0] / magnitude, v[1] / magnitude};
	}

	private static double magnitude(double[] a) {
		return Math.sqrt(a[0] * a[0] + a[1] * a[1]);
	}

	public static double dotProduct(double[] a, double[] b) {
		return (a[0] * b[0] + a[1] * b[1]);
	}

	public static double angleBetweenVectors(double[] a, double[] b, boolean clockwise) {
		double angleA = Math.atan2(b[1], b[0]);
		double angleB = Math.atan2(a[1], a[0]);
		double angle = angleA - angleB;
		if (clockwise) {
			angle = -angle;
		}
		if (angle < 0) {
			angle = Math.PI * 2 + angle;
		}
		return angle;
	}

}
