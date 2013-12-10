package tendiwa.core;

public class SoundType {
private String name;
private int bass;
private int mid;
private int treble;

public SoundType(String name, int bass, int mid, int treble) {
	this.name = name;
	this.bass = bass;
	this.mid = mid;
	this.treble = treble;
}

/**
 * @return the name
 */
public String getName() {
	return name;
}

/**
 * @return the bass
 */
public int getBass() {
	return bass;
}

/**
 * @return the mid
 */
public int getMid() {
	return mid;
}

/**
 * @return the treble
 */
public int getTreble() {
	return treble;
}

}
