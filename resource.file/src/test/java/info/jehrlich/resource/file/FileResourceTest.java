package info.jehrlich.resource.file;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import info.jehrlich.server.resource.ResourceNotFoundException;
import info.jehrlich.server.resource.file.FileResource;
import info.jehrlich.server.resource.file.internal.MimeType;

import java.io.File;

import org.junit.Test;

public class FileResourceTest extends AbstractResourceTest<FileResource>
{

	@Override
	protected FileResource instantiateResource(File file)
	{
		return new FileResource(file, new MimeType(null));
	}

	
	@Test
	public void getContentLength_existingFile_realLength()
	{
		FileResource htmlFile = open("/index.html");
		assertThat(htmlFile.getContentLength(), equalTo(31L));
	}

	@Test
	public void getContentLength_nonExistingFile_zero()
	{
		FileResource htmlFile = open("/notExisting.html");
		assertThat(htmlFile.getContentLength(), equalTo(0L));
	}
	
	@Test
	public void getContentType_knownType_correctType()
	{
		FileResource htmlFile = open("/index.html");
		assertThat(htmlFile.getContentType(), equalTo("text/html"));

		FileResource imageFile = open("/plain.jpg");
		assertThat(imageFile.getContentType(), equalTo("image/jpeg"));
	}

	@Test
	public void getContentType_UnknownType_correctType()
	{
		FileResource htmlFile = open("/notExisting.xyz");
		assertThat(htmlFile.getContentType(), equalTo("content/unknown"));
	}
	
	@Test
	public void getContent_existingFile_correctContent() throws ResourceNotFoundException
	{
		FileResource htmlFile = open("/index.html");
		String content = readAsString(htmlFile.getContent());
		assertThat(content, containsString("<html><body>index</body></html>"));
	}

	@Test(expected=ResourceNotFoundException.class)
	public void getContent_NonExistingFile_throw() throws ResourceNotFoundException
	{
		FileResource htmlFile = open("/notExisting.html");
		htmlFile.getContent();
	}
	

}
