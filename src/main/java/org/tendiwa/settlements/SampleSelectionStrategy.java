package org.tendiwa.settlements;

import com.google.common.collect.ImmutableCollection;
import org.tendiwa.geometry.Point2D;

/**
 * [Kelly section 4.2.3]
 * <p/>
 * Defines how adaptive roads algorithm chooses where to place the next node when sampling a path from one high level
 * graph's vertex to another as described in [Kelly section 4.2.2].
 */
public interface SampleSelectionStrategy {
/**
 * Determines which sample point from fan is the most suitable to be the end of next road segment.
 *
 * @param sampleFan
 * 	[Kelly section 4.2.2]
 * 	<p/>
 * 	A set of possible next points. {@code sampleFan.size()} is guaranteed to be > 0.
 * @return The most suitable next point from the set.
 */
public Point2D selectNextPoint(ImmutableCollection<Point2D> sampleFan);
}
