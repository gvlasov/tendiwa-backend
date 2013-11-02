package tendiwa.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PlayerCharacter extends Character implements GsonForStaticDataSerializable {
public static final long serialVersionUID = 96186762L;
protected final String cls;
public WebSocket connection;
protected int str;
protected int dex;
protected int wis;
protected int itl;
protected HashMap<String, Integer> skills = new HashMap<>();
protected int party = 0;
protected PlayerCharacter inviter;
protected NonPlayerCharacter dialoguePartner;

public PlayerCharacter(HorizontalPlane plane, int x, int y, String name, CharacterType race, String cls) {
	super(plane, race, x, y, name);
	this.cls = cls;
	this.plane = plane;
	this.plane.getCell(x, y).setPassability(TerrainBasics.PASSABILITY_SEE);
	maxEp = 100;
	ep = 100;
	fraction = 1;
	timeStream = new TimeStream(this);
}

/* Getters */
public String toString() {
	return name + " the " + cls;
}

public String getCls() {
	return cls;
}

/* Setters */
public void move(int x, int y) {
	int prevX = this.x;
	int prevY = this.y;
	super.move(x, y);
	if (
		plane.getChunkRoundedCoord(prevX) != plane.getChunkRoundedCoord(x)
			|| plane.getChunkRoundedCoord(prevY) != plane.getChunkRoundedCoord(y)
		) {
		// If player moves to another chunk, load chunks
		timeStream.loadApproachedChunks(plane, x, y);
		timeStream.unloadUnusedChunks(plane);
	}
}

/* Actions */
public void say(String message) {
	// location message
	Chat.locationMessage(this, message);
	Tendiwa.getClient().event(new EventSay(message));
}

public void die() {
	super.die();
	timeStream.removeCharacter(this);
}

public void startConversation(int characterId) {
	dialoguePartner = (NonPlayerCharacter) timeStream.getCharacterById(characterId);
	if (dialoguePartner.hasDialogue()) {
		dialoguePartner.applyConversationStarting(this);
	}
}

/* Travelling */
public void goToAnotherLevel(HorizontalPlane newPlane, int x, int y) {
	/**
	 * Transports character to another level of current location.
	 */
	this.plane = newPlane;
	this.x = x;
	this.y = y;
}

public void dialogueAnswer(int answerIndex) {
	say(dialoguePartner.dialogues.get(this).getAnswerText(answerIndex));
	dialoguePartner.proceedToNextDialoguePoint(this, answerIndex);
	moveTime(500);
}

/* Data */
public Set<Chunk> getClosestChunks() {
	Set<Chunk> answer = new HashSet<>();
	Chunk playerChunk = plane.getChunkWithCell(x, y);
	answer.add(playerChunk);
	answer.add(plane.getChunkByCoord(playerChunk.getX() - Chunk.SIZE, playerChunk.getY() - Chunk.SIZE));
	answer.add(plane.getChunkByCoord(playerChunk.getX(), playerChunk.getY() - Chunk.SIZE));
	answer.add(plane.getChunkByCoord(playerChunk.getX() + Chunk.SIZE, playerChunk.getY() - Chunk.SIZE));

	answer.add(plane.getChunkByCoord(playerChunk.getX() - Chunk.SIZE, playerChunk.getY()));
	answer.add(plane.getChunkByCoord(playerChunk.getX() + Chunk.SIZE, playerChunk.getY()));

	answer.add(plane.getChunkByCoord(playerChunk.getX() - Chunk.SIZE, playerChunk.getY() + Chunk.SIZE));
	answer.add(plane.getChunkByCoord(playerChunk.getX(), playerChunk.getY() + Chunk.SIZE));
	answer.add(plane.getChunkByCoord(playerChunk.getX() + Chunk.SIZE, playerChunk.getY() + Chunk.SIZE));
	return answer;
}

@Override
public JsonElement serialize(JsonSerializationContext context) {
	JsonArray jArray = (JsonArray) super.serialize(context);
	jArray.add(new JsonPrimitive(cls));
	return jArray;
}
}
