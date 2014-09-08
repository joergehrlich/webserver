package info.jehrlich.server.impl;

import info.jehrlich.server.Connection;
import info.jehrlich.server.Connector;
import info.jehrlich.server.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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
	private int acceptors;
	private ServerSocket serverSocket;
	private HttpConnectionFactory<DefaultBHttpServerConnection> connFactory;

	/**
	 * Connections that are currently opened.
	 */
	private Set<Connection> connections;

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

					LOG.info("Dispatching connection from: " + socket.getRemoteSocketAddress().toString());

					HttpServerConnection conn = connFactory.createConnection(socket);
					ConnectionWrapper connWrapper = new ConnectionWrapper(conn, server);
					addConnection(connWrapper);
					server.dispatch(connWrapper);
				}
			}
			catch (Exception e)
			{
				LOG.error("Error in Acceptor" + this, e);
			}
		}
	}

	/**
	 * Remove connection from the opened connection set.
	 * 
	 * @param conn
	 */
	private synchronized void addConnection(Connection conn)
	{
		connections.add(conn);
	}
	
	
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
	}

	protected void configure(Socket socket) throws IOException
	{

	}

	/**
	 * @see com.foo.tpws.Connector#start()
	 */
	public void start() throws Exception
	{
		LOG.info("Starting Server on port " + port);

		for (int i = 0; i < acceptors; i++)
		{
			server.dispatch(new Acceptor(i));
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

	public static Configurator configurator = new Configurator();

	public static class Configurator
	{
		private SocketConnector conn;

		public Configurator()
		{
			conn = new SocketConnector();
		}

		public Configurator withPort(int port)
		{
			conn.port = port;
			return this;
		}

		public Configurator withHost(String host)
		{
			conn.host = host;
			return this;
		}

		public Configurator withServer(Server server)
		{
			conn.server = server;
			return this;
		}

		public Configurator withAcceptors(int num)
		{
			conn.acceptors = num;
			return this;
		}

		public SocketConnector configure() throws UnknownHostException, IOException
		{
			conn.connections = new HashSet<Connection>();
			conn.connFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
			conn.serverSocket = conn.host == null ? new ServerSocket(conn.port, 0) : new ServerSocket(conn.port, 0,
					InetAddress.getByName(conn.host));

			return conn;
		}
	}

}
