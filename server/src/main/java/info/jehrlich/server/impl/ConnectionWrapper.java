package info.jehrlich.server.impl;

import info.jehrlich.server.Connection;
import info.jehrlich.server.Server;

import org.apache.http.HttpServerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of a Connection that wraps the HttpCore Connection. 
 *
 */
public class ConnectionWrapper extends Connection
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	private final HttpServerConnection conn;

	public ConnectionWrapper(HttpServerConnection conn, Server server)
	{
		super(server);
		this.conn = conn;
	}

	/**
	 * @see Connection#close()
	 */
	@Override
	public void close() throws Exception
	{
		LOG.info("Shutting down connection");
		conn.shutdown(); //immediately shut down the connection
	}

	/**
	 * Return the actual HttpCore Connection
	 * @return
	 */
	public HttpServerConnection getHttpConnection()
	{
		return conn;
	}

}
