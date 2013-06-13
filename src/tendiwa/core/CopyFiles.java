package tendiwa.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Copy files, using two techniques, FileChannels and streams. Using
 * FileChannels is usually faster than using streams.
 */
public final class CopyFiles {

	/** This may fail for VERY large files. 
	 * @throws IOException */
	public static void copyWithChannels(File aSourceFile, File aTargetFile) throws IOException {
		ensureTargetDirectoryExists(aTargetFile.getParentFile());
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			inStream = new FileInputStream(aSourceFile);
			inChannel = inStream.getChannel();
			outStream = new FileOutputStream(aTargetFile, false);
			outChannel = outStream.getChannel();
			long bytesTransferred = 0;
			// defensive loop - there's usually only a single iteration :
			while (bytesTransferred < inChannel.size()) {
				bytesTransferred += inChannel
					.transferTo(0, inChannel.size(), outChannel);
			}
		} finally {
			// being defensive about closing all channels and streams
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
			if (inStream != null)
				inStream.close();
			if (outStream != null)
				outStream.close();
		}
	}

	public static void copyWithStreams(File aSourceFile, File aTargetFile)
		throws IOException {
		ensureTargetDirectoryExists(aTargetFile.getParentFile());
		InputStream inStream = null;
		OutputStream outStream = null;
		try {
			byte[] bucket = new byte[32 * 1024];
			inStream = new BufferedInputStream(new FileInputStream(aSourceFile));
			outStream = new BufferedOutputStream(new FileOutputStream(aTargetFile, false));
			int bytesRead = 0;
			while (bytesRead != -1) {
				bytesRead = inStream.read(bucket); // -1, 0, or more
				if (bytesRead > 0) {
					outStream.write(bucket, 0, bytesRead);
				}
			}
		} finally {
			if (inStream != null)
				inStream.close();
			if (outStream != null)
				outStream.close();
		}

	}

	private static void ensureTargetDirectoryExists(File aTargetDir) {
		if (!aTargetDir.exists()) {
			aTargetDir.mkdirs();
		}
	}

}