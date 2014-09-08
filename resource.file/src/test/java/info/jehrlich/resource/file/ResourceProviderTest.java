package info.jehrlich.resource.file;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import info.jehrlich.server.resource.Resource;
import info.jehrlich.server.resource.ResourceProvider;
import info.jehrlich.server.resource.file.DirectoryResource;
import info.jehrlich.server.resource.file.FileResource;
import info.jehrlich.server.resource.file.FileResourceProvider;

import org.junit.Test;

public class ResourceProviderTest
{
	ResourceProvider provider = new FileResourceProvider(SampleFiles.DIR);
	
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

	private Resource createFrom(String path)
	{
		return provider.create(path);
	}

}
