package info.jehrlich.server;

import java.io.IOException;

/**
 * General connection interface. Must be implemented by any Connection that shall be handled by the
 * server. As each Handler implementation is expecting a specific connection object, it should be
 * wrapped in an object that implements this interface. The wrapped object can then be retrieved by
 * the handler.
 * 
 * @author jehrlich
 * 
 */
public interface Connection extends Runnable
{
	/**
	 * Closes the connection
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;

	/**
	 * Retrieve the actual specific connection object.
	 * 
	 * @return The wrapped connection
	 */
	public <T> T getBaseConnection();
}
