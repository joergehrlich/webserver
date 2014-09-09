package info.jehrlich.server.impl;

import info.jehrlich.server.Connection;
import info.jehrlich.server.Server;

import java.io.IOException;

import org.apache.http.HttpServerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionWrapper implements Connection
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	private final HttpServerConnection conn;
	private final Server server;

	public ConnectionWrapper(HttpServerConnection conn, Server server)
	{
		this.conn = conn;
		this.server = server;
	}

	/**
	 * @see server.Connection#close()
	 */
	public void close() throws IOException
	{
		LOG.info("Shutting down connection");
		conn.shutdown();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		try
		{
			LOG.info("Start handling connection.");

			server.handle(this);

			LOG.info("Finished handling connection.");
		}
		catch (Exception e)
		{
			LOG.error("Error in handling connection", e);
		}
		finally
		{
			try
			{
				// Close connection
				close();
			}
			catch (IOException e)
			{
				LOG.error("Error in closing connection", e);
			}
		}
	}

	/**
	 * @see server.Connection#getBaseConnection()
	 */
	public <T> T getBaseConnection()
	{
		try
		{
			T baseConn = (T) conn;
			return baseConn;
		}
		catch (ClassCastException e)
		{
			return null;
		}
	}

}
