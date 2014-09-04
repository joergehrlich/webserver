package info.jehrlich.server.resource.file;

import info.jehrlich.server.resource.MimeType;
import info.jehrlich.server.resource.Resource;
import info.jehrlich.server.resource.ResourceNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Simple {@link Resource} representing a file under the current document root.
 * 
 */
public class FileResource implements Resource
{

	private File file;

	public FileResource(File file)
	{
		this.file = file;
	}

	public long getContentLength()
	{
		return file.length();
	}

	public String getContentType()
	{
		return MimeType.of(file);
	}

	public InputStream getContent() throws ResourceNotFoundException
	{
		try
		{
			return new FileInputStream(file);
		} 
		catch (FileNotFoundException e)
		{
			throw new ResourceNotFoundException();
		}
	}

	public OutputStream getContentWriter() throws ResourceNotFoundException
	{
		try
		{
			return new FileOutputStream(file);
		} 
		catch (FileNotFoundException e)
		{
			throw new ResourceNotFoundException();
		}
	}

	public Date getLastModified()
	{
		return new Date(file.lastModified());
	}
}
