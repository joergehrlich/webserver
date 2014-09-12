package info.jehrlich.resource.file;

import info.jehrlich.server.resource.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import shaded.org.apache.commons.io.IOUtils;

public abstract class AbstractResourceTest<T extends Resource>
{

	protected T open(String path)
	{
		return instantiateResource(new File(SampleFiles.DIR, path));
	}

	protected abstract T instantiateResource(File file);

	protected String readAsString(InputStream content)
	{
		try
		{
			return IOUtils.toString(content);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

}
