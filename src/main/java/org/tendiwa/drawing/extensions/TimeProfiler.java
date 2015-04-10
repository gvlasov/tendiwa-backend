package org.tendiwa.drawing.extensions;

public interface TimeProfiler {
	void saveTime(String name);

	static TimeProfiler profiler = new PieChartTimeProfiler();
}
