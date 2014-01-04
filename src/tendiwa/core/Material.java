package tendiwa.core;

public class Material {
public String name;
private int durability;
private int density;

public Material() {
}

public void name(String name) {
	this.name = name;
}

public void durability(int durability) {
	this.durability = durability;
}

public void density(int density) {
	this.density = density;
}

public int getDensity() {
	return density;
}

public int getDurability() {
	return durability;
}
}
