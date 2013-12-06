package tendiwa.core;

public class Material {
private final int durability;
private final int density;

public Material(int durability, int density) {
	this.durability = durability;
	this.density = density;
}

public int getDensity() {
	return density;
}

public int getDurability() {
	return durability;
}
}
