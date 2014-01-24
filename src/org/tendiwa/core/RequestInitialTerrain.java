package org.tendiwa.core;

public class RequestInitialTerrain implements Request {
@Override
public void process() {
	Tendiwa.getPlayerCharacter().getSeer().computeFullVisionCache();
	Tendiwa.getClientEventManager().event(new EventInitialTerrain());
}
}
