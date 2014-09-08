package info.jehrlich.resource.file;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;
import info.jehrlich.server.resource.file.DirectoryResource;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;

public class DirectoryResourceTest extends AbstractResourceTest<DirectoryResource>
{
	
	@Test
	public void getContentType_sampleDirectory_html()
	{
		DirectoryResource folder = open("/");
		assertThat(folder.getContentType(), equalTo("text/html"));
	}

	@Test
	public void getContentLength_sampleDirectory_greaterZero()
	{
		DirectoryResource folder = open("/");
		assertThat(folder.getContentLength(), greaterThan(0L));
	}

	@Test
	public void getContent_sampleDirectory_correctListing()
	{
		DirectoryResource folder = open("/");
		// getContent will throw if invalid HTML
		InputStream input = folder.getContent();
		// correct title
		assertThat(readAsString(input), containsString("<title>sample</title>"));
		// correct links
		assertThat(readAsString(folder.getContent()), stringContainsInOrder(asList("href=\"/index.html\"", "href=\"/plain.jpg\"")));
	}


	@Override
	protected DirectoryResource instantiateResource(File file)
	{
		return new DirectoryResource(SampleFiles.DIR, file);
	}

}
