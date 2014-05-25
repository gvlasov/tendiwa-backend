package org.tendiwa.demos.drawing;

import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.PieChartTest;

public class PieChartDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(PieChartDemo.class);
	}

	@Override
	public void run() {
		PieChartTest pieChart = new PieChartTest(400);
		pieChart.add("penis", 20);
		pieChart.add("vagina", 40);
		pieChart.add("cock", 60);
		pieChart.add("penis", 20.12599);
		pieChart.draw();

	}
}
