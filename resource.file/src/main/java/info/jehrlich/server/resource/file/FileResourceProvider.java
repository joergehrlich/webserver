package info.jehrlich.server.resource.file;

import info.jehrlich.server.resource.Resource;
import info.jehrlich.server.resource.ResourceProvider;

import java.io.File;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a {@link ResourceProvider} that provides access to resources on file system.
 * Needs to be configured with the root path of the resources.
 * 
 * @author jehrlich
 * 
 */
@Service
@Component(metatype = true)
public class FileResourceProvider implements ResourceProvider
{
	@Property(value = "C:\\Users\\jehrlich\\Desktop\\server")
	private static final String ROOTPATH = "rootPath";

	private File rootDir;

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	public FileResourceProvider()
	{
	}
	
	@Activate
	public void activate(Map<String, String> config)
	{
		// TODO what if null?
		String rootPath = config.get(ROOTPATH);

		rootDir = new File(rootPath);

		if (!rootDir.isDirectory() || !rootDir.exists() || !rootDir.canRead())
		{
			LOG.error(rootDir.getPath() + " does not exist or is not readable.");
			// TODO what exception to throw?
			throw new IllegalArgumentException(rootDir.getPath() + " does not exist or is not readable.");
		}
	}

	/**
	 * 
	 */
	@Deactivate
	protected void deactivate()
	{
		// Nothing to do
	}

	/**
	 * 
	 */
	@Modified
	private void modified(Map<String, String> config)
	{
		LOG.info("Updating ResourceProvider with new config");

		deactivate();
		activate(config);
	}

	/**
	 * @see ResourceProvider#create(String)
	 */
	public Resource create(String path)
	{
		File file = new File(rootDir, path);

		return file.isDirectory() ? new DirectoryResource(rootDir, file) : new FileResource(file);
	}
}
