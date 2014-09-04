package info.jehrlich.server.resource.file;

import info.jehrlich.server.resource.ResourceFactory;
import info.jehrlich.server.resource.ResourceProvider;

// will be a service and component
public class FileResourceProvider implements ResourceProvider
{
	private ResourceFactory rsFactory;
	
	public FileResourceProvider(  )
	{
	}
	
	/* (non-Javadoc)
	 * @see server.resource.ResourceProvider#getResourceFactory()
	 */
	public ResourceFactory getResourceFactory()
	{
		if( rsFactory == null )
			rsFactory = new FileResourceFactory();
		
		return rsFactory;
	}
}
