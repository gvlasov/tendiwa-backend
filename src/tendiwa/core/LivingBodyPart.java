package tendiwa.core;
import java.util.HashSet;

import tendiwa.core.BodyPart;
import tendiwa.core.BodyPartType;
public class LivingBodyPart extends BodyPart {
	final HashSet<Injury> injuries = new HashSet<Injury>();
	LivingBodyPart(BodyPartType type) {
		super(type);
	}
}
