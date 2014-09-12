package info.jehrlich.server.resource.file;

import info.jehrlich.server.resource.Resource;
import info.jehrlich.server.resource.ResourceProvider;
import info.jehrlich.server.resource.file.internal.DirectoryResource;
import info.jehrlich.server.resource.file.internal.FileResource;
import info.jehrlich.server.resource.file.internal.MimeType;

import java.io.File;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.mime.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a {@link ResourceProvider} that provides access to resources on file system.
 * Needs to be configured with the root path of the resources.
 * If available it will use the Sling {@link MimeTypeService} to determine Mimetypes.
 * 
 */
@Service
@Component(metatype = true)
public class FileResourceProvider implements ResourceProvider
{
	// Per default it is looking at a "www" directory in the execution working directory
	@Property(value = "www")
	private static final String ROOTPATH = "rootPath";

	// Service references
	@Reference(policy = ReferencePolicy.STATIC, cardinality = ReferenceCardinality.MANDATORY_UNARY)
	private MimeTypeService mimeTypeService;
	
	// This wraps the Sling MimeTypeService, has a fallback for testing purposes
	private MimeType mimeService;
	
	private File rootDir;
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	public FileResourceProvider()
	{
	}
	
	// --- Lifecycle methods ---
	@Activate
	public void activate(Map<String, String> config)
	{
		String rootPath = config.get(ROOTPATH);

		mimeService = new MimeType(mimeTypeService);
		
		rootDir = new File(rootPath);
		LOG.info("Init with path: " + rootDir.getAbsolutePath());
		
		if (!rootDir.isDirectory() || !rootDir.exists() || !rootDir.canRead())
		{
			LOG.error(rootDir.getPath() + " does not exist or is not readable.");
		}
	}

	@Deactivate
	protected void deactivate()
	{
		// Nothing to do
	}

	@Modified
	private void modified(Map<String, String> config)
	{
		LOG.info("Updating with new config");

		deactivate();
		activate(config);
	}

	/**
	 * @see ResourceProvider#create(String)
	 */
	public Resource create(String path)
	{
		File file = new File(rootDir, path);

		return file.isDirectory() ? new DirectoryResource(rootDir, file, mimeService) : new FileResource(file, mimeService);
	}
}
