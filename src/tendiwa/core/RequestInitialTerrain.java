package tendiwa.core;

import org.tendiwa.events.EventInitialTerrain;

public class RequestInitialTerrain implements Request {
@Override
public void process() {
	Tendiwa.getClientEventManager().event(new EventInitialTerrain());
}
}
