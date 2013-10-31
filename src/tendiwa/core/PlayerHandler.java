package tendiwa.core;

import java.util.Arrays;

import tendiwa.core.net.clientmessages.ClientMessageAnswer;
import tendiwa.core.net.clientmessages.ClientMessageAttack;
import tendiwa.core.net.clientmessages.ClientMessageAuth;
import tendiwa.core.net.clientmessages.ClientMessageCastSpell;
import tendiwa.core.net.clientmessages.ClientMessageChangePlaces;
import tendiwa.core.net.clientmessages.ClientMessageChatMessage;
import tendiwa.core.net.clientmessages.ClientMessageDropPile;
import tendiwa.core.net.clientmessages.ClientMessageDropUnique;
import tendiwa.core.net.clientmessages.ClientMessageEnterState;
import tendiwa.core.net.clientmessages.ClientMessageJump;
import tendiwa.core.net.clientmessages.ClientMessageMakeSound;
import tendiwa.core.net.clientmessages.ClientMessagePickUpPile;
import tendiwa.core.net.clientmessages.ClientMessagePickUpUnique;
import tendiwa.core.net.clientmessages.ClientMessagePush;
import tendiwa.core.net.clientmessages.ClientMessagePutOn;
import tendiwa.core.net.clientmessages.ClientMessageShieldBash;
import tendiwa.core.net.clientmessages.ClientMessageShootMissile;
import tendiwa.core.net.clientmessages.ClientMessageStartConversation;
import tendiwa.core.net.clientmessages.ClientMessageStep;
import tendiwa.core.net.clientmessages.ClientMessageTakeFromContainer;
import tendiwa.core.net.clientmessages.ClientMessageTakeOff;
import tendiwa.core.net.clientmessages.ClientMessageUseObject;

import com.google.gson.Gson;


/**
 * Extension of PlayerCharacter that contains methods
 * for incoming data handling and sending outcoming data to clients.
 * Methods transform raw string/integer data to game objects, if necessary,
 * and then pass these objects to Character methods (which do not
 * apply raw data in many cases)
 */
public class PlayerHandler extends PlayerCharacter {
	public static final long serialVersionUID = 9299166661L;
	public boolean checkedOut = false;
	protected boolean isAuthorized = false;
	private static final Gson gson = new Gson();
	public PlayerHandler(HorizontalPlane plane, int x, int y, String name, CharacterType race, String cls) {
		super(plane, x, y, name, race, cls);
	}
	/* Net setters */
	public void deauthorize() {
		// Inform all characters who are on global map right now that
		// this character has left
		isAuthorized = false;
//		timeStream.addEvent(new EventDeauthorization(characterId));
//		timeStream.flushEvents();
	}
	public void authorize() {
		// Inform all characters who are on global map right now that
		// this character has entered the game
		isAuthorized = true;
	}
	
