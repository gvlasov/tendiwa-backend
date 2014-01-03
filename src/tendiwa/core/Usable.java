package tendiwa.core;

import java.util.Collection;

public interface Usable extends ObjectType {
public Collection<? extends CharacterAbility> getUsages();
}
