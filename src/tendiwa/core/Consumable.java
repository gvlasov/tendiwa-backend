package tendiwa.core;

public interface Consumable extends ItemType {
public boolean canConsume(Character consumer);

public void onConsume(Character consumer);
}
