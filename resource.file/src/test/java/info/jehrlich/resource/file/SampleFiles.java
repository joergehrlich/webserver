package info.jehrlich.resource.file;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class SampleFiles
{
	public static File DIR;

	static
	{
		try
		{
			URL resource = SampleFiles.class.getClassLoader().getResource("sample");
			String filePath = URLDecoder.decode(resource.getFile(), "UTF-8");
			DIR = new File(filePath);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException("Unable to access sample directory.", e);
		}

	}

}
