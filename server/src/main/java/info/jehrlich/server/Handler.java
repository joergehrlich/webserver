package info.jehrlich.server;

import info.jehrlich.server.resource.Resource;
import info.jehrlich.server.resource.ResourceProvider;

/**
 * Interface that must be implemented by handlers to handle incoming connections.
 * The handler must use a {@link ResourceProvider} to retrieve requested resources.
 * As ResourceProvider are dynamic in the Server, the handler must work with and without a provider.
 */
public interface Handler
{
	/**
	 * Handles the given {@link Connection}.
	 * @param conn
	 *            The concrete Connection should be a wrapper around the actual base connection
	 *            which would be handler specific
	 */
	public void handle(Connection conn);

	/**
	 * Sets the {@link ResourceProvider} the handler must use to retrieve a {@link Resource} from.
	 * @param provider
	 */
	public void setResourceProvider(ResourceProvider provider);
	
	/**
	 * Unsets the {@link ResourceProvider} for the Handler.
	 */
	public void unsetResourceProvider();
}
