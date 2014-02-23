package org.tendiwa.core.events;

import com.google.common.collect.ImmutableList;
import org.tendiwa.core.*;
import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Event;
import org.tendiwa.core.vision.*;

public class EventInitialTerrain implements Event {
public final ImmutableList<RenderCell> seenCells;
public final ImmutableList<RenderBorder> seenBorders;
public final int zLevel;
public final Character player;
public final World world;

/**
 * Answer to initial request after World has just been loaded. Collects terrain around PlayerCharacter to send it to
 * client for displaying.
 */
public EventInitialTerrain(Character player, World world, HorizontalPlane plane, Seer seer) {
	this.player = player;
	this.world = world;
	ImmutableList.Builder<RenderCell> seenCellsBuilder = ImmutableList.builder();
	ImmutableList.Builder<RenderBorder> seenBordersBuilder = ImmutableList.builder();
//	HorizontalPlane plane = backend.getPlayerCharacter().getPlane();
	zLevel = plane.getLevel();
//	Seer seer = backend.getPlayerCharacter().getSeer();
	Visibility[][] visionCache = seer.getVisionCache().getContent();
	Cell startPoint = seer.getActualVisionRecStartPoint();
	Cell theoreticalStartPoint = seer.getTheoreticalVisionRecStartPoint();
	for (int i = startPoint.getX() - theoreticalStartPoint.getX(); i < ModifiableCellVisionCache.VISION_CACHE_WIDTH; i++) {
		for (int j = startPoint.getY() - theoreticalStartPoint.getY(); j < ModifiableCellVisionCache.VISION_CACHE_WIDTH; j++) {
			if (visionCache[i][j] == Visibility.VISIBLE) {
				int x = theoreticalStartPoint.getX() + i;
				int y = theoreticalStartPoint.getY() + j;
				seenCellsBuilder.add(new RenderCell(
					world,
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
