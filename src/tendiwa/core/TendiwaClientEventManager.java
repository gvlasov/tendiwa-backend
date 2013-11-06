package tendiwa.core;

import org.tendiwa.events.EventFovChange;
import org.tendiwa.events.EventInitialTerrain;
import org.tendiwa.events.EventMove;

public interface TendiwaClientEventManager {
void event(EventMove e);

void event(EventSay eventSay);

void event(EventFovChange eventFovChange);

void event(EventInitialTerrain eventInitialTerrain);
}
