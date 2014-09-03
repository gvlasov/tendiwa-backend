package org.tendiwa.settlements.buildings;

import com.sun.org.apache.xalan.internal.xsltc.dom.DocumentCache;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.settlements.RectangleWithNeighbors;

@FunctionalInterface
public interface LotFacadeAssigner {
	public CardinalDirection assignDirection(RectangleWithNeighbors lot);
}
