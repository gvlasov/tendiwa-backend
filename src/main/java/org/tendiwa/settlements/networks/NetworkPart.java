package org.tendiwa.settlements.networks;

import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.graphs.graphs2d.Graph2D;

public interface NetworkPart {
	public void notify(CutSegment2D cutSegment);

	public Graph2D graph();
}
