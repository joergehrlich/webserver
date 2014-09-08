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
	 * @param conn
	 *            The Connection is a wrapper around the actual base connection which must be
	 *            retrieved by the handler
	 */
	public void handle(Connection conn);

}
