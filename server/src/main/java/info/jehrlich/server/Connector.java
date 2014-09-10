package info.jehrlich.server;

/**
 * Connector will be used by the Server to accept incoming connections.
 */
public interface Connector
{

	/**
	 * Start connector.
	 * Starts the Acceptor threads that will listen for incoming connections
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception;

	/**
	 * Stop connector.
	 * Will stop all listening acceptors
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception;
}
