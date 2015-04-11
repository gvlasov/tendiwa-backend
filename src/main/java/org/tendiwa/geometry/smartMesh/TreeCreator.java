package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

public interface TreeCreator {
	InnerTree createTreeOnEnclosedCycle(OrientedCycle enclosedCycle, Point2D root);
}
