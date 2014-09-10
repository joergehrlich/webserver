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
import org.apache.felix.scr.annotations.ReferenceCardinality;
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

	// --- Config properties ---
	@Property(value = "8080")
	private static final String PORT = "port";

	@Property(value = "")
	private static final String HOST = "host";
	
	@Property(value = "3")
	private static final String NUM_ACCEPTORS = "numAcceptors"; 

	// --- Service references ---
	// The server cannot run without ThreadPool manager
	@Reference(policy = ReferencePolicy.STATIC)
	private ThreadPoolManager tpManager;

	// But it could still handle requests, even if no resources can be served
	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_UNARY)
	private volatile ResourceProvider resourceProvider;

	// --- server variables ---
	// Will listen for incoming connections
	private Connector connector;
	// Will handle connections
	private Handler handler;
	// Manages server threads
	private ThreadPool threadpool;
	// List of open connections
	private Set<Connection> connections;

	// --- Lifecycle methods ---

	@Activate
	private void activate(Map<String, String> config)
	{
		try
		{
			// Get configuration values
			String portConfig = config.get(PORT);
			int port = portConfig != null ? Integer.parseInt(portConfig) : 0;
			
			String host = config.get(HOST); // can be null
			
			String acceptorConfig = config.get(NUM_ACCEPTORS);
			int numAcceptors = acceptorConfig != null ? Integer.parseInt(acceptorConfig) : 3;
			
			// set up the server
			
			connections = new HashSet<Connection>();

			// resourceProvider could be null if no provider has been activated in the system
			handler = new HTTPHandler(resourceProvider);

			threadpool = tpManager.get(Server.class.getCanonicalName());

			connector = SocketConnector.configurator
										.withPort(port)
										.withHost(host)
										.withServer(this)
										.withAcceptors(numAcceptors)
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
	private void deactivate()
	{
		LOG.info("Shutting down server");

		try
		{
			for (Connection conn : connections)
			{
				conn.close(); // Don't wait for connections to finish
			}
			connections.clear();

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
	 * Executes the given {@link Connection} in the ThreadPool.
	 * 
	 * @param job
	 */
	public void dispatchConnection(Connection conn)
	{
		addConnection(conn);
		threadpool.execute(conn);
	}

	/**
	 * Starts handling of incoming {@link Connection}.
	 * 
	 * @param conn
	 *            The connection to handle
	 */
	public void handle(Connection conn)
	{
		handler.handle(conn);
	}

	/**
	 * Add {@link Connection} to the opened connection set.
	 * 
	 * @param conn
	 */
	private synchronized void addConnection(Connection conn)
	{
		connections.add(conn);
	}

	/**
	 * Remove {@link Connection} from the opened connection set.
	 * 
	 * @param conn
	 */
	public synchronized void removeConnection(Connection conn)
	{
		connections.remove(conn);
	}
}
