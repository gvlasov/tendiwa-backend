package org.tendiwa.core;

public enum ApparelSlot {
	/**
	 * Something that wraps around one's neck and shoulders. Examples: scarf
	 */
	SCARF,
	/**
	 * Something that covers head's top. Examples: helm, cap, skullcap, headphones.
	 */
	HEADGEAR,
	/**
	 * Something that is attached to one's ear soak. Examples: pair earrings, single earring, other piercing.
	 */
	EARRINGS,
	/**
	 * Something that is attached to one's pinna. Examples: bluetooth headset.
	 */
	EAR,
	/**
	 * Something on a chain that goes around one's neck. Examples: necklace, chain,
	 */
	NECKLACE,
	/**
	 * Something that is attached to one's back or is worn on shoulders. Examples: backpack, jetpack, mechanical wings.
	 */
	BACK,
	/**
	 * Something that wraps around one's neck and shoulders and covers one's back. Examples: cloak
	 */
	CLOAK,
	/**
	 * Something that covers one's groin ang goes under legwear. Examples: underpants, loincloth, thong, swimming trunks.
	 */
	UNDERPANTS,
	/**
	 * Something worn on one's feet. Examples: boots, slippers,
	 */
	FEET,
	/**
	 * Socks.
	 */
	SOCKS,
	/**
	 * Something that covers area from waist up to feet. Examples: jeans, leggings, shorts
	 */
	LEGWEAR,
	/**
	 * Something that goes around one's waist. Examples: fanny pack, girdle.
	 */
	WAIST,
	/**
	 * Something that goes on one's wrist. Examples: bracers, watch, personal radiation meter.
	 */
	WRIST,
	/**
	 * Something that goes on one's face. Examples: goggles, eyepatch, mask.
	 */
	FACE,
	/**
	 * Something that is worn on one's hands. Examples: gloves, gauntlets, mittens.
	 */
	HAND,
	/**
	 * Something that is worn on one's finger.
	 */
	FINGER,
	/**
	 * A piece of bodywear that may go under another (outer) piece of bodywear. Examples: t-shirt, shirt, gambeson,
	 *
	 * @see ApparelSlot#OUTER_BODYWEAR
	 */
	LOWER_BODYWEAR,
	/**
	 * A piece of bodywear that may go above another (lower) piece of bodywear and may not go below any piece of bodywear.
	 * Examples: jacket, fur coat, plate armour.
	 *
	 * @see ApparelSlot#LOWER_BODYWEAR
	 */
	OUTER_BODYWEAR,
	/**
	 * A piece of bodywear that is worn on breasts. Examples: brassiere, swimsuit.
	 */
	BREASTS;

public static ApparelSlot string2slot(String string) {
	if (string.equals("scarf")) {
		return SCARF;
	}
	if (string.equals("headgear")) {
		return HEADGEAR;
	}
	if (string.equals("back")) {
		return BACK;
	}
	if (string.equals("breasts")) {
		return BREASTS;
	}
	if (string.equals("cloak")) {
		return CLOAK;
	}
	if (string.equals("ear")) {
		return EAR;
	}
	if (string.equals("earrings")) {
		return EARRINGS;
	}
	if (string.equals("face")) {
		return FACE;
	}
	if (string.equals("feet")) {
		return FEET;
	}
	if (string.equals("finger")) {
		return FINGER;
	}
	if (string.equals("hand")) {
		return HAND;
	}
	if (string.equals("legwear")) {
		return LEGWEAR;
	}
	if (string.equals("lower_bodywear")) {
		return LOWER_BODYWEAR;
	}
	if (string.equals("necklace")) {
		return NECKLACE;
	}
	if (string.equals("outer_bodywear")) {
		return OUTER_BODYWEAR;
	}
	if (string.equals("socks")) {
		return SOCKS;
	}
	if (string.equals("underpants")) {
		return UNDERPANTS;
	}
	if (string.equals("waist")) {
		return WAIST;
	}
	if (string.equals("wrist")) {
		return WRIST;
	}
	throw new RuntimeException("ApparelSlot for string \"" + string + "\" does not exist");
}

}
