package tendiwa.core;

import java.net.MalformedURLException;

import tendiwa.locationtypes.Forest;
//import tendiwa.modules.SuseikaBrowserClientResourceBuilder;

public class Main {
	public static final int DEFAULT_PORT = 8787;
	public static int[][] arr;
	public final static boolean DEBUG = true;
	public static final String TEST_LOCATION_TYPE = "Empty";
	public static final int DEFAULT_LOCATION_WIDTH = 30;
	public static void main(String args[]) throws ClassNotFoundException, MalformedURLException {
		// Main.window = new Window();
		if (false) {
			//new SuseikaBrowserClientResourceBuilder();
		}

		ModuleLoader.loadModules();
		StaticData.showData();

		// World world = new World(20,20,"TestWorld", "Erpoge World");
		// world.showWorld();

		HorizontalPlane plane = new HorizontalPlane();
		plane.generateLocation(-20, -20, 59, 26, Forest.class);

		// PlayerHandler burok = CharacterManager.createPlayer(plane, 6, 9,
		// "Alvoi", StaticData.getCharacterType("elf"), "Warrior");
		// burok.eventlessGetItem(new UniqueItem(ItemType.CLASS_SWORD *
		// ItemsTypology.CLASS_LENGTH));
		// burok.eventlessGetItem(new ItemPile(ItemType.CLASS_AMMO *
		// ItemsTypology.CLASS_LENGTH,200));
		// burok.eventlessGetItem(new UniqueItem(ItemType.CLASS_BLUNT *
		// ItemsTypology.CLASS_LENGTH));
		// burok.eventlessGetItem(new UniqueItem(ItemType.CLASS_BOW *
		// ItemsTypology.CLASS_LENGTH));
		// burok.eventlessGetItem(new UniqueItem(ItemType.CLASS_SHIELD *
		// ItemsTypology.CLASS_LENGTH));
		// burok.eventlessGetItem(new UniqueItem(1204));
		// burok.eventlessGetItem(new UniqueItem(1102));
		// burok.learnSpell(9);
		// burok.learnSpell(10);
		// Accounts.addAccount(new Account("1","1"));
		// Accounts.addAccount(new Account("Billy","1"));
		// Accounts.account("1").addCharacter(burok);

		ConnectionServer.setDefaultPlane(plane);
		// MainHandler.startServer();
	}
}
