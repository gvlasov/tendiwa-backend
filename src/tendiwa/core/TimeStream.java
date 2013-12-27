package tendiwa.core;

import org.tendiwa.events.EventSound;
import org.tendiwa.lexeme.Localizable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * <p> TimeStream is an instance in which a group of characters acts separated from the rest of the world. This is the
 * key class for in-game turn-based time model: it makes different groups of {@link Character}s around the world act
 * independent. A TimeStream is much like a level in traditional rogue-likes, but it is not a static area as it moves
 * along with PlayerCharacters that form this TimeStream. </p> <p/> <p> A group of PlayerCharacters traveling together
 * forms a TimeStream around them. NonPlayerCharacters close to this group get into this TimeStream, thereby starting
 * acting synchronously with this group of PlayerCharacters. As a group of PlayeCharacters moves, TimeStream moves too,
 * releasing faraway Chunks and adding new Chunks reached by the group. NonPlayerCharacters in the Chunks released by
 * TimeStream also leave the TimeStream, thereby stopping acting until they again are loaded by another (or the same)
 * TimeStream. </p> <p/> <p> Shortly, this class does the following: </p> <ol> <li>Allocates a piece of territory in
 * which time flows synchronously;</li> <li>Determines the order of turns of characters inside this territory;</li>
 * <li>Sends data about what happens to clients if their characters are in this TimeStream;</li> <li>Controls some
 * aspects of {@link NonPlayerCharacter}s' behavior, like how they know about interesting entities around them.</li>
 * </ol>
 */
public class TimeStream {
/**
 * How far from character should terrain be loaded (in chunks)
 */
public static int BASE_ENERGY = 500;
/**
 * All the Characters that take their turns in this TimeStream, both PlayerCharacters and NonPlayerCharacters.
 */
HashSet<Character> characters = new HashSet<>();
/**
 * Chunks of territory that belong to this TimeStream.
 */
HashSet<Chunk> chunks = new HashSet<>();
/**
 * What character is currently seen by who is saved here. Personally, each character himself knows a set of characters
 * he sees; this field contains backward relation - a set of characters a character is seen by. Only NonPlayerCharacters
 * are considered the ones who can see - vision of PlayerCharacters is computed on the client side.
 *
 * @see TimeStream#notifyNeighborsVisiblilty(Character)
 */
private HashMap<Character, Set<NonPlayerCharacter>> observers = new HashMap<>();
/**
 * All the NonPlayerCharacters that take their turns in this TimeStream.
 */
private HashSet<NonPlayerCharacter> nonPlayerCharacters = new HashSet<>();
/**
 * Events, accumulated here in this ArrayList each turn, ready to send out to clients.
 */

/**
 * Initiate a TimeStream around one PlayerCharacter. Places a PlayerCharacter in this TimeStream, which makes
 * PlayerCharacter's client receive events from this TimeStream and determines PlayerCharacter's turn queue.
 */
public TimeStream() {
	characters = new HashSet<>();
}

private void makeObservable() {

}

public HashSet<Character> getCharacters() {
	return characters;
}

public void addNonPlayerCharacter(NonPlayerCharacter character) {
	assert character.chunk != null;
	if (!chunks.contains(character.chunk)) {
		throw new RuntimeException(
			character
				+ " must be in a timeStream's chunk to be added to timeStream. "
				+ "His chunk is " + character.chunk);
	}
	nonPlayerCharacters.add(character);
	characters.add(character);
	observers.put(character, new HashSet<NonPlayerCharacter>());
	character.setTimeStream(this);
}

public void removeCharacter(Character character) {
	if (!characters.contains(character)) {
		throw new Error("Character " + character + " is not in this time stream");
	}
	characters.remove(character);
}

public void removeCharacter(NonPlayerCharacter character) {
	if (!characters.contains(character)) {
		throw new Error("Character " + character
			+ " is not in this time stream");
	}
	characters.remove(character);
	nonPlayerCharacters.remove(character);
}

public void makeSound(int x, int y, SoundType type, Localizable soundSource) {
	Tendiwa.getClientEventManager().event(new EventSound(type, soundSource, x, y));
}

public Character getCharacterById(int characterId) {
	for (Character character : characters) {
		if (character.getId() == characterId) {
			return character;
		}
	}
	throw new Error("No character with id " + characterId);
}

/**
 * Get the next character in turn queue.
 *
 * @return Character
 */
public Character next() {
	Character nextCharacter = null;
	// Get the character with the greatest action points left
	for (Character ch : characters) {
		if (nextCharacter == null
			|| ch.getActionPoints() > nextCharacter.getActionPoints()) {
			nextCharacter = ch;
		}
	}
	// If all the characters' energy is less than 0, then here goes the next
	// turn
	if (nextCharacter.getActionPoints() <= 0) {
		for (Character ch : characters) {
			ch.increaseActionPoints(BASE_ENERGY);
		}
		return next();
	}
	assert nextCharacter != null;
	return nextCharacter;
}

/**
 * Add a chunk to the TimeStream, set chunk's timeStream pointer to this TimeStream and add an event for players that
 * this chunk has been added to the TimeStream.
 *
 * @param chunk
 */
void addChunk(Chunk chunk) {
	chunk.setTimeStream(this);
	chunks.add(chunk);
//	throw new UnsupportedOperationException();
}

public void excludeChunk(Chunk chunk) {
	if (!chunk.belongsToTimeStream(this)) {
		throw new Error(chunk + " is not in this time stream!");
	}
	chunks.remove(chunk);
	chunk.setTimeStream(null);
	throw new UnsupportedOperationException();
}

/**
 * Gets a set of characters that are near this character in square with VISION_RANGE*2+1 side length.
 *
 * @return A set of characters that are close enough to this character.
 */
public HashSet<NonPlayerCharacter> getNearbyNonPlayerCharacters(Character character) {
	HashSet<NonPlayerCharacter> answer = new HashSet<>();
	for (NonPlayerCharacter neighbor : nonPlayerCharacters) {
		// Quickly select characters that could be seen (including this Seer
		// itself)
		if (Math.abs(neighbor.x - character.x) <= Character.VISION_RANGE && Math.abs(neighbor.y - character.y) <= Character.VISION_RANGE) {
			answer.add(neighbor);
		}
	}
	answer.remove(character);
	return answer;
}

public void unloadUnusedChunks(HorizontalPlane plane) {
	Set<Chunk> chunksToExclude = new HashSet<>();
	for (Chunk chunk : chunks) {
		if (chunk.plane != plane) {
			continue;
		}
	}
	for (Chunk chunk : chunksToExclude) {
		this.excludeChunk(chunk);
	}
}

