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
		timeStream.unloadUnusedChunks(plane);
	}
}

/* Actions */
public void say(String message) {
	Tendiwa.getClient().getEventManager().event(new EventSay(message, this));
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

@Override
public JsonElement serialize(JsonSerializationContext context) {
	JsonArray jArray = (JsonArray) super.serialize(context);
	jArray.add(new JsonPrimitive(cls));
	return jArray;
}

}
