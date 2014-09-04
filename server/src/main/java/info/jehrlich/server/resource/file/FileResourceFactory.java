package info.jehrlich.server.resource.file;

import info.jehrlich.server.resource.Resource;
import info.jehrlich.server.resource.ResourceAccessException;
import info.jehrlich.server.resource.ResourceException;
import info.jehrlich.server.resource.ResourceFactory;
import info.jehrlich.server.resource.ResourceNotFoundException;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * This factory creates an appropriate {@link Resource} representation for a given file path.
 * 
 */
public class FileResourceFactory implements ResourceFactory
{

	// ~ setup =============================================================================/

	private File rootDir = null;

	/**
	 * Constructor.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given directory does not exist or isn't readable
	 * 
	 * @param rootDir
	 */
	public FileResourceFactory() {}
	
	
	public void initialize(String root)
	{
		File rootDir = new File(root);
		
		if (!rootDir.isDirectory() || !rootDir.exists() || !rootDir.canRead())
		{
			throw new IllegalArgumentException(rootDir.getPath() + " does not exist or is not readable.");
		}

		this.rootDir = rootDir;
	}

	// ~ public methods ====================================================================/

	/*
	 * (non-Javadoc)
	 * 
	 * @see server.resource.ResourceFactorz#create(java.lang.String)
	 */
	public Resource create(String path) throws ResourceException
	{
		if( rootDir == null)
		{
			throw new ResourceException("Initialize first");
		}
		
		File file = new File(rootDir, path);

		if (!file.exists())
		{
			throw new ResourceNotFoundException();
		}
		if( !file.canRead() )
		{
			throw new ResourceAccessException(); 
		}
		
		return file.isDirectory() ? new DirectoryResource(rootDir, file) : new FileResource(file);
	}

}
