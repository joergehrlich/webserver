package server;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = true)
public class Server 
{
	private final Logger LOG = LoggerFactory.getLogger(Server.class);
	
	@Property(value = "8080")
	static final String PORT = "port";
	
	@Property(value = "20")
	static final String THREADPOOL_SIZE = "threadpoolSize";
	
	private SocketConnector connector;
	private int port;
	private Handler handler;
	
	/**
	 * 
	 * @param config
	 */
	@Activate
	protected void activate(Map<String, String> config)
	{
		try 
		{
			String portConfig = config.get(PORT);
			int port = portConfig != null ? Integer.parseInt(portConfig) : 0;
			
			handler = new HTTPHandler();
			
			this.connector = new SocketConnector();
			connector.setPort(port);
			connector.setServer(this);
			connector.start();
		} 
		catch (Exception e) 
		{
			LOG.error("Cannot open Server on port " + port, e);
		}
	}

	/**
	 * 
	 */
	@Deactivate
	protected void deactivate()
	{
		try 
		{
			connector.stop();
        } 
		catch (Exception e) 
		{
			LOG.error("Error closing server", e);
        }
	}
	
	/**
	 * 
	 */
	@Modified
	private void modified(Map<String, String> config)
	{
		String portConfig = config.get(PORT);
		this.port = portConfig != null ? Integer.parseInt(portConfig) : 0;
		LOG.info("Updating server with new port" + port );
		
		deactivate();
		activate(config);
	}
	
	public void dispatch(Runnable job)
	{
		Thread t = new Thread(job);
        t.setDaemon(true);
        t.start();
	}
	
	public void handle(Connection conn)
	{
		handler.handle(conn);
	}
}
