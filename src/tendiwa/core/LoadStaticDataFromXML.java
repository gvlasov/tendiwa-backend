package tendiwa.core;

import com.google.common.collect.ImmutableSet;
import com.sun.codemodel.*;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;

import static org.w3c.dom.Node.ELEMENT_NODE;

public class LoadStaticDataFromXML {
static final JCodeModel[] codeModels;
private static final EdgeFactory<BodyPartTypeInstance, DefaultEdge> defaultEdgeFactory = new EdgeFactory<BodyPartTypeInstance, DefaultEdge>() {
	public DefaultEdge createEdge(BodyPartTypeInstance v1, BodyPartTypeInstance v2) {
		return new DefaultEdge();
	}
};
private static final JCodeModel soundsCodeModel = new JCodeModel();
private static final JCodeModel charactersCodeModel = new JCodeModel();
private static final JCodeModel objectsCodeModel = new JCodeModel();
private static final JCodeModel floorsCodeModel = new JCodeModel();
private static final JCodeModel itemsCodeModel = new JCodeModel();
private static final JCodeModel materialsCodeModel = new JCodeModel();
private static final JDefinedClass soundsClass;
private static final JDefinedClass charactersClass;
private static final JDefinedClass objectsClass;
private static final JDefinedClass floorsClass;
private static final JDefinedClass itemsClass;
private static final JDefinedClass materialsClass;
private static final String staticDataPackageName = "tendiwa.resources.";
private static final String schemaFileName = "schema.xsd";
private static final JDefinedClass[] definedClasses;
private static final String GENERATED_CLASS_COMMENT_TEXT = "Do not modify!\n\n" +
	"This class was automatically generated.\n\n" +
	"To define your own game resources, you need to describe them in data/*.xml files \n" +
	"and then run build.";

static {
	// Define classes in code models
	JDefinedClass itemsClass1;
	JDefinedClass floorsClass1;
	JDefinedClass objectsClass1;
	JDefinedClass charactersClass1;
	JDefinedClass soundsClass1;
	JDefinedClass materialsClass1;
	try {
		soundsClass1 = soundsCodeModel._class(staticDataPackageName + "SoundTypes");
		charactersClass1 = charactersCodeModel._class(staticDataPackageName + "CharacterTypes");
		objectsClass1 = objectsCodeModel._class(staticDataPackageName + "ObjectTypes");
		floorsClass1 = floorsCodeModel._class(staticDataPackageName + "FloorTypes");
		itemsClass1 = itemsCodeModel._class(staticDataPackageName + "ItemTypes");
		materialsClass1 = materialsCodeModel._class(staticDataPackageName + "MaterialTypes");
	} catch (JClassAlreadyExistsException e) {
		soundsClass1 = null;
		charactersClass1 = null;
		objectsClass1 = null;
		floorsClass1 = null;
		itemsClass1 = null;
		materialsClass1 = null;
		e.printStackTrace();
	}
	itemsClass = itemsClass1;
	soundsClass = soundsClass1;
	charactersClass = charactersClass1;
	objectsClass = objectsClass1;
	floorsClass = floorsClass1;
	materialsClass = materialsClass1;
	// Create an array to iterate over all code models
	codeModels = new JCodeModel[]{
		soundsCodeModel,
		charactersCodeModel,
		objectsCodeModel,
		floorsCodeModel,
		itemsCodeModel,
		materialsCodeModel
	};
	definedClasses = new JDefinedClass[]{
		soundsClass,
		charactersClass,
		objectsClass,
		floorsClass,
		itemsClass,
		materialsClass
	};
	// Add comments to each code model
	for (JDefinedClass cls : definedClasses) {
		cls.javadoc().append(GENERATED_CLASS_COMMENT_TEXT);
	}
}

/**
 * Reads .xml file with game objects' text descriptions and loads the descriptions into memory as data structures to be
 * used in in-game calculations and client-side static data building.
 *
 * @param filename
 * 	Path to .xml data file.
 */
public static void loadGameDataFromXml(String pathToResource) {
	InputStream xmlResource = getResourceFileInputStream(pathToResource);
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = null;
	try {
		dBuilder = dbFactory.newDocumentBuilder();
	} catch (ParserConfigurationException e) {
		e.printStackTrace();
	}
	Document doc = null;
	try {
		doc = dBuilder.parse(xmlResource);
	} catch (SAXException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	Element eRoot = doc.getDocumentElement();

	xmlResource = getResourceFileInputStream(pathToResource);
	String textOfFile = new Scanner(xmlResource, "UTF-8").useDelimiter("\\A").next();
	try {
		URL resource = LoadStaticDataFromXML.class.getResource("/" + schemaFileName);
		validate(
			textOfFile,
			resource
		);
	} catch (SAXException e) {
		e.printStackTrace();
		System.exit(1);
	} catch (IOException e) {
		e.printStackTrace();
		System.exit(1);
	}
	removeWhitespaceTextNodes(eRoot);

	// Remove all blank text nodes between elements
	// (these nodes are there because XML is formatted in
	// a human-readable way).
	loadCharacters(eRoot);
	loadMaterials(eRoot);
	loadSounds(eRoot);
	loadItems(eRoot);
	loadObjects(eRoot);
	loadFloors(eRoot);
}

private static InputStream getResourceFileInputStream(String pathToResource) {
	try {
		return new FileInputStream(new File(pathToResource));
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	return null;
}

private static void removeWhitespaceTextNodes(Node node) {
	NodeList nlChildren = node.getChildNodes();
	for (int i = nlChildren.getLength() - 1; i > -1; i--) {
		Node nChild = nlChildren.item(i);
		if (nChild.getNodeType() == Node.ELEMENT_NODE) {
			removeWhitespaceTextNodes(nChild);
		}
		if (nChild.getNodeType() == Node.TEXT_NODE && nChild.getNodeValue().trim().length() == 0) {
			nChild.getParentNode().removeChild(nChild);
		}
	}
}

public static boolean validate(String inputXml, URL schemaFile)
	throws SAXException, IOException {
	SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
	Schema schema = factory.newSchema(schemaFile);
	Validator validator = schema.newValidator();

	Source source = new StreamSource(new StringReader(inputXml));

	boolean isValid = true;
	try {
		validator.validate(source);
	} catch (SAXException e) {
		e.printStackTrace();
		isValid = false;
	}

	return isValid;
}

private static void loadSounds(Element eRoot) {
	Element eSounds = (Element) eRoot.getElementsByTagName("sounds").item(0);
	JClass clsSoundType = soundsCodeModel.ref(SoundType.class);
	for (Element eSound = (Element) eSounds.getFirstChild(); eSound != null; eSound = (Element) eSound.getNextSibling()) {
		String name = eSound.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
		int bass = Integer.parseInt(eSound.getElementsByTagName("bass").item(0).getFirstChild().getNodeValue());
		int mid = Integer.parseInt(eSound.getElementsByTagName("mid").item(0).getFirstChild().getNodeValue());
		int treble = Integer.parseInt(eSound.getElementsByTagName("treble").item(0).getFirstChild().getNodeValue());
//		SoundType soundType = new SoundType(name, bass, mid, treble);
//		StaticData.add(soundType);

		soundsClass.field(
			JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
			SoundType.class,
			name,
			JExpr._new(clsSoundType)
				.arg(JExpr.lit(name))
				.arg(JExpr.lit(bass))
				.arg(JExpr.lit(mid))
				.arg(JExpr.lit(treble))
		);
	}
}

private static void loadCharacters(Element eRoot) {
	Element eCharacters = (Element) eRoot.getElementsByTagName("characters").item(0);
	JClass clsCharacterType = charactersCodeModel.ref(CharacterType.class);
	for (Element eCharacter = (Element) eCharacters.getFirstChild(); eCharacter != null; eCharacter = (Element) eCharacter.getNextSibling()) {
		// Form a CharacterType object
		String name = eCharacter.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
		double weight = Double.parseDouble(eCharacter.getElementsByTagName("weight").item(0).getFirstChild().getNodeValue());
		double height = Double.parseDouble(eCharacter.getElementsByTagName("height").item(0).getFirstChild().getNodeValue());

		JClass clsHashSet = charactersCodeModel.ref(ImmutableSet.class);
		JClass clsCharacterAspect = charactersCodeModel.ref("tendiwa.core.CharacterAspect");
		assert clsCharacterAspect != null;
		JInvocation invSet = clsHashSet.staticInvoke("<CharacterAspect>builder");
		for (Element eAspect = (Element) eCharacter.getElementsByTagName("aspects").item(0).getFirstChild(); eAspect != null; eAspect = (Element) eAspect.getNextSibling()) {
			// For each aspect inside XML element <aspects/> add a
			// CharacterAspect to CharacterType
			String tagName = eAspect.getTagName();
			invSet = invSet
				.invoke("add")
				.arg(clsCharacterAspect.staticInvoke("getByName").arg(JExpr.lit(tagName)));
		}
		invSet = invSet.invoke("build");
//		DirectedGraph<BodyPartTypeInstance, DefaultEdge> bodyGraph = xml2BodyGraph((Element) eCharacter.getElementsByTagName("body").item(0).getFirstChild());
//		CharacterType characterType = new CharacterType(name, aspects, weight, height, bodyGraph);
//		StaticData.add(characterType);
		charactersClass.field(
			JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
			CharacterType.class,
			name,
			JExpr._new(clsCharacterType)
				.arg(JExpr.lit(name))
				.arg(invSet)
				.arg(JExpr.lit(weight))
				.arg(JExpr.lit(height))
		);
//		System.out.println("Character " + name + " loaded");
	}
}

private static void loadMaterials(Element eRoot) {
	Element eMaterials = (Element) eRoot.getElementsByTagName("materials").item(0);
	JClass clsMaterial = materialsCodeModel.ref(Material.class);
	for (Element eMaterial = (Element) eMaterials.getFirstChild(); eMaterial != null; eMaterial = (Element) eMaterial.getNextSibling()) {
		int durability = Integer.parseInt(eMaterial.getElementsByTagName("durability").item(0).getFirstChild().getNodeValue());
		String name = eMaterial.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
		int density = Integer.parseInt(eMaterial.getElementsByTagName("density").item(0).getFirstChild().getNodeValue());
		Material material = new Material(name, durability, density);
		StaticData.add(material);
		materialsClass.field(
			JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
			Material.class,
			name,
			JExpr._new(clsMaterial)
				.arg(JExpr.lit(name))
				.arg(JExpr.lit(durability))
				.arg(JExpr.lit(density))
		);
	}
}

private static void loadObjects(Element eRoot) {
	Element eObjects = (Element) eRoot.getElementsByTagName("objects").item(0);

	JClass clsObjectType = objectsCodeModel.ref(ObjectType.class);

	for (Element eObject = (Element) eObjects.getFirstChild(); eObject != null; eObject = (Element) eObject.getNextSibling()) {
		String name = eObject.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
		Element ePassability = (Element) eObject.getElementsByTagName("passability").item(0);
		int passability = StaticData.PASSABILITY_NONE;
		boolean isUsable;
		for (Element ePassType = (Element) ePassability.getFirstChild(); ePassType != null; ePassType = (Element) ePassType.getNextSibling()) {
			/*
			 * <passability /> element can contain several flag elements,
			 * that determine what types of passability this ObjectType has:
			 * visual, walkable.
			 */
			if (ePassType.getTagName().equals("none")) {
				passability = StaticData.PASSABILITY_NONE;
				break;
			}
			if (ePassType.getTagName().equals("all")) {
				passability = StaticData.PASSABILITY_VISUAL + StaticData.PASSABILITY_WALKABLE;
				break;
			}
			if (ePassType.getTagName().equals("visual")) {
				passability += StaticData.PASSABILITY_VISUAL;
			} else if (ePassType.getTagName().equals("walkable")) {
				passability += StaticData.PASSABILITY_WALKABLE;
			}
		}
		if (eObject.getElementsByTagName("usable").getLength() == 1) {
			isUsable = true;
		} else {
			isUsable = false;
		}
		int objectClass = ObjectType.CLASS_DEFAULT;
		if (eObject.getElementsByTagName("type").getLength() == 1) {
			String objectClassAsString = eObject.getElementsByTagName("type").item(0).getFirstChild().getNodeValue();
			if (objectClassAsString.equals("wall")) {
				objectClass = ObjectType.CLASS_WALL;
			} else if (objectClassAsString.equals("door")) {
				objectClass = ObjectType.CLASS_DOOR;
			} else if (objectClassAsString.equals("interlevel")) {
				objectClass = ObjectType.CLASS_INTERLEVEL;
			} else {
				throw new RuntimeException("Unknown object class " + objectClassAsString);
			}
		}
		ObjectType objectType = new ObjectType(name, passability, isUsable, objectClass);
		StaticData.add(objectType);
		if (objectClass == ObjectType.CLASS_DOOR) {
			/*
			 * There are 2 ObjectTypes associated with each door: open and
			 * closed. The default one is closed, then we should register
			 * the open one.
			 */
			StaticData.add(new ObjectType(name + "_open", StaticData.PASSABILITY_PENETRABLE + StaticData.PASSABILITY_VISUAL + StaticData.PASSABILITY_WALKABLE, isUsable, objectClass));
		}

		objectsClass.field(
			JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
			ObjectType.class,
			name,
			JExpr._new(clsObjectType)
				.arg(JExpr.lit(name))
				.arg(JExpr.lit(passability))
				.arg(JExpr.lit(isUsable))
				.arg(JExpr.lit(objectClass))
		);
	}
}

private static void loadFloors(Element eRoot) {
	Element eFloors = (Element) eRoot.getElementsByTagName("floors").item(0);
	JClass clsFloorType = floorsCodeModel.ref(FloorType.class);
	for (Element eFloor = (Element) eFloors.getFirstChild(); eFloor != null; eFloor = (Element) eFloor.getNextSibling()) {
		String name = eFloor.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
//		FloorType floorType = new FloorType(name);
//		StaticData.add(floorType);
		floorsClass.field(
			JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
			FloorType.class,
			name,
			JExpr._new(clsFloorType)
				.arg(JExpr.lit(name))
		);
	}
}

private static Element getChild(Element parent, String name) {
	for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
		if (child instanceof Element && name.equals(child.getNodeName())) {
			return (Element) child;
		}
	}
	return null;
}

private static void loadItems(Element eRoot) {
	JClass clsHashSet = itemsCodeModel.ref(ImmutableSet.class);
	JClass clsItemAspect = itemsCodeModel.ref(Aspect.class);
	JClass clsItemType = itemsCodeModel.ref(ItemType.class);
	JClass clsAspectRangedWeapon = itemsCodeModel.ref(AspectRangedWeapon.class);
	JClass clsAspectApparel = itemsCodeModel.ref(AspectApparel.class);
	JClass clsAspectContainer = itemsCodeModel.ref(AspectContainer.class);
	// This class is referred through a string because it is created dynamically in modules, not in framework.
	JClass clsMaterialTypes = itemsCodeModel.ref("tendiwa.resources.MaterialTypes");

	// TODO: This is a hack. I couldn't find another way to import tendiwa.core.Aspect to the resulting class.
	itemsClass.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, clsItemAspect, "ASPECT", JExpr._null());

	for (Element eItem = (Element) eRoot.getElementsByTagName("items").item(0).getFirstChild(); eItem != null; eItem = (Element) eItem.getNextSibling()) {
		// Parsing item type's properties
		String name = eItem.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
		String materialName = getChild(eItem, "material").getFirstChild().getNodeValue();

		JInvocation expAspects = clsHashSet.staticInvoke("<Aspect>builder");

		double weight = Double.parseDouble(eItem.getElementsByTagName("weight").item(0).getFirstChild().getNodeValue());
		double volume = Double.parseDouble(eItem.getElementsByTagName("volume").item(0).getFirstChild().getNodeValue());
		boolean stackable;
		if (eItem.getElementsByTagName("stackable").getLength() == 1) {
			stackable = true;
		} else {
			stackable = false;
		}
		if (eItem.getElementsByTagName("stackable").getLength() == 1) {
			stackable = true;
		}
		// Parsing item type's aspects
		Node nAspects = eItem.getElementsByTagName("aspects").item(0);
		if (nAspects != null) {
			for (Element eAspect = (Element) nAspects.getFirstChild(); eAspect != null; eAspect = (Element) eAspect.getNextSibling()) {
				if (eAspect.getNodeType() != ELEMENT_NODE) {
					continue;
				}
				if (eAspect.getTagName().equals("rangedWeapon")) {
					String sAmmoType = eAspect.getElementsByTagName("ammo").item(0).getFirstChild().getNodeValue();
					int iReloadTime = Integer.parseInt(eAspect.getElementsByTagName("reloadTime").item(0).getFirstChild().getNodeValue());
					int iAimTime = Integer.parseInt(eAspect.getElementsByTagName("aimTime").item(0).getFirstChild().getNodeValue());
					int iMagazine = Integer.parseInt(eAspect.getElementsByTagName("magazine").item(0).getFirstChild().getNodeValue());
//					aspectBuilder.add(new AspectRangedWeapon(iReloadTime, iAimTime, iMagazine, sAmmoType));

					JExpression expRangedWeaponAspect = JExpr
						._new(clsAspectRangedWeapon)
						.arg(JExpr.lit(iReloadTime))
						.arg(JExpr.lit(iAimTime))
						.arg(JExpr.lit(iMagazine))
						.arg(JExpr.lit(sAmmoType));
					expAspects = expAspects.invoke("add").arg(expRangedWeaponAspect);
				}
				if (eAspect.getTagName().equals("craftable")) {

				}
				if (eAspect.getTagName().equals("apparel")) {
//					Graph<BodyPartTypeInstance, DefaultEdge> form = xml2BodyGraph((Element) eAspect.getElementsByTagName("form").item(0).getFirstChild());
					HashSet<BodyPartType> covers = new HashSet<BodyPartType>();
					HashSet<BodyPartType> blocks = new HashSet<BodyPartType>();
					if (eAspect.getElementsByTagName("covers").getLength() > 0) {
						for (Element eCovers = (Element) eAspect.getElementsByTagName("covers").item(0).getFirstChild(); eCovers != null; eCovers = (Element) eCovers.getNextSibling()) {
							covers.add(BodyPartType.string2BodyPart(eCovers.getTagName()));
						}
					}
					if (eAspect.getElementsByTagName("blocks").getLength() > 0) {
						for (Element eBlocks = (Element) eAspect.getElementsByTagName("blocks").item(0).getFirstChild(); eBlocks != null; eBlocks = (Element) eBlocks.getNextSibling()) {
							blocks.add(BodyPartType.string2BodyPart(eBlocks.getTagName()));
						}
					}
//					aspects.add(new AspectApparel(form, covers, blocks));

					JExpression expApparel = JExpr
						._new(clsAspectApparel);
					expAspects = expAspects.invoke("add").arg(expApparel);

				}
				if (eAspect.getTagName().equals("container")) {
					double containerVolume = Double.parseDouble(eAspect.getElementsByTagName("volume").item(0).getFirstChild().getNodeValue());
					boolean liquidAllowing;
					if (eAspect.getElementsByTagName("liquidAllowing").getLength() == 1) {
						liquidAllowing = true;
					} else {
						liquidAllowing = false;
					}
//					aspects.add(new AspectContainer(containerVolume, liquidAllowing));

					JExpression expAspectContainer = JExpr
						._new(clsAspectContainer)
						.arg(JExpr.lit(containerVolume))
						.arg(JExpr.lit(liquidAllowing));
					expAspects = expAspects.invoke("add").arg(expAspectContainer);
				}
			}
		}
//		ItemType itemType = new ItemType(name, aspects, weight, volume, material, stackable);
//		StaticData.add(itemType);

		JFieldRef expMaterial = clsMaterialTypes
			.staticRef(materialName);
		expAspects = expAspects.invoke("build");
		itemsClass.field(
			JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
			ItemType.class,
			name,
			JExpr._new(clsItemType)
				.arg(JExpr.lit(name))
				.arg(expAspects)
				.arg(JExpr.lit(weight))
				.arg(JExpr.lit(volume))
				.arg(expMaterial)
				.arg(JExpr.lit(stackable))
		);
		System.out.println("Created item " + name);
	}
}

/**
 * Creates a graph of body organs from xml.
 *
 * @param eRoot
 * 	The root element of a body organ structure (not <form/> element, but the root body organ).
 * @return
 */
private static DirectedGraph<BodyPartTypeInstance, DefaultEdge> xml2BodyGraph(Element eRoot) {
	DefaultDirectedGraph<BodyPartTypeInstance, DefaultEdge> graph = new DefaultDirectedGraph<BodyPartTypeInstance, DefaultEdge>(defaultEdgeFactory);
	BodyPartTypeInstance rootVertex = new BodyPartTypeInstance(BodyPartType.string2BodyPart(eRoot.getTagName()));
	graph.addVertex(rootVertex);
	xml2Graph(eRoot, graph, rootVertex);
	return graph;
}

/**
 * A recursive helper method for {@link LoadStaticDataFromXML#xml2BodyGraph(Element)}.
 *
 * @param eRoot
 * @param graph
 * @param rootVertex
 */
private static void xml2Graph(Element eRoot, DefaultDirectedGraph<BodyPartTypeInstance, DefaultEdge> graph, BodyPartTypeInstance rootVertex) {
	for (Element eChild = (Element) eRoot.getFirstChild(); eChild != null; eChild = (Element) eChild.getNextSibling()) {
		BodyPartTypeInstance newVertex = new BodyPartTypeInstance(BodyPartType.string2BodyPart(eChild.getTagName()));
		graph.addVertex(newVertex);
		try {
			graph.addEdge(rootVertex, newVertex);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("error adding " + newVertex.type + " to " + rootVertex.type);
		}
		xml2Graph(eChild, graph, newVertex);
	}
}
}
