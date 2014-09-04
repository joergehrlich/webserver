package info.jehrlich.server.impl;

import info.jehrlich.server.Connection;
import info.jehrlich.server.Connector;
import info.jehrlich.server.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Socket Connector implementation.
 */
public class SocketConnector implements Connector
{
	private final Logger LOG = LoggerFactory.getLogger(SocketConnector.class);

	private int port;
	private String host;
	private Server server;
	protected ServerSocket serverSocket;
	protected final HttpConnectionFactory<DefaultBHttpServerConnection> connFactory;
	
	private final int acceptors;

	/**
   *
   */
	private class Acceptor implements Runnable
	{
		private final int index;

		/**
		 * Constructor.
		 * 
		 * @param index
		 *            An index attached to the acceptor, for identification.
		 */
		public Acceptor(int index)
		{
			this.index = index;
		}

		@Override
		public String toString()
		{
			return "[Acceptor " + index + "] ";
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			try
			{
				while (true)
				{
					LOG.info(this + "Accepting connections");
					Socket socket = serverSocket.accept();
					// Configure socket properties.
					configure(socket);

					LOG.info(this + "Begin dispacthing connection from: " + socket.getRemoteSocketAddress().toString());

					HttpServerConnection conn = connFactory.createConnection(socket);
					ConnectionWrapper connWrapper = new ConnectionWrapper(conn, server);
					connections.add(connWrapper);
					server.dispatch(connWrapper);

					LOG.info(this + "Finised dispacthing connection from: "
							+ socket.getRemoteSocketAddress().toString());
				}
			} catch (Exception e)
			{
				LOG.error("Error in Acceptor" + this, e);
			}
		}
	}

	/**
	 * Connections that are currently opened.
	 */
	private Set<ConnectionWrapper> connections;

	/**
	 * Remove connection from the opened connection set.
	 * 
	 * @param conn
	 */
	private synchronized void removeConnection(Connection conn)
	{
		connections.remove(conn);
	}

	/**
	 * Default constructor. Connector with a single acceptor thread.
	 */
	public SocketConnector()
	{
		this(1);
	}

	/**
	 * Constructor.
	 * 
	 * @param acceptors
	 *            Number of acceptor threads.
	 */
	public SocketConnector(int acceptors)
	{
		this.acceptors = acceptors;
		connections = new HashSet<ConnectionWrapper>();
		this.connFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
	}

	protected void configure(Socket socket) throws IOException
	{

	}

	/**
	 * @see com.foo.tpws.Connector#setPort(int)
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * @see com.foo.tpws.Connector#getPort()
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * @see com.foo.tpws.Connector#setHost(java.lang.String)
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	/**
	 * @see com.foo.tpws.Connector#getHost()
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 * @see com.foo.tpws.Connector#setServer(com.foo.tpws.Server)
	 */
	public void setServer(Server server)
	{
		this.server = server;
	}

	/**
	 * @see com.foo.tpws.Connector#getServer()
	 */
	public Server getServer()
	{
		return server;
	}

	/**
	 * @see com.foo.tpws.Connector#start()
	 */
	public void start() throws Exception
	{
		LOG.info("Starting Server on port " + port);
		serverSocket = host == null ? new ServerSocket(port, 0)
				: new ServerSocket(port, 0, InetAddress.getByName(host));
		for (int i = 0; i < acceptors; i++)
		{
			this.getServer().dispatch(new Acceptor(i));
		}
	}

	/**
	 * Close all opened connections.
	 * 
	 * @see com.foo.tpws.Connector#stop()
	 */
	public void stop() throws Exception
	{
		for (Connection conn : connections)
		{
			conn.close();
			removeConnection(conn);
		}
	}
}
