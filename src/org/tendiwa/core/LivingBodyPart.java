package org.tendiwa.core;
import java.util.HashSet;

public class LivingBodyPart extends BodyPart {
	final HashSet<Injury> injuries = new HashSet<Injury>();
	LivingBodyPart(BodyPartType type) {
		super(type);
	}
}
