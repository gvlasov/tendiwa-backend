package tendiwa.core.characters;

import java.util.HashMap;
import java.util.HashSet;

import tendiwa.core.Character;
import tendiwa.core.CharacterType;
import tendiwa.core.Chunk;
import tendiwa.core.HorizontalPlane;
import tendiwa.core.NonPlayerCharacter;
import tendiwa.core.PlayerHandler;
import tendiwa.core.StaticData;
import tendiwa.core.TimeStream;


/**
 * Manages Characters when using of {@link TimeStream} is not permitted, for
 * example, when there are no PlayerCharacters in the world and therefore no
 * TimeStreams.
 */
public class CharacterManager {
	public static final HashSet<Character> characters = new HashSet<Character>();
	public static final HashMap<Integer, PlayerHandler> players = new HashMap<Integer, PlayerHandler>();

	public static PlayerHandler createPlayer(HorizontalPlane plane, int x,
			int y, String name, CharacterType race, String cls) {
		PlayerHandler player = new PlayerHandler(plane, x, y, name, race, cls);
		players.put(player.getId(), player);
		player.setTimeStream(new TimeStream(player, null));
		plane.getCell(x, y).character(player);
		return player;
	}

	public static void createCharacter(HorizontalPlane plane, int x, int y,
			int characterTypeId, String name) {
		characters.add(new NonPlayerCharacter(plane, StaticData.getCharacterType(characterTypeId), x, y, 
				name));
	}

	public static void getCharactersInChunk(Chunk chunk) {

	}

	public static PlayerHandler getPlayerById(int characterId) {
		return players.get(characterId);
	}

}
