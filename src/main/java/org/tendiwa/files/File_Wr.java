package org.tendiwa.files;

import java.io.File;

public abstract class File_Wr extends File {
	public File_Wr(File file) {
		super(file.getPath());
	}
}
