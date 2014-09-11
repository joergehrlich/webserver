package info.jehrlich.server;

/**
 * Interface that must be implemented by handlers to handle incoming connections.
 * 
 * @author jehrlich
 * 
 */
public interface Handler
{
	/**
	 * Handles the given {@link Connection}.
	 * @param conn
	 *            The concrete Connection should be a wrapper around the actual base connection
	 *            which would be handler specific
	 */
	public void handle(Connection conn);

}
