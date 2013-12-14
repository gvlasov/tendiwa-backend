package tendiwa.core;

public interface Spell<T extends ActionTargetType> extends Resourceable {

public T getAction();
public int getManaPointsRequired();

}