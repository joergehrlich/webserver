package info.jehrlich.resource.file;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import info.jehrlich.server.resource.Resource;
import info.jehrlich.server.resource.file.DirectoryResource;
import info.jehrlich.server.resource.file.FileResource;
import info.jehrlich.server.resource.file.FileResourceProvider;

import org.junit.BeforeClass;
import org.junit.Test;

public class ResourceProviderTest
{
	private static FileResourceProvider provider;
	
	@BeforeClass
	public static void testSetup()
	{
		Map<String, String> config = new HashMap<String, String>();
		config.put("rootPath", SampleFiles.DIR.getAbsolutePath());
		
		provider = new FileResourceProvider();
		provider.activate(config);
	}
	
	private Resource createFrom(String path)
	{
		return provider.create(path);
	}

	// --- Tests ---
	
	@Test
	public void create_file_FileResource()
	{
		Resource resource = createFrom("/index.html");
		assertThat(resource, instanceOf(FileResource.class));
	}

	@Test
	public void create_directory_DirectoryResource()
	{
		Resource resource = createFrom("/");
		assertThat(resource, instanceOf(DirectoryResource.class));
	}

	@Test
	public void create_notExisting_FileResource()
	{
		Resource resource = createFrom("/notExisting.html");
		assertThat(resource, instanceOf(FileResource.class));
	}

}
