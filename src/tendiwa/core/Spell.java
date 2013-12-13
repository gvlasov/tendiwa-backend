package tendiwa.core;

public interface Spell<T extends SpellTargetType> extends Resourceable {

public T getAction();
public int getManaPointsRequired();

}