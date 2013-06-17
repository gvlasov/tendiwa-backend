package tendiwa.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class ModuleBuilder {
public static File buildModuleJar(File moduleDir, File outputDir) {
	File tendiwaModulesPackage = new File(moduleDir.getAbsolutePath()+"/src/tendiwa/modules/");

	File[] files = tendiwaModulesPackage.listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File file, String name) {
			// TODO Auto-generated method stub
			if (name.endsWith("Module.java")) {
				return true;
			}
			return false;
		}
	});
	if (files.length > 1) {
		throw new Error("There are more than 1 file in "+moduleDir+" that ends with *Module.jar");
	}
	if (files.length == 0) {
		throw new Error("No file *Module.java found in "+moduleDir);
	}
	File moduleClassFile = files[0];

	String moduleName = moduleClassFile.getName().replace(".java", "");
	Manifest manifest = new Manifest();
	manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
	JarOutputStream target = null;
	String outputFileName = moduleName+".jar";
	String outputFilePath = outputDir.getAbsolutePath()+"/"+outputFileName;
	try {
		target = new JarOutputStream(new FileOutputStream(outputFilePath), manifest);
		add(moduleDir, target);
		target.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return new File(outputFilePath);
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
				System.out.println(nestedFile);
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
