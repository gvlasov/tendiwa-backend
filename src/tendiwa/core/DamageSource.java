package tendiwa.core;

public interface DamageSource {

public DamageSourceType getSourceType();

enum DamageSourceType {
	CHARACTER, ITEM
}
}
