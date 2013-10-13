package tendiwa.core;

import com.sun.codemodel.JCodeModel;

import java.io.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class ModuleBuilder {
/**
 * Defines the name of a directory with static data inside a module.
 */
static final String staticDataDirectory = "src";

static void generateResourcesCode(String moduleDir) {
	File moduleDirFile = new File(moduleDir);
	File[] xmls = new File(moduleDir + File.separator + "data").listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith("xml");
		}
	});
	for (File xml : xmls) {
		LoadStaticDataFromXML.loadGameDataFromXml(xml.getAbsolutePath());
	}
	// Create source files
	File destDir = new File(moduleDir + File.separator + staticDataDirectory);
	destDir.mkdirs();
	for (JCodeModel model : LoadStaticDataFromXML.codeModels) {
		try {
			model.build(destDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//	createJar(moduleDirFile);
}

private static void createJar(File moduleDirFile) {
	Manifest manifest = new Manifest();
	manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
	JarOutputStream target;
	String outputFileName = new File(System.getProperty("user.dir")).getName() + "Resources.jar";
	String outputFilePath = moduleDirFile.getAbsolutePath() + File.separator + outputFileName;
	try {
		target = new JarOutputStream(new FileOutputStream(outputFilePath), manifest);
		add(new File(moduleDirFile.getPath() + File.separator + staticDataDirectory), target);
		target.close();
		System.out.println("Created " + outputFileName);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

private static void add(File source, JarOutputStream target) throws IOException {
	BufferedInputStream in = null;
	try {
		if (source.isDirectory()) {
			String name = source.getPath().replace("\\", "/");
			if (!name.isEmpty()) {
				if (!name.endsWith("/"))
					name += "/";
				JarEntry entry = new JarEntry(name);
				entry.setTime(source.lastModified());
				target.putNextEntry(entry);
				target.closeEntry();
			}
			for (File nestedFile : source.listFiles()) {
				add(nestedFile, target);
			}
			return;
		}

		JarEntry entry = new JarEntry(source.getPath().replace("\\", "/"));
		entry.setTime(source.lastModified());
		target.putNextEntry(entry);
		in = new BufferedInputStream(new FileInputStream(source));

		byte[] buffer = new byte[1024];
		while (true) {
			int count = in.read(buffer);
			if (count == -1)
				break;
			target.write(buffer, 0, count);
		}
		target.closeEntry();
	} finally {
		if (in != null)
			in.close();
	}
}
}