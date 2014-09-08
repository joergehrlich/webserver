package info.jehrlich.server.resource.file.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for mime-type detection. Implements a simple file extension based approach.
 * 
 */
public class MimeType
{

	public static Map<String, String> mimeTypes = new HashMap<String, String>();

	static
	{
		mimeTypes.put("", "content/unknown");
		mimeTypes.put(".html", "text/html");
		mimeTypes.put(".txt", "text/plain");
		mimeTypes.put(".jpg", "image/jpeg");
		mimeTypes.put(".jpeg", "image/jpeg");
	}

	public static String of(File file)
	{
		return of(getFileExtension(file));
	}

	public static String of(String extension)
	{

		extension = extension.toLowerCase();

		if (!mimeTypes.containsKey(extension))
			return mimeTypes.get("");

		return mimeTypes.get(extension);
	}

	private static String getFileExtension(File file)
	{
		String filename = file.getName();
		int pos = filename.lastIndexOf(".");
		return pos >= 0 ? filename.substring(pos) : "";
	}

}
