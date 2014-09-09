package info.jehrlich.server;

import info.jehrlich.server.impl.HTTPHandler;
import info.jehrlich.server.impl.SocketConnector;
import info.jehrlich.server.resource.ResourceProvider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.sling.commons.threads.ThreadPool;
import org.apache.sling.commons.threads.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main server class that handles connections and dispatches request handling. The server needs a
 * Threadpool to execute connectors and dispatch requests. It can be configured with Configuration
 * Manager.
 * 
 * @author jehrlich
 * 
 */
@Component(metatype = true)
public class Server
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Property(value = "8080")
	static final String PORT = "port";

	@Property(value = "")
	static final String HOST = "host";

	// The server cannot run without ThreadPool manager
	@Reference(policy = ReferencePolicy.STATIC)
	private ThreadPoolManager tpManager;

	// But it could still handle requests, even if no resources can be served
	@Reference(policy = ReferencePolicy.DYNAMIC)
	private volatile ResourceProvider resourceProvider;

	private Connector connector;
	private Handler handler;
	private ThreadPool threadpool;
	private Set<Connection> connections;

	// --- Lifecycle methods ---

	@Activate
	protected void activate(Map<String, String> config)
	{
		try
		{
			String portConfig = config.get(PORT);
			int port = portConfig != null ? Integer.parseInt(portConfig) : 0;
			String host = config.get(HOST);

			connections = new HashSet<Connection>();

			// resourceProvider could be null if no provider has been activated in the system
			handler = new HTTPHandler(resourceProvider);

			threadpool = tpManager.get(Server.class.getCanonicalName());

			connector = SocketConnector.configurator.withPort(port).withHost(host).withServer(this).withAcceptors(1)
					.configure();

			LOG.info("Starting Server on port " + port);
			connector.start();
		}
		catch (Exception e)
		{
			LOG.error("Cannot open Server", e);
			if (threadpool != null)
			{
				tpManager.release(threadpool);
			}
		}
	}

	@Deactivate
	protected void deactivate()
	{
		LOG.info("Shutting down server");

		try
		{
			LOG.info("Shutting down connections");
			for (Connection conn : connections)
			{
				conn.close();
				removeConnection(conn);
			}

			if (connector != null)
			{
				connector.stop();
			}
			if (threadpool != null)
			{
				tpManager.release(threadpool);
			}
		}
		catch (Exception e)
		{
			LOG.error("Error shutting down server", e);
		}
	}

	@Modified
	private void modified(Map<String, String> config)
	{
		LOG.info("Updating server with new config");

		deactivate();
		activate(config);
	}

	// --- Connection methods ---
	/**
	 * Executes the given job in the ThreadPool.
	 * 
	 * @param job
	 */
	public void dispatch(Runnable job)
	{
		threadpool.execute(job);
	}

	/**
	 * Executes the given Connection in the ThreadPool.
	 * 
	 * @param job
	 */
	public void dispatchConnection(Connection conn)
	{
		addConnection(conn);
		threadpool.execute(conn);
	}

	/**
	 * Starts handling of incoming connection.
	 * 
	 * @param conn
	 *            The connection to handle
	 */
	public void handle(Connection conn)
	{
		handler.handle(conn);
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
	public synchronized void removeConnection(Connection conn)
	{
		connections.remove(conn);
	}
}
