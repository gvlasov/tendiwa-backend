package org.tendiwa.drawing.extensions;

import com.google.common.base.Stopwatch;
import javafx.scene.chart.PieChart;
import org.tendiwa.drawing.PieChartTest;

import java.util.concurrent.TimeUnit;

public class PieChartTimeProfiler {
	private PieChartTest chart;
	private Stopwatch watch;
	private TimeUnit timeUnit;

	public PieChartTimeProfiler() {
		this(400, TimeUnit.MILLISECONDS);
	}

	public PieChartTimeProfiler(int width, TimeUnit timeUnit) {
		this.chart = new PieChartTest(width);
		this.watch = Stopwatch.createStarted();
		this.timeUnit = timeUnit;
	}

	/**
	 * Adds time elapsed since the last call to this method or constructor call to pie chart's slice named {@code
	 * name}.
	 *
	 * @param name
	 * 	Name of a pie chart's slice.
	 */
	public void saveTime(String name) {
		chart.add(name, (int) watch.elapsed(timeUnit));
		watch.reset().start();
	}

	/**
	 * Creates pie chart's window.
	 *
	 * @see {@link org.tendiwa.drawing.PieChartTest#draw()}
	 */
	public void draw() {
		chart.setTitleSupplier(() -> "Time elapsed: " + chart
			.getData()
			.stream()
			.map(PieChart.Data::getPieValue)
			.reduce((double) 0, (a, b) -> a + b) + " " + PieChartTimeProfiler.abbreviate(timeUnit)
		);
		chart.draw();
	}

	/**
	 * Provides a short name for a time unit, e.g. "ms" for {@link java.util.concurrent.TimeUnit#MILLISECONDS}.
	 * <p>
	 * Copied from {@link com.google.common.base.Stopwatch#abbreviate(java.util.concurrent.TimeUnit)} because it is
	 * not public.
	 *
	 * @param unit
	 * 	Time unit to abbreviate.
	 * @return Short name for a time unit.
	 */
	public static String abbreviate(TimeUnit unit) {
		switch (unit) {
			case NANOSECONDS:
				return "ns";
			case MICROSECONDS:
				return "\u03bcs"; // μs
			case MILLISECONDS:
				return "ms";
			case SECONDS:
				return "s";
			case MINUTES:
				return "min";
			case HOURS:
				return "h";
			case DAYS:
				return "d";
			default:
				throw new AssertionError();
		}
	}
}
