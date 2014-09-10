package info.jehrlich.server.resource.file;

import info.jehrlich.server.resource.Resource;
import info.jehrlich.server.resource.ResourceAccessException;
import info.jehrlich.server.resource.ResourceNotFoundException;
import info.jehrlich.server.resource.file.internal.MimeType;

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
	private MimeType mimeService;
	
	public FileResource(File file, MimeType mimeService)
	{
		this.file = file;
		this.mimeService = mimeService;
	}

	public long getContentLength()
	{
		return file.length();
	}

	public String getContentType()
	{
		return mimeService.getMimeType(file.getName());
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

	public OutputStream getContentWriter() throws ResourceAccessException
	{
		try
		{
			return new FileOutputStream(file);
		}
		catch (FileNotFoundException e)
		{
			throw new ResourceAccessException();
		}
	}

	public Date getLastModified()
	{
		return new Date(file.lastModified());
	}

	public boolean exists()
	{
		return file.exists();
	}

	public boolean canRead()
	{
		return file.canRead();
	}
	
	public boolean canWrite()
	{
		return file.canWrite();
	}
}
