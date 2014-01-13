package org.tendiwa.core;

public interface PlaceableInCell {
void place(HorizontalPlane terrain, int x, int y);
boolean containedIn(HorizontalPlane plane, int x, int y);
}
