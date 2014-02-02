package org.tendiwa.core.vision;

import org.tendiwa.core.Border;

public interface ObstacleFindingStrategy {

boolean isCellBlockingVision(int x, int y);

boolean isBorderBlockingVision(Border border);
}
