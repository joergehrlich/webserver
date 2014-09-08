package info.jehrlich.server.resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * A {@literal Resource} exposes data through this interface to be consumed by a {@link Handler}.
 * 
 */
public interface Resource
{
	/**
	 * Indicates if the resource already exists
	 * @return
	 */
	boolean exists();

	/**
	 * Indicates if the resource can be read
	 * @return
	 */
	boolean canRead();

	/**
	 * Indicates if the resource can be written
	 * @return
	 */
	boolean canWrite();
	
	/**
	 * The length of the content's byte stream.
	 * 
	 * @return
	 */
	long getContentLength();

	/**
	 * The content's mime type.
	 * 
	 * @return
	 */
	String getContentType();

	/**
	 * The last modification time. May be used for caching purposes.
	 * 
	 * @return
	 */
	Date getLastModified();

	/**
	 * Opens a data stream to read a resource's content.
	 * 
	 * @return
	 * @throws ResourceNotFoundException
	 */
	InputStream getContent() throws ResourceNotFoundException;

	/**
	 * opens a data stream to write to a resource.
	 * 
	 * @return
	 * @throws ResourceAccessException
	 */
	OutputStream getContentWriter() throws ResourceAccessException;

}
