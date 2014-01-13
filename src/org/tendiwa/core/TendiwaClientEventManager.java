package org.tendiwa.core;

public interface TendiwaClientEventManager {
void event(EventMove e);

void event(EventSay e);

void event(EventFovChange e);

void event(EventInitialTerrain e);

void event(EventItemDisappear e);

void event(EventGetItem e);

void event(EventLoseItem e);

void event(EventItemAppear e);

void event(EventPutOn e);

void event(EventWield e);

void event(EventTakeOff e);

void event(EventUnwield e);

void event(EventProjectileFly e);

void event(EventSound e);

void event(EventExplosion e);

void event(EventGetDamage e);

void event(EventAttack e);

void event(EventDie e);

void event(EventMoveToPlane eventMoveToPlane);
}
