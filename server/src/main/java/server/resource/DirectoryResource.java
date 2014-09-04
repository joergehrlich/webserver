package server.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.regex.Pattern;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.SocketConnector;

/**
 * {@link Resource} implementation representing a directory under the current
 * document root.
 * 
 * The resource renders as an HTML document providing a listing of all files and
 * sub-directories within the given folder.
 * 
 */
public class DirectoryResource implements Resource {
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private File documentRoot;
	private File folder;
	private HtmlListing listing;
	
	/**
	 * Constructor. 
	 * 
	 * @param documentRoot
	 * 	the root folder our server is serving files form.
	 * @param folder
	 * 	the requested folder represented by this resource.
	 */
	public DirectoryResource(File documentRoot, File folder) {
		this.documentRoot = documentRoot;
		this.folder = folder;
	}
	
//~ public methods ===================================================================/

	public long getContentLength() {
		return getListing().getLength();
	}

	public String getContentType() {
		return MimeType.of(".html");
	}

	public InputStream getContent() {
		return getListing().toInputStream();		
	}
	
	/**
	 * Returns the latest modification date of any file or folder under the active directory.
	 */
	public Date getLastModified() {
		File[] files = folder.listFiles();
		if(files != null) {
			
			long lastModified = folder.lastModified();
			for(File file : files) {
				lastModified = Math.max(lastModified, file.lastModified());
			}
			return new Date(lastModified);
			
		}
		return null;
	}

//~ internal helpers ==================================================================/
	
	/**
	 * Lazily creates the HTML listing if it doesn't exist yet.
	 * @return
	 */
	private HtmlListing getListing() {

		if(listing == null) {
			
			try {
				HtmlListingWriter writer = new HtmlListingWriter();
			
				writer.startDocument();
				writer.writeTitle(folder.getName());
				
				writer.startListing();

				File[] files = folder.listFiles();
				if(files != null) {
					for(File file : files) {
						if(file.isDirectory()) {
							writer.writeDirectory(file);
						} else {
							writer.writeFile(file);
						}
					}
				}
				
				writer.closeListing();
				writer.closeDocument();
				
				listing = writer.toListing();
			} catch (Exception e) {
				LOG.error("error", e);
				e.printStackTrace();
				return null;
			}
			
		} 
		
		return listing;
	}


//~ inner classes ====================================================================/
			
	/**
	 * Helper class storing the HTML document once generated.
	 * Calculates the content-length and opens an {@link InputStream} on the data.
	 * 
	 */
	private class HtmlListing {
		
		private String html;

		public HtmlListing(String html) {
			this.html = html;
		}
		
		public long getLength() {
			return html.length();
		}
		
		public InputStream toInputStream() {
			return new ByteArrayInputStream(html.getBytes());
		}
		
	}
	
	/**
	 * Provides a high-level interface for HTML document generation.
	 * Makes use of the Java XML APIs to ensure well-formed HTML output. 
	 * 
	 */
	private class HtmlListingWriter {

		private XMLStreamWriter xml;
		private ByteArrayOutputStream out;

		public HtmlListingWriter() throws XMLStreamException {
			out = new ByteArrayOutputStream();
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			xml = outputFactory.createXMLStreamWriter(out);
		}
		
		public void closeListing() throws XMLStreamException {
			xml.writeEndElement();
		}

		public void startListing() throws XMLStreamException {
			writeTag("h1", "Folder \"" + folder.getName() + "\"");
			xml.writeStartElement("ul");
		}

		public void writeFile(File file) throws XMLStreamException {
			normalizePathAndWriteLink(file);
		}
		
		public void writeDirectory(File file) throws XMLStreamException {
			normalizePathAndWriteLink(file);
		}

		public void startDocument() throws XMLStreamException {
			xml.writeStartDocument();
			xml.writeStartElement("html");
			xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
		}

		public void writeTitle(String title) throws XMLStreamException {
			writeTag("title", title);
		}
		
		public void closeDocument() throws XMLStreamException, IOException {
			xml.writeEndElement();
			xml.writeEndDocument();
			
			xml.close();
			out.close();
		}
		
		public HtmlListing toListing() {
			String html = new String(out.toByteArray());
			return new HtmlListing(html);
		}

		private void writeTag(String tagName, String text) throws XMLStreamException {
			xml.writeStartElement(tagName);
			xml.writeCharacters(text);
			xml.writeEndElement();
		}

		private void normalizePathAndWriteLink(File file) throws XMLStreamException {
			String path = file.getPath().replace(documentRoot.getPath(), "");

			// sanitize file path to support windows and unix-based OS.
			path = path.replaceAll("[" + Pattern.quote(File.separator) + "]+", "/");
			
			writeLink(path, file.getName());
		}

		private void writeLink(String path, String name) throws XMLStreamException {
			xml.writeStartElement("li");
			xml.writeStartElement("a");
			xml.writeAttribute("href", path);
			xml.writeCharacters(name);
			xml.writeEndElement();
			xml.writeEndElement();
		}
	}


}
