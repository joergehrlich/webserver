package info.jehrlich.server;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;


import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ServerIT
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	// -------------------------------------------------------------------------------- Test Fixture
	@Configuration
	public Option[] config()
	{
		String baseDir = PathUtils.getBaseDir();
		return options(
				mavenBundle("info.jehrlich", "resource.api", "0.0.1-SNAPSHOT"),
				mavenBundle("info.jehrlich", "resource.file", "0.0.1-SNAPSHOT"),
				mavenBundle("org.apache.httpcomponents", "httpcore-osgi", "4.3.2"),
				// This bundle is wrongly configured, it states all imports explicitely but left one (javax.naming) out.
				mavenBundle("org.apache.httpcomponents", "httpclient-osgi", "4.3.5"),
//				wrappedBundle(mavenBundle("org.apache.httpcomponents", "httpclient", "4.3.2")),
//					.bundleSymbolicName("org.apache.httpcomponents.httpclient")
//					.bundleVersion("4.3.5")
//					.exports("*;version=4.3.5").noStart(),
				mavenBundle("org.apache.sling", "org.apache.sling.commons.threads", "3.2.0"),
				mavenBundle("org.slf4j", "slf4j-api", "1.7.7"), 
				mavenBundle("org.rendersnake", "rendersnake", "1.8"), 
				mavenBundle("org.apache.commons", "commons-lang3", "3.3.2"), 
				mavenBundle("org.slf4j", "slf4j-simple", "1.7.7").noStart(),
				mavenBundle("org.apache.felix", "org.apache.felix.scr", "1.6.0"),
				mavenBundle("org.apache.felix", "org.apache.felix.configadmin", "1.8.0"),
				url("reference:file:" + baseDir + "/target/classes"), // the actual bundle to test
				// mavenBundle("org.apache.felix", "org.apache.felix.fileinstall", "3.4.0"),
				frameworkProperty("osgi.clean").value("false"),
//				systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO"), 
				workingDirectory(baseDir + "/target/paxexam"),
				// systemProperty("felix.fileinstall.dir").value(baseDir + "/src/test/resources/config"),
				junitBundles());
	}

	// =============================================================================/

	// --- GET ---
	@Test
	public void get_existingFile_200() throws Exception
	{
//		HttpResponse response = get("/index.html");
//		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
//		assertThat(response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue(), equalTo("text/html"));
//
//		assertThat(EntityUtils.toString(response.getEntity()), equalTo("<html><body>index</body></html>"));
	}

	// =============================================================================/

	private Header[] head(String path)
	{
		HttpResponse response = send(new HttpHead(buildURI(path)));
		assertThat(response.getEntity(), nullValue());
		return response.getAllHeaders();
	}

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
}
