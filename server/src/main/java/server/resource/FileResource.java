package server.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

/**
 * Simple {@link Resource} representing a file under the current document root.
 * 
 */
public class FileResource implements Resource {

	private File file;

	public FileResource(File file) {
		this.file = file;
	}

	public long getContentLength() {
		return file.length();
	}

	public String getContentType() {
		return MimeType.of(file);
	}

	public InputStream getContent() throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public Date getLastModified() {
		return new Date(file.lastModified());
	}
}
