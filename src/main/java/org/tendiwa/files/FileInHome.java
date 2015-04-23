package org.tendiwa.files;

import java.io.File;

public final class FileInHome extends File {
	public FileInHome(String fileName) {
		super(System.getProperty("user.home") + "/" + fileName);
	}
}
