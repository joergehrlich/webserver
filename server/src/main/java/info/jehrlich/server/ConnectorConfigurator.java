package info.jehrlich.server;

/**
 * Should be implemented by a concrete {@link Connector} to create instances of the Connector.
 * @author jehrlich
 *
 */
public interface ConnectorConfigurator
{
	/**
	 * Sets the port to use in the Connector.
	 * @param port
	 * @return
	 */
	public ConnectorConfigurator withPort(int port);

	/**
	 * Sets the host to use in the Connector
	 * @param host
	 * @return
	 */
	public ConnectorConfigurator withHost(String host);

	/**
	 * Sets a reference to the calling {@link Server}
	 * @param server
	 * @return
	 */
	public ConnectorConfigurator withServer(Server server);

	/**
	 * Sets the number of acceptor threads that shall be opened to receive connections
	 * @param num
	 * @return
	 */
	public ConnectorConfigurator withAcceptors(int num);

	/**
	 * Creates the actual Connector instance
	 * @return the Connector instance
	 * @throws Exception in case of an error
	 */
	public Connector configure() throws Exception;

}
