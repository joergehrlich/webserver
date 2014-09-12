package info.jehrlich.server;

import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.PathUtils;

/**
 * This test is meant as an example of an integration test using Pax Exam
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ServerIT
{
	// =============================================================================/
	@Configuration
	public Option[] config()
	{
		String baseDir = PathUtils.getBaseDir();
		return options(
				mavenBundle("info.jehrlich", "resource.api", "0.0.1-SNAPSHOT"),
				mavenBundle("info.jehrlich", "resource.file", "0.0.1-SNAPSHOT"),
				mavenBundle("org.apache.httpcomponents", "httpcore-osgi", "4.3.2"),
				mavenBundle("org.apache.httpcomponents", "httpclient-osgi", "4.3.4"),
				mavenBundle("org.apache.sling", "org.apache.sling.commons.threads", "3.2.0"),
				mavenBundle("org.apache.sling", "org.apache.sling.commons.mime", "2.1.8"),
				mavenBundle("org.apache.sling", "org.apache.sling.commons.osgi", "2.1.0"),
				mavenBundle("org.apache.felix", "org.apache.felix.http.bundle", "2.3.0"), 
				mavenBundle("org.slf4j", "slf4j-api", "1.7.7"), 
				mavenBundle("org.slf4j", "slf4j-simple", "1.7.7").noStart(),
				mavenBundle("org.rendersnake", "rendersnake", "1.8"), 
				mavenBundle("org.apache.commons", "commons-lang3", "3.3.2"), 
				mavenBundle("org.apache.felix", "org.apache.felix.scr", "1.6.0"),
				mavenBundle("org.apache.felix", "org.apache.felix.configadmin", "1.8.0"),
				junitBundles(),
				url("reference:file:" + baseDir + "/target/classes"), // the actual bundle to test
				frameworkProperty("osgi.clean").value("false"),
				workingDirectory(baseDir + "/target/paxexam"));
	}

	// =============================================================================/
	// Utility methods

	private HttpResponse get(String path)
	{
		return send(new HttpGet(buildURI(path)));
	}

	private HttpResponse send(HttpUriRequest request)
	{
		try
		{
			CloseableHttpClient httpClient = HttpClients.createDefault();
			return httpClient.execute(request);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private URI buildURI(String path)
	{
		try
		{
			return new URIBuilder().setScheme("http").setHost("localhost").setPort(8080).setPath(path).build();
		}
		catch (URISyntaxException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
	// =============================================================================/
	// Tests
	
	// --- GET ---
	@Test
	public void get_existingFile_200() throws Exception
	{
		HttpResponse response = get("/index.html");
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		assertEquals("text/html", response.getEntity().getContentType().getValue());

		assertEquals("<html><body>index</body></html>", EntityUtils.toString(response.getEntity()));
	}

}
