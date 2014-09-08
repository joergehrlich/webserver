package info.jehrlich.server;

import info.jehrlich.server.impl.HTTPHandler;
import info.jehrlich.server.impl.SocketConnector;
import info.jehrlich.server.resource.ResourceProvider;

import java.util.Map;

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
 * Main server class that handles connections and dispatches request handling.
 * The server needs a Threadpool to execute connectors and dispatch requests.
 * It can be configured with Configuration Manager.
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
	
	@Property(value = "20")
	static final String THREADPOOL_SIZE = "threadpoolSize";

	// The server cannot run without ThreadPool manager
	@Reference(policy = ReferencePolicy.STATIC)
	private ThreadPoolManager tpManager;

	//TODO dynamic reference must be declared volatile for unary references
	@Reference(policy = ReferencePolicy.STATIC)
	private ResourceProvider resourceProvider;

	private Connector connector;
	private Handler handler;
	private ThreadPool threadpool;

	// --- Lifecycle methods ---
	
	@Activate
	protected void activate(Map<String, String> config)
	{
		try
		{
			String portConfig = config.get(PORT);
			int port = portConfig != null ? Integer.parseInt(portConfig) : 0;
			String host = config.get(HOST);
			
			// TODO get from Service
//			ResourceProvider provider = new FileResourceProvider("C:\\Users\\jehrlich\\Desktop\\server");
			
			// resourceProvider could be null if no provider has been activated in the system
			handler = new HTTPHandler(resourceProvider);

			threadpool = tpManager.get(Server.class.getCanonicalName());

			connector = SocketConnector.configurator
										.withPort(port)
										.withHost(host)
										.withServer(this)
										.withAcceptors(1)
										.configure();
			
			connector.start();
		}
		catch (Exception e)
		{
			LOG.error("Cannot open Server", e);
			if(threadpool != null)
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
			tpManager.release(threadpool);
			connector.stop();
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
	 * @param job
	 */
	public void dispatch(Runnable job)
	{
		threadpool.execute(job);
	}

	/**
	 * Starts handling of incoming connection.
	 * @param conn The connection to handle
	 */
	public void handle(Connection conn)
	{
		handler.handle(conn);
	}
}