	/*
	 * NonPlayerCharacters may observe Characters -  and so track their current
	 * coordinates. TimeStream handles most of the observing routine. However,
	 * characters know about who can they personally see even without a
	 * TimeStream. TimeStream observation methods are used when it is needed to
	 * track not who sees who, but _who is seen by who_. NonPlayerCharacters
	 * themselves tell TimeStream about who it should add as observer to whom.
	 * Only NonPlayerCharacters can be observers - it makes no sense tracking
	 * PlayerCharacters as observers since their general thought process is made
	 * by player himself, and their visibility is computed on the client-side.
	 */

/**
 * Remember that Character aim can now be seen by NonPlayerCharacter observer.
 *
 * @param aim
 * 	Key; who is observed
 * @param observer
 * 	Value; who is he observed by;
 */
void addObserver(Character aim, NonPlayerCharacter observer) {
	observers.get(aim).add(observer);
}

void removeObserver(Character aim, NonPlayerCharacter observer) {
	observers.get(aim).remove(observer);
}

/**
 * Tells every nearby {@link NonPlayerCharacter} about this Character's new position, so nearby characters can update
 * status of this character as seen/unseen and remember where they have seen this aim last time.
 */
public void notifyNeighborsVisiblilty(Character aim) {
	/*
	 * First each of NonPlayerCharacters in TimeStream tries to see the aim,
	 * then all of the aim's observers try to unsee it. Then all the current
	 * observers remember aim's coordinate.
	 */
	for (NonPlayerCharacter neighbor : nonPlayerCharacters) {
		if (neighbor == aim) {
			continue;
		}
		neighbor.tryToSee(aim);
	}
	Set<NonPlayerCharacter> currentObservers = observers.get(aim);
	// Need to copy observers because its contents will change in the next
	// for loop.
	HashSet<NonPlayerCharacter> observersCopy = new HashSet<>(currentObservers);
	for (NonPlayerCharacter neighbor : observersCopy) {
		neighbor.tryToUnsee(aim);
	}
	for (NonPlayerCharacter character : currentObservers) {
		character.updateObservation(aim, aim.x, aim.y);
	}
}

public void claimCharacterDisappearance(Character character) {
	for (NonPlayerCharacter ch : observers.get(character)) {
		ch.tryToUnsee(character);
	}
}

public void addPlayerCharacter(Character character) {
	characters.add(character);
	character.setTimeStream(this);
	observers.put(character, new HashSet<NonPlayerCharacter>());
}

Set<NonPlayerCharacter> getObservers(Character character) {
	return observers.get(character);
}
}
