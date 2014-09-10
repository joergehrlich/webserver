package info.jehrlich.server.resource.file.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.commons.mime.MimeTypeService;

/**
 * Utility class for mime-type detection. It uses Sling {@link MimeTypeService} to detect mimetypes.
 * For testing puuposes it also provides a simple file extension based approach that only knows a
 * few mime-types
 * 
 */
public class MimeType
{
	// Fallback list of known mimetypes
	public static Map<String, String> mimeTypes = new HashMap<String, String>();

	static
	{
		mimeTypes.put("", "content/unknown");
		mimeTypes.put(".html", "text/html");
		mimeTypes.put(".txt", "text/plain");
		mimeTypes.put(".jpg", "image/jpeg");
	}

	// Handle to the Sling MimeTypeService
	private MimeTypeService mimeService;

	public MimeType(MimeTypeService mimeService)
	{
		this.mimeService = mimeService;
	}

	/**
	 * Taken from {@link MimeTypeService} javadoc: Returns the MIME type of the extension of the
	 * given name. The extension is the part of the name after the last dot. If the name does not
	 * contain a dot, the name as a whole is assumed to be the extension.
	 * 
	 * @param fileName
	 * @return the mime-type
	 */
	public String getMimeType(String fileName)
	{
		String mimetype;

		if (mimeService != null)
		{
			mimetype = mimeService.getMimeType(fileName);
		}
		else
		{
			String extension = getFileExtension(fileName);
			extension = extension.toLowerCase();

			if (!mimeTypes.containsKey(extension))
			{
				mimetype = mimeTypes.get("");
			}
			else
			{
				mimetype = mimeTypes.get(extension);
			}
		}

		return mimetype;
	}

	private String getFileExtension(String fileName)
	{
		int pos = fileName.lastIndexOf(".");
		return pos >= 0 ? fileName.substring(pos) : "";
	}

}
