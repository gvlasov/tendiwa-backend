package tendiwa.core;

import java.util.Collection;
import java.util.LinkedList;

public class Usable {
Collection<CharacterAbility> actions = new LinkedList<>();

void addAction(CharacterAbility action) {
	actions.add(action);
}

public Collection<CharacterAbility> getActions() {
	return actions;
}

}
