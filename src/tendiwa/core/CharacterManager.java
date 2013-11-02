package tendiwa.core;

import java.util.HashSet;

/**
 * Manages Characters when using of TimeStream is not permitted, for example, when there are no PlayerCharacters in the
 * world and therefore no TimeStreams.
 */
public class CharacterManager {
public static final HashSet<Character> characters = new HashSet<>();

public static void createCharacter(HorizontalPlane plane, int x, int y,
                                   int characterTypeId, String name) {
	characters.add(new NonPlayerCharacter(plane, StaticData.getCharacterType(characterTypeId), x, y,
		name));
}

public static void getCharactersInChunk(Chunk chunk) {

}

}
