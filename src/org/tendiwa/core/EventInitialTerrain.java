package org.tendiwa.core;

import com.google.common.collect.ImmutableList;
import org.tendiwa.core.vision.*;

import java.util.LinkedList;

public class EventInitialTerrain implements Event {
public final ImmutableList<RenderCell> seenCells;
public final ImmutableList<RenderBorder> seenBorders;
public final int zLevel;

/**
 * Answer to initial request after World has just been loaded. Collects terrain around PlayerCharacter to send it to
 * client for displaying.
 */
public EventInitialTerrain() {
	ImmutableList.Builder<RenderCell> seenCellsBuilder = ImmutableList.builder();
	ImmutableList.Builder<RenderBorder> seenBordersBuilder = ImmutableList.builder();
	HorizontalPlane plane = Tendiwa.getWorld().getPlayer().getPlane();
	zLevel = plane.getLevel();
	Seer seer = Tendiwa.getPlayerCharacter().getSeer();
	Visibility[][] visionCache = seer.getVisionCache().getContent();
	EnhancedPoint startPoint = seer.getActualVisionRecStartPoint();
	EnhancedPoint theoreticalStartPoint = seer.getTheoreticalVisionRecStartPoint();
	for (int i = startPoint.x-theoreticalStartPoint.x; i < ModifiableCellVisionCache.VISION_CACHE_WIDTH; i++) {
		for (int j = startPoint.y-theoreticalStartPoint.y; j < ModifiableCellVisionCache.VISION_CACHE_WIDTH; j++) {
			if (visionCache[i][j] == Visibility.VISIBLE) {
				int x = theoreticalStartPoint.x + i;
				int y = theoreticalStartPoint.y + j;
				seenCellsBuilder.add(new RenderCell(
					x,
					y,
					plane.getFloor(x, y),
					plane.getGameObject(x, y)
				));
			}
		}
	}
	BorderVisionCache borderVisionCache = seer.getBorderVisionCache();

	for (BorderVisibility border : borderVisionCache) {
		if (border.visibility == Visibility.VISIBLE) {
			seenBordersBuilder.add(new RenderBorder(border.x, border.y, border.side, plane.getBorderObject(border)));
		}
	}
	seenCells = seenCellsBuilder.build();
	seenBorders = seenBordersBuilder.build();
}

}
