package info.jehrlich.server.impl;

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
	private int numAcceptors;
	private ServerSocket serverSocket;
	private HttpConnectionFactory<DefaultBHttpServerConnection> connFactory;

	/**
	 * Acceptors that are currently opened.
	 */
	private Set<Acceptor> acceptors;

	/**
	 * Will accept incoming connections which are dispatched to the {@link Server}.
	 * 
	 */
	private class Acceptor implements Runnable
	{
		private final int index;
		private volatile boolean running = true;

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

		public void close()
		{
			LOG.info(this + "Shutting down");
			running = false;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			try
			{
				while (running)
				{
					try
					{
						LOG.info(this + "Accepting connections");
						Socket socket = serverSocket.accept();
						// Configure socket properties.
						configure(socket);

						HttpServerConnection conn = connFactory.createConnection(socket);
						ConnectionWrapper connWrapper = new ConnectionWrapper(conn, server);

						LOG.info("Dispatching connection from: " + socket.getRemoteSocketAddress().toString());
						server.dispatchConnection(connWrapper);
					}
					catch (IOException e)
					{
						running = false;
					}
				}
			}
			catch (Exception e)
			{
				LOG.error("Error in " + this, e);
			}
		}
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
		for (int i = 0; i < numAcceptors; i++)
		{
			Acceptor acceptor = new Acceptor(i);
			acceptors.add(acceptor);
			server.dispatch(acceptor);
		}
	}

	/**
	 * Close all opened connections.
	 * 
	 * @see com.foo.tpws.Connector#stop()
	 */
	public void stop() throws Exception
	{
		// TODO close all acceptors
		LOG.info("Shutting down acceptors");
		for (Acceptor acceptor : acceptors)
		{
			acceptor.close();
			acceptors.remove(acceptor);
		}

		LOG.info("Shutting down serversocket");
		serverSocket.close();
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
			conn.numAcceptors = num;
			return this;
		}

		public SocketConnector configure() throws UnknownHostException, IOException
		{
			conn.acceptors = new HashSet<SocketConnector.Acceptor>();
			conn.connFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
			
			if( conn.host == null )
			{
				conn.serverSocket = new ServerSocket(conn.port, 0);
			}
			else
			{
				conn.serverSocket = new ServerSocket(conn.port, 0, InetAddress.getByName(conn.host));
			}
			
			return conn;
		}
	}

}