	/* Action handlers */
	
	
	public void aAttack(String message) throws InterruptedException {
		ClientMessageAttack data = gson.fromJson(message, ClientMessageAttack.class);
		for (Character ch : timeStream.characters) {
			if (ch.id == data.aimId) {
				attack(ch);
				getTimeStream().flushEvents();
				return;
			}
		}
		throw new Error("No character with id "+data.aimId);
	}
	public void aStep(String message) throws InterruptedException {
		int dx, dy;
		switch (gson.fromJson(message, ClientMessageStep.class).dir) {
			case 0: dx =  0; dy = -1; break;
			case 1:	dx =  1; dy = -1; break;
			case 2:	dx =  1; dy =  0; break;
			case 3:	dx =  1; dy =  1; break;
			case 4:	dx =  0; dy =  1; break;
			case 5:	dx = -1; dy =  1; break;
			case 6: dx = -1; dy =  0; break;
			default: dx = -1; dy = -1;
		}
		step(x + dx, y + dy);
		timeStream.flushEvents();
	}
	public void aPutOn(String message) throws InterruptedException {
		// put on an item
		// v - item id
		int itemId = gson.fromJson(message,	ClientMessagePutOn.class).itemId;
		putOn(inventory.getUnique(itemId), false);
		timeStream.flushEvents();
	}
	public void aTakeOff(String message) throws InterruptedException {
		// take off an item
		// v - item id
		int itemId = gson.fromJson(message,
				ClientMessageTakeOff.class).itemId;
		takeOff(body.getItem(itemId));
		timeStream.flushEvents();
	}
	public void aPickUpPile(String message) throws InterruptedException {
		ClientMessagePickUpPile data = gson.fromJson(message,
				ClientMessagePickUpPile.class);
		pickUp(plane.getItems(x, y).getPile(data.typeId).separatePile(data.amount));
		timeStream.flushEvents();
	}
	public void aPickUpUnique(String message) throws InterruptedException {
		ClientMessagePickUpUnique data = gson.fromJson(message,
				ClientMessagePickUpUnique.class);
		pickUp(plane.getItems(x, y).getUnique(data.itemId));
		timeStream.flushEvents();
	}
	public void aDropPile(String message) throws InterruptedException {
		// drop an item
		ClientMessageDropPile data = gson.fromJson(message,
				ClientMessageDropPile.class);
		drop(inventory.getPile(data.typeId).separatePile(data.amount));
		getTimeStream().flushEvents();
	}
	public void aDropUnique(String message) throws InterruptedException {
		// drop an item
		ClientMessageDropUnique data = gson.fromJson(message,
				ClientMessageDropUnique.class);
		drop(inventory.getUnique(data.itemId));
		getTimeStream().flushEvents();
	}
	public void aDeauth(String message) throws InterruptedException {
			deauthorize();
	}
	public void aChatMessage(String message) throws InterruptedException {
		ClientMessageChatMessage data = gson.fromJson(message, ClientMessageChatMessage.class);
		say(data.text);
	}
	public void aTakeFromContainer(String message) throws InterruptedException {
		ClientMessageTakeFromContainer data = gson.fromJson(message, ClientMessageTakeFromContainer.class);
		Container container = plane.getChunkWithCell(data.x, data.y).getContainer(data.x, data.y);
		if (StaticData.getItemType(data.typeId).isStackable()) {
			takeFromContainer(container.getPile(data.typeId).separatePile(data.param), container);
		} else {
			takeFromContainer(container.getUnique(data.param), container);
		}			
		timeStream.flushEvents();
	}
	public void aPutToContainer(String message) throws InterruptedException {
		ClientMessageTakeFromContainer data = gson.fromJson(message, ClientMessageTakeFromContainer.class);
		Container container = plane.getChunkWithCell(data.x, data.y).getContainer(data.x, data.y);
		if (StaticData.getItemType(data.typeId).isStackable()) {
			super.putToContainer(inventory.getPile(data.typeId).separatePile(data.param), container);
		} else {
			super.putToContainer(inventory.getUnique(data.param), container);
		}
		timeStream.flushEvents();
	}
	public void aCastSpell(String message) throws InterruptedException {
		ClientMessageCastSpell data = gson.fromJson(message, ClientMessageCastSpell.class);
		castSpell(data.spellId, data.x, data.y);
		timeStream.flushEvents();
	}
	public void aShootMissile(String message) throws InterruptedException {
		ClientMessageShootMissile data = gson.fromJson(message, ClientMessageShootMissile.class);
		if (data.unique) {
			shootMissile(data.x, data.y, inventory.getUnique(data.missile));
		} else {
			shootMissile(data.x, data.y, inventory.getPile(data.missile).separatePile(1));
		}
		timeStream.flushEvents();
	}
	public void aUseObject(String message) throws InterruptedException {
		ClientMessageUseObject data = gson.fromJson(message, ClientMessageUseObject.class);
		useObject(data.x, data.y);
		timeStream.flushEvents();
	}
	public void aAnswer(String message) throws InterruptedException {
		ClientMessageAnswer messageAnswer = gson.fromJson(message, ClientMessageAnswer.class);
		dialogueAnswer(messageAnswer.answerId);
	}
	public void aStartConversation(String message) throws InterruptedException {
		ClientMessageStartConversation data = gson.fromJson(message, ClientMessageStartConversation.class);
		startConversation(data.characterId);
	}
	public void aIdle(String message) {
		idle();
		timeStream.flushEvents();
	}
	public boolean isAuthorized() {
		return isAuthorized;
	}
	public void aPush(String message) {
		ClientMessagePush data = gson.fromJson(message, ClientMessagePush.class);
		push(plane.getChunkWithCell(x, y).getCell(data.x, data.y).character(), Directions.intToDirection(data.direction));
		timeStream.flushEvents();
	}
	public void aChangePlaces(String message) {
		ClientMessageChangePlaces data = gson.fromJson(message, ClientMessageChangePlaces.class);
		changePlaces(plane.getChunkWithCell(x, y).getCell(data.x, data.y).character());
		timeStream.flushEvents();
	}
	public void aMakeSound(String message) {
		ClientMessageMakeSound data = gson.fromJson(message, ClientMessageMakeSound.class);
		makeSound(StaticData.getSoundType(data.type));
		timeStream.flushEvents();
	}
	public void aJump(String message) {
		ClientMessageJump data = gson.fromJson(message, ClientMessageJump.class);
		jump(data.x, data.y);
		getTimeStream().flushEvents();
	}
	public void aShieldBash(String message) {
		ClientMessageShieldBash data = gson.fromJson(message, ClientMessageShieldBash.class);
		Character aim = plane.getChunkWithCell(x, y).getCell(data.x, data.y).character();
		if (aim == null) {
			shieldBash(data.x, data.y);
		} else {
			shieldBash(aim);
		}
		timeStream.flushEvents();		
	}
	public boolean inTimeStream(TimeStream timeStream) {
		return this.getTimeStream() == timeStream; 
	}
	
	public void aEnterState(String message) {
		ClientMessageEnterState data = gson.fromJson(message, ClientMessageEnterState.class);
		enterState(CharacterState.int2state(data.stateId));
		timeStream.flushEvents();
	}
}
