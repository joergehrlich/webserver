package info.jehrlich.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Connection implements Runnable
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private final Server server;

	public Connection(Server server)
	{
		this.server = server;
	}

	/**
	 * Close this connection. 
	 * Should block until connection is actually closed
	 */
	public abstract void close() throws Exception;
	
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
				close();
				server.removeConnection(this);
			}
			catch (Exception e)
			{
				LOG.error("Error in closing connection", e);
			}
		}
	}

}
