package info.jehrlich.server.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
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
	InputStream getContent() throws ResourceNotFoundException;

	OutputStream getContentWriter() throws ResourceNotFoundException;

}
