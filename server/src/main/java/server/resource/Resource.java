package server.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

/**
 * A {@literal Resource} exposes its data through this interface in a format
 * suitable for the active protocol {@link Handler}.
 * 
 */
public interface Resource {

	/**
	 * The length of the content's byte stream.
	 * @return
	 */
	long getContentLength();

	/**
	 * The content's mime type.
	 * 
	 * @see MimeType#of(java.io.File)
	 * @return
	 */
	String getContentType();

	/**
	 * The last modification time.
	 * May be used for caching purposes.
	 * @return
	 */
	Date getLastModified();

	/**
	 * Opens a data stream to the resource's content.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	InputStream getContent() throws FileNotFoundException;

	/**
	 * No-Op implementation for unit tests.
	 */
	static class NullResource implements Resource {

		public long getContentLength() {
			return 0;
		}

		public String getContentType() {
			return "";
		}

		public InputStream getContent() throws FileNotFoundException {
			return null;
		}

		public Date getLastModified() {
			return null;
		}

	}

}
