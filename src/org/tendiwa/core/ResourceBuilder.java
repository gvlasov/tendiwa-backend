package org.tendiwa.core;


/**
 * <p>
 * ResourceBuilder is a class that prepares game staticDataDirectory for deployment. At
 * the development phase staticDataDirectory are supposed to be located in the folders of
 * modules that add these staticDataDirectory to game. Does the following:
 * </p>
 * <ul>
 * <li>takes staticDataDirectory from all the related modules and copies them into client
 * folder, changing filenames or paths to files if needed;</li>
 * <li>builds JSON data that client needs to properly use the staticDataDirectory (for
 * example, saves width and height of images). This data will be saved in
 * client's StaticData along with all the properties of game entities (which are
 * also saved into a separate file at the building phase, see {@link StaticData}
 * for that).</li>
 * </ul>
 * 
 * <p>
 * ResourceBuilders allow developers and designers use their own form of storing
 * game graphical content during development, not dependent on the form that a
 * client uses.
 * </p>
 * 
 * <p>
 * ResourceBuilders are supposed to be implemented by the developers of clients
 * based on the game's client core. Details of implementation fully depend on
 * the developers' needs. However, it is recommended for a resource builder to
 * validate staticDataDirectory: check that required staticDataDirectory exist for each game ammunitionType,
 * or checks width/height of images. See
 * {@link SuseikaBrowserClientResourceBuilder} for example implementation.
 * </p>
 */
public abstract class ResourceBuilder {
	public abstract void build(String clientPath);
	public abstract String buildResourcesStaticData();
	protected abstract void cleanClientResourcesDirectory(String path);


}
