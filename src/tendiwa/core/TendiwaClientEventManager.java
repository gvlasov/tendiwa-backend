package tendiwa.core;

import org.tendiwa.events.*;

public interface TendiwaClientEventManager {
void event(EventMove e);

void event(EventSay eventSay);

void event(EventFovChange eventFovChange);

void event(EventInitialTerrain eventInitialTerrain);

void event(EventItemDisappear eventItemDisappear);

void event(EventGetItem eventGetItem);
}
