package info.jehrlich.server.resource.file;

import static org.rendersnake.HtmlAttributesFactory.href;
import info.jehrlich.server.resource.Resource;
import info.jehrlich.server.resource.ResourceAccessException;
import info.jehrlich.server.resource.file.internal.MimeType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.regex.Pattern;

import org.rendersnake.HtmlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Resource} implementation representing a directory under the current document root.
 * 
 * The resource renders as an HTML document providing a listing of all files and sub-directories
 * within the given folder.
 * 
 */
public class DirectoryResource implements Resource
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private File documentRoot;
	private File folder;
	private HtmlCanvas listing;
	
	/**
	 * Constructor.
	 * 
	 * @param documentRoot
	 *            the root folder the server is serving files form.
	 * @param folder
	 *            the requested folder represented by this resource.
	 */
	public DirectoryResource(File documentRoot, File folder)
	{
		this.documentRoot = documentRoot;
		this.folder = folder;
	}

	// ~ public methods ===================================================================/

	public long getContentLength()
	{
		return getListing().toString().length();
	}

	public String getContentType()
	{
		return MimeType.of(".html");
	}

	public InputStream getContent()
	{
		return new ByteArrayInputStream(getListing().toHtml().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Returns the latest modification date of any file or folder under the active directory.
	 */
	public Date getLastModified()
	{
		File[] files = folder.listFiles();
		if (files != null)
		{

			long lastModified = folder.lastModified();
			for (File file : files)
			{
				lastModified = Math.max(lastModified, file.lastModified());
			}
			return new Date(lastModified);

		}
		return null;
	}


	public OutputStream getContentWriter() throws ResourceAccessException
	{
		// Cannot write a directory
		throw new ResourceAccessException();
	}

	public boolean exists()
	{
		return folder.exists();
	}

	public boolean canRead()
	{
		return folder.canRead();
	}

	public boolean canWrite()
	{
		return folder.canWrite();
	}
	
	// ~ internal helpers ==================================================================/
	private String getDirectoryName()
	{
		return folder.getName().length() == 0 ? documentRoot.getName() : folder.getName();
	}
	
	/**
	 * Lazily creates the HTML listing if it doesn't exist yet.
	 * 
	 * @return
	 */
	private HtmlCanvas getListing()
	{
		if (listing == null)
		{
			listing = new HtmlCanvas();
			try
			{
				HtmlListingWriter writer = new HtmlListingWriter(listing);

				writer.startDocument();
				writer.writeTitle(getDirectoryName());

				writer.startListing();

				File[] files = folder.listFiles();
				if (files != null)
				{
					for (File file : files)
					{
						if (file.isDirectory())
						{
							writer.writeDirectory(file);
						}
						else
						{
							writer.writeFile(file);
						}
					}
				}

				writer.closeListing();
				writer.closeDocument();

			}
			catch (IOException e)
			{
				LOG.error("Error creating the directory listing", e);
				return null;
			}

		}

		return listing;
	}

	// ~ inner classes ====================================================================/

	/**
	 * Provides a high-level interface for HTML document generation. Makes use of the Java XML APIs
	 * to ensure well-formed HTML output.
	 * 
	 */
	private class HtmlListingWriter
	{
		HtmlCanvas listing;

		public HtmlListingWriter(HtmlCanvas listing)
		{
			this.listing = listing;
		}

		public void writeTitle(String title) throws IOException
		{
			listing.head().title().content(title)._head();
		}

		public void startDocument() throws IOException
		{
			listing.html().body();
		}

		public void startListing() throws IOException
		{
			listing.h1().content("Folder \"" + getDirectoryName() + "\"");
			listing.ul();
		}

		public void writeFile(File file) throws IOException
		{
			listing.li();
			normalizePathAndWriteLink(file);
			listing._li();
		}

		public void writeDirectory(File file) throws IOException
		{
			listing.li();
			normalizePathAndWriteLink(file);
			listing._li();
		}

		public void closeListing() throws IOException
		{
			listing._ul();
		}

		public void closeDocument() throws IOException
		{
			listing._body()._html();
		}

		private void writeLink(String path, String name) throws IOException
		{
			listing.a(href(path)).content(name);
		}

		private void normalizePathAndWriteLink(File file) throws IOException
		{
			String path = file.getPath().replace(documentRoot.getPath(), "");

			// sanitize file path to support windows and unix-based OS.
			path = path.replaceAll("[" + Pattern.quote(File.separator) + "]+", "/");

			writeLink(path, file.getName());
		}

	}
}
