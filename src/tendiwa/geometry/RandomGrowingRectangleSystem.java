package tendiwa.geometry;

public class RandomGrowingRectangleSystem extends GrowingRectangleSystem {
	private int amountOfRectangles;
	private int rectangleWidth;
	public RandomGrowingRectangleSystem(int borderWidth, int recntalgeWidth, int amountOfRectangles) {
		super(0);
	}
	public void setBorderWidth(int width) {
		this.borderWidth = borderWidth;
	}
	public void setAmountOfRectnagles(int amount) {
		this.amountOfRectangles = amount;
	}
	public void setRectangleWidth(int width) {
		this.rectangleWidth = width;
	}
	public void build() {
		
	}
}
