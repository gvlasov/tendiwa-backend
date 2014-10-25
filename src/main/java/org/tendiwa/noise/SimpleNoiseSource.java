package org.tendiwa.noise;

@FunctionalInterface
public interface SimpleNoiseSource {
	int noise(int x, int y);
}
