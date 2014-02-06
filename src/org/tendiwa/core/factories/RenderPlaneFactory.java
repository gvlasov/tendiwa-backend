package org.tendiwa.core.factories;

import org.tendiwa.core.HorizontalPlane;
import org.tendiwa.core.RenderPlane;
import org.tendiwa.core.World;

public interface RenderPlaneFactory {
public RenderPlane create(World world, HorizontalPlane plane);
}
