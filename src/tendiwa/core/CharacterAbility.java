package tendiwa.core;

public interface CharacterAbility<T extends ActionTargetType> extends Resourceable {
public T getAction();
}
