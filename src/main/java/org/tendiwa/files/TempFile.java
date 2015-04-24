package org.tendiwa.files;

import java.io.File;
import java.io.IOException;

public final class TempFile extends File_Wr {
	public TempFile(String prefix, String suffix) {
		super(createTempFileWithRuntimeException(prefix, suffix));
	}

	private static File createTempFileWithRuntimeException(String prefix, String suffix) {
		try {
			return File.createTempFile(prefix, suffix);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
