package info.jehrlich.server.resource;

/**
 * Interface to obtain access to a {@link Resource}
 * 
 * @author jehrlich
 * 
 */
public interface ResourceProvider
{
	/**
	 * Creates a {@link Resource} according to the given path. The {@link Resource} is always
	 * created. Operations on the {@link Resource} will throw in case of errors
	 * 
	 * @param path
	 *            A URI path
	 * @return the Resource handle
	 */
	Resource create(String path);
}
