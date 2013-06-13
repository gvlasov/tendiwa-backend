package tendiwa.modules;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.imageio.ImageIO;

import tendiwa.core.AspectName;
import tendiwa.core.CharacterType;
import tendiwa.core.CopyFiles;
import tendiwa.core.ItemType;
import tendiwa.core.ObjectType;
import tendiwa.core.ResourceBuilder;
import tendiwa.core.StaticData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public final class SuseikaBrowserClientResourceBuilder extends ResourceBuilder {
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(SuseikaBrowserClientResourceBuilder.class, new JsonSerializer<SuseikaBrowserClientResourceBuilder>() {
		@Override
		/**
		 * <p>Serializes SuseikaBrowserClientResourceBuilder's resource data as:</p>
		 * <pre>
		 * [obj1Name, obj1Width, obj1Height, obj2Name, obj2Width, obj2Height, ...]
		 * </pre>
		 * @param module
		 * @param typeOfSrc
		 * @param context
		 * @return
		 */
		public JsonElement serialize(SuseikaBrowserClientResourceBuilder module, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray jArray = new JsonArray();
			for (Map.Entry<Integer, Integer[]> e : objectSizes.entrySet()) {
				jArray.add(new JsonPrimitive(e.getKey()));
				jArray.add(new JsonPrimitive(e.getValue()[0]));
				jArray.add(new JsonPrimitive(e.getValue()[1]));
			}
			return jArray;
		}
	}).create();
	/**
	 * Sizes of all object images in pixels. Though walls have several images
	 * associated with them, they still have only one width and one height value
	 * saved here, because all the variants of the same wall must have the same
	 * with and height in pixels.
	 */
	private static HashMap<Integer, Integer[]> objectSizes = new HashMap<Integer, Integer[]>();
	/**
	 * Registered item types that have their resources are saved here.
	 */
	private static HashSet<ItemType> itemsWithResources = new HashSet<ItemType>();
	private static final String clientFolder = "/home/suseika/client/";

	String modulePath = "/home/suseika/workspace/Erpoge Server/src/erpoge/modules/images/";
	String wallsPath = modulePath + "walls/";
	String itemsPath = modulePath + "items/";
	String chardollPath = modulePath + "chardoll/";
	String particlesPath = modulePath + "particles/";
	String charactersPath = modulePath + "characters/";

	private static int amountOfPreviousResourceFiles = 0;

	public SuseikaBrowserClientResourceBuilder() {

	}

	@Override
	public String buildResourcesStaticData() {
		// Implemented in GsonBuilder at class initialization.
		return gson.toJson(this);
	}

	/**
	 * Implementation of {@link ResourceBuilder#build()} for the Suseika's
	 * browser client. Uses methods starting from "build..." in
	 * {@link SuseikaBrowserClientResourceBuilder}.
	 */
	public void build(String clientPath) {
		cleanClientResourcesDirectory(clientFolder);
		System.out.println("Deleted " + amountOfPreviousResourceFiles + " files");
		buildWalls();
		buildItems();
		buildApparel();
		buildWieldable();
		buildCharacters();
	}

	/**
	 * Builds resources
	 */
	private void buildCharacters() {
		/*
		 * Use CharacterType ids intead of CharacterType names in resource
		 * files: %goblin.png% copies to %263.png%
		 */
		File[] files = new File(charactersPath).listFiles();
		HashSet<String> resourcesWithoutType = new HashSet<String>();
		HashSet<CharacterType> typesWithoutResources = new HashSet<CharacterType>();
		HashSet<CharacterType> registeredTypesWithResources = new HashSet<CharacterType>();
		for (File file : files) {
			if (file.isDirectory()) {
				continue;

			}
			String filename = file.getName();
			String characterName = filename.substring(0, filename.length() - 4);
			if (!StaticData.characterTypeExists(characterName)) {
				// If resource exists, but its type wasn't registered, remember
				// the resource name
				resourcesWithoutType.add(characterName);
				continue;
			}
			CharacterType type = StaticData.getCharacterType(characterName);
			try {
				CopyFiles.copyWithStreams(file, new File(clientFolder + "characters/" + type.getId() + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			registeredTypesWithResources.add(type);
		}
		for (CharacterType type : StaticData.getAllCharacterTypes()) {
			if (!registeredTypesWithResources.contains(type)) {
				typesWithoutResources.add(type);
			}
		}
		System.out.println("---\tWCharacters statistics:");
		System.out.println(resourcesWithoutType.size() + "\tcharacter types resources without type: " + resourcesWithoutType);
		System.out.println(typesWithoutResources.size() + "\tcharacterTypes types without resources: " + typesWithoutResources);
		System.out.println(registeredTypesWithResources.size() + "\tcharacterTypes with both registered types and resources: " + registeredTypesWithResources);
	}

	private void buildWieldable() {
		/*
		 * Almost all items are supposed to be wieldable, so each of them should
		 * have an image of that item held in hand.
		 */
		File[] files = new File(chardollPath + "wielded").listFiles();
		HashSet<String> resourcesWithoutType = new HashSet<String>();
		HashSet<ItemType> typesWithoutResources = new HashSet<ItemType>();
		HashSet<ItemType> registeredTypesWithResources = new HashSet<ItemType>();
		for (File file : files) {
			String filename = file.getName();
			String itemName = filename.substring(0, filename.length() - 4);
			if (!StaticData.itemTypeExists(itemName)) {
				// If resource exists, but its type wasn't registered, remember
				// the resource name
				resourcesWithoutType.add(itemName);
				continue;
			}
			ItemType type = StaticData.getItemType(itemName);
			try {
				CopyFiles.copyWithStreams(file, new File(clientFolder + "chardoll/wielded/" + type.getId() + ".png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			registeredTypesWithResources.add(type);
		}
		for (ItemType type : StaticData.getAllItems()) {
			if (!registeredTypesWithResources.contains(type)) {
				typesWithoutResources.add(type);
			}
		}
		System.out.println("---\tWielded items statistics:");
		System.out.println(resourcesWithoutType.size() + "\titems resources without type: " + resourcesWithoutType);
		System.out.println(typesWithoutResources.size() + "\titems types without resources: " + typesWithoutResources);
		System.out.println(registeredTypesWithResources.size() + "\titems with both registered types and resources: " + registeredTypesWithResources);
	}

	private void buildApparel() {
		/*
		 * Build character doll parts graphics data. These include apparel,
		 * wielded items, bodies and body parts. Building items graphics data
		 * means moving images from build directory to game client directory
		 * changing their names from %name_of_item%.png to %id_of_item%.png
		 */
		File[] files = new File(chardollPath + "apparel").listFiles();
		HashSet<String> resourcesWithoutType = new HashSet<String>();
		HashSet<ItemType> typesWithoutResources = new HashSet<ItemType>();
		HashSet<ItemType> registeredTypesWithResources = new HashSet<ItemType>();

		for (File file : files) {
			String filename = file.getName();
			String apparelName = filename.substring(0, filename.lastIndexOf("."));
			if (!StaticData.itemTypeExists(apparelName)) {
				resourcesWithoutType.add(apparelName);
				// System.out.println("Apparel "+apparelName+" is not registered, but has a resource");
				continue;
			}
			ItemType type = StaticData.getItemType(apparelName);
			registeredTypesWithResources.add(type);
			copyApparelImageToClientFolder(apparelName, type.getId());
		}
		for (ItemType type : StaticData.getAllItems()) {
			if (!type.hasAspect(AspectName.APPAREL)) {
				continue;
			}
			if (!registeredTypesWithResources.contains(type)) {
				typesWithoutResources.add(type);
				// System.out.println("ItemType "+type.getName()+" doesn't have a resource");
			}
		}
		System.out.println("---\tApparel statistics:");
		System.out.println(resourcesWithoutType.size() + "\tapparel resources without type: " + resourcesWithoutType);
		System.out.println(typesWithoutResources.size() + "\tapparel types without resources: " + typesWithoutResources);
		System.out.println(registeredTypesWithResources.size() + "\tapparel with both registered types and resources: " + registeredTypesWithResources);
	}

	private void buildWalls() {
		/*
		 * Build walls graphical data. Building walls graphics data means moving
		 * images from build directory to game client directory changing their
		 * names from name_of_wall%.png to %id_of_wall%.png
		 */
		File[] files = new File(wallsPath).listFiles();
		HashSet<String> resourcesWithoutType = new HashSet<String>();
		HashSet<String> resourcesWithDimensionProblems = new HashSet<String>();
		HashSet<ObjectType> typesWithoutResources = new HashSet<ObjectType>();
		int numOfBothResourceAndTypePresent = 0;
		HashMap<String, HashMap<String, BufferedImage>> wallImages = new HashMap<String, HashMap<String, BufferedImage>>();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				continue;
			}
			// File name without extension
			String fileName = file.getName().substring(0, file.getName().length() - 4);
			/*
			 * Get the name of a wall type. A wall name of a file like
			 * "stone_wall_1011.png" is "stone_wall".
			 */
			String wallName = fileName.substring(0, fileName.lastIndexOf("_"));
			/*
			 * A String of 4 numbers 1 or 0, indicating the presence of neighbor
			 * walls
			 */
			String wallSide = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.length());
			HashMap<String, BufferedImage> thisWallImages;
			if (wallImages.containsKey(wallName)) {
				thisWallImages = wallImages.get(wallName);
			} else {
				thisWallImages = new HashMap<String, BufferedImage>();
				wallImages.put(wallName, thisWallImages);
			}
			BufferedImage image = null;
			try {
				image = ImageIO.read(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			thisWallImages.put(wallSide, image);
		}
		/*
		 * Check if all the wall images of each type have the same width and
		 * height within their type. If a wall type has images with different
		 * width or height, add such type to resourcesWithDimensionProblems
		 * collection.
		 */
		loop: for (String wallName : wallImages.keySet()) {
			BufferedImage defaultWallImage = wallImages.get(wallName).get("0000");
			if (defaultWallImage == null) {
				throw new NullPointerException("Wall image 0000 does not exist in images/walls directory for wall " + wallName);
			}
			int neededWidth = defaultWallImage.getWidth();
			int neededHeight = defaultWallImage.getHeight();
			for (BufferedImage image : wallImages.get(wallName).values()) {
				if (image.getWidth() != neededWidth || image.getHeight() != neededHeight) {
					resourcesWithDimensionProblems.add(wallName);
					continue loop;
				}
			}
			ObjectType type = null;
			if (StaticData.objectTypeExists(wallName)) {
				numOfBothResourceAndTypePresent++;
				type = StaticData.getObjectType(wallName);
				copyWallImagesToClientFolder(wallName, type.getId());
				objectSizes.put(StaticData.getObjectType(wallName).getId(), new Integer[] {
					neededWidth, neededHeight });
			} else {
				resourcesWithoutType.add(wallName);
			}
		}
		/*
		 * Check if all the registered item types have their resources
		 */
		for (ObjectType type : StaticData.getAllObjectTypes()) {
			// Iterate over all registered object types
			if (type.getObjectClass() == ObjectType.CLASS_WALL && !wallImages.containsKey(type.getName())) {
				/*
				 * If an object type is a wall and a resource for it was not
				 * loaded in this method, save it.
				 */
				typesWithoutResources.add(type);
			}
		}
		System.out.println("---\tWalls statistics:");
		System.out.println(resourcesWithoutType.size() + "\twall resources without type: " + resourcesWithoutType);
		System.out.println(typesWithoutResources.size() + "\twall types without resources: " + typesWithoutResources);
		System.out.println(resourcesWithDimensionProblems.size() + "\twall resources with dimension problems: " + resourcesWithDimensionProblems);
		System.out.println(numOfBothResourceAndTypePresent + "\twalls with both registered types and resources");
	}

	private void buildItems() {
		/*
		 * Build items graphics data. Building items graphics data means moving
		 * images from build directory to game client directory changing their
		 * names from %name_of_item%.png to %id_of_item%.png
		 */
		File[] files = new File(itemsPath).listFiles();
		HashSet<ItemType> typesWitoutResources = new HashSet<ItemType>();
		HashSet<String> resourcesWithoutTypes = new HashSet<String>();
		for (File file : files) {
			String filename = file.getName();
			String itemName = filename.substring(0, filename.lastIndexOf("."));
			ItemType type = null;
			if (StaticData.itemTypeExists(itemName)) {
				type = StaticData.getItemType(itemName);
				itemsWithResources.add(type);
			} else {
				resourcesWithoutTypes.add(itemName);
				continue;
			}
			copyItemImageToClientFolder(itemName, type.getId());
		}
		/*
		 * Check if all the registered
		 */
		for (ItemType registeredItemType : StaticData.getAllItems()) {
			if (!itemsWithResources.contains(registeredItemType)) {
				typesWitoutResources.add(registeredItemType);
			}
		}
		System.out.println("---\tItems statistics:");
		System.out.println(resourcesWithoutTypes.size() + "\titem resources without registered types: " + resourcesWithoutTypes);
		System.out.println(typesWitoutResources.size() + "\titem types without resources: " + typesWitoutResources);
		System.out.println(itemsWithResources.size() + "\titem types with both registered type and resource: " + itemsWithResources);
	}

	private void copyApparelImageToClientFolder(String itemName, int itemId) {
		try {
			CopyFiles.copyWithChannels(new File(chardollPath + "apparel/" + itemName + ".png"), new File(clientFolder + "chardoll/a" + itemId + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void copyItemImageToClientFolder(String itemName, int itemId) {
		try {
			CopyFiles.copyWithChannels(new File(itemsPath + itemName + ".png"), new File(clientFolder + "items/" + itemId + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void copyWallImagesToClientFolder(String wallName, int wallId) {
		try {
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_0000.png"), new File(clientFolder + "walls/" + wallId + "_0000.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_0001.png"), new File(clientFolder + "walls/" + wallId + "_0001.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_0010.png"), new File(clientFolder + "walls/" + wallId + "_0010.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_0011.png"), new File(clientFolder + "walls/" + wallId + "_0011.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_0100.png"), new File(clientFolder + "walls/" + wallId + "_0100.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_0101.png"), new File(clientFolder + "walls/" + wallId + "_0101.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_0110.png"), new File(clientFolder + "walls/" + wallId + "_0110.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_0111.png"), new File(clientFolder + "walls/" + wallId + "_0111.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_1000.png"), new File(clientFolder + "walls/" + wallId + "_1000.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_1001.png"), new File(clientFolder + "walls/" + wallId + "_1001.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_1010.png"), new File(clientFolder + "walls/" + wallId + "_1010.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_1011.png"), new File(clientFolder + "walls/" + wallId + "_1011.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_1100.png"), new File(clientFolder + "walls/" + wallId + "_1100.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_1101.png"), new File(clientFolder + "walls/" + wallId + "_1101.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_1110.png"), new File(clientFolder + "walls/" + wallId + "_1110.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_1111.png"), new File(clientFolder + "walls/" + wallId + "_1111.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_d0.png"), new File(clientFolder + "walls/" + wallId + "_d0.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_d1.png"), new File(clientFolder + "walls/" + wallId + "_d1.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_d2.png"), new File(clientFolder + "walls/" + wallId + "_d2.png"));
			CopyFiles.copyWithChannels(new File(wallsPath + wallName + "_d3.png"), new File(clientFolder + "walls/" + wallId + "_d3.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void cleanClientResourcesDirectory(String path) {
		File[] files = new File(path).listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				cleanClientResourcesDirectory(file.getAbsolutePath());
			}
			if (!file.delete()) {
				System.out.println("dafuq");
			}
			amountOfPreviousResourceFiles++;
		}
	}
}
