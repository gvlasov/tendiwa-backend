package org.tendiwa.core.vision;

import org.la4j.factory.Basic2DFactory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;
import org.tendiwa.core.Border;

import java.awt.geom.AffineTransform;

class Obstacle {
	private static final Basic2DFactory factory = new Basic2DFactory();
	private final Vector vector;
	private final double x;
	private final double y;

	public Obstacle(double x, double y, Vector vector) {
		this.x = x;
		this.y = y;
		this.vector = vector;
	}

	/**
	 * Creates a new obstacle from this one, moved by {@code dx} and {@code dy} and with its vector rotated by {@code
	 * angle}
	 *
	 * @param dx
	 * 	Shift by x axis
	 * @param dy
	 * 	Shift by y axis
	 * @param angle
	 * 	Angle in radians
	 * @return Moved and rotated obstacle
	 */
	public Obstacle(Border border, int dx, int dy, double angle) {
		double x;
		double y;
		Vector vector;
		switch (border.side) {
			case N:
				vector = factory.createVector(new double[]{1, 0});
				x = border.x - 0.5;
				y = border.y - 0.5;
				break;
			case E:
				vector = factory.createVector(new double[]{0, 1});
				x = border.x + 0.5;
				y = border.y - 0.5;
				break;
			case S:
				vector = factory.createVector(new double[]{1, 0});
				x = border.x - 0.5;
				y = border.y + 0.5;
				break;
			case W:
				vector = factory.createVector(new double[]{0, 1});
				x = border.x - 0.5;
				y = border.y - 0.5;
				break;
			default:
				throw new NullPointerException();
		}
		double sin = Math.sin(angle);
		double cos = Math.cos(angle);
		Matrix m = factory.createMatrix(
			new double[][]{
				new double[]{cos, sin},
				new double[]{-sin, cos}
			}
		);
		Vector rotated = m.multiply(vector);
		double[] srcPoint = new double[]{
			x + dx,
			y + dy
		};
		double[] destPoint = new double[2];
		AffineTransform.getRotateInstance(-angle, 0, 0).transform(srcPoint, 0, destPoint, 0, 1);
		this.x = destPoint[0];
		this.y = destPoint[1];
		this.vector = rotated;
	}

	double getEndX() {
		return x + vector.get(0);
	}

	double getEndY() {
		return y + vector.get(1);
	}

	public double getX() {
		return x;
	}

	public Vector getVector() {
		return vector;
	}

	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		return "Obstacle{" +
			"vector.x=" + vector.get(0) +
			", vector.y=" + vector.get(1) +
			", x=" + x +
			", y=" + y +
			'}';
	}

	/**
	 * Splits this Obstacle into two pieces with x-axis and returns the piece lower by y-axis (i.e., closer to {@link
	 * Seer}.
	 *
	 * @return A new obstacle lying completely in III and IV quarters of the target cell's rotated coordinate system.
	 */
	Obstacle splitWithXAxis() {
		double xLower = x;
		double yLower = y;
		double xHigher = x + vector.get(0);
		double yHigher = y + vector.get(1);
		assert Math.signum(yLower) != Math.signum(yHigher);
		if (yLower > yHigher) {
			double buf = xLower;
			xLower = xHigher;
			xHigher = buf;
			buf = yLower;
			yLower = yHigher;
			yHigher = buf;
		}
		double k = (yHigher - yLower) / (xHigher - xLower);
		Vector newVector = factory.createVector(new double[]{
			xLower - yLower / k, -yLower
		});
		return new Obstacle(xLower, yLower, newVector);

	}

}
