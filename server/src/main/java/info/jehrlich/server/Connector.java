package info.jehrlich.server;

/**
 * Connector will be used by the Server to accept incoming connections.
 */
public interface Connector
{

	/**
	 * Start connector.
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception;

	/**
	 * Stop connector.
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception;
}
