package tendiwa.core;

import org.tendiwa.lexeme.Localizable;

public class SoundType implements Localizable {
private String name;
private int bass;
private int mid;
private int treble;

public SoundType() {
}

public void name(String name) {
	this.name = name;
}

public void bass(int bass) {
	this.bass = bass;
}

public void mid(int mid) {
	this.mid = mid;
}

public void treble(int treble) {
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

@Override
public String getLocalizationId() {
	return name;
}
}
