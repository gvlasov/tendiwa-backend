package tendiwa.core;

import org.tendiwa.events.EventMove;

public interface TendiwaClientEventManager {
public void event(EventMove e);

void event(EventSay eventSay);

}
