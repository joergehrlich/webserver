package info.jehrlich.server.impl;

import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.url;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import info.jehrlich.server.resource.ResourceProvider;

import javax.inject.Inject;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.PathUtils;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class HttpContentHandlerIT
{

	// Needed to resolve resources
	@Inject
	private ResourceProvider provider;
	
	// -------------------------------------------------------------------------------- Test Fixture
	@Configuration
	public Option[] config()
	{
		String baseDir = PathUtils.getBaseDir();
		return options(
				mavenBundle("info.jehrlich", "resource.api", "0.0.1-SNAPSHOT"),
				mavenBundle("info.jehrlich", "resource.file", "0.0.1-SNAPSHOT"),
				mavenBundle("org.apache.httpcomponents", "httpcore-osgi", "4.3.2"),
				mavenBundle("org.apache.sling", "org.apache.sling.commons.threads", "3.2.0"),
				mavenBundle("org.slf4j", "slf4j-api", "1.7.7"),
				mavenBundle("org.slf4j", "slf4j-simple", "1.7.7"),
				url("reference:file:" + baseDir + "/target/classes"), 		//the actual bundle to test
				mavenBundle("org.apache.felix", "org.apache.felix.scr", "1.6.0"),
	            frameworkProperty("osgi.clean").value("false"),
				systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO"),
				junitBundles()
			);
	}
	
	// =============================================================================/
	
	private final static String GET = "GET";
	private final static String HEAD = "HEAD";
	
	// --- GET ---
	@Test
	public void get_existingFile_200() throws Exception
	{
		HttpResponse response = send(GET, "/index.html");
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		assertEquals("text/html", response.getFirstHeader(HttpHeaders.LAST_MODIFIED).getValue());
		assertEquals("text/html", response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue());
		
		byte[] buffer = new byte[100];
		int num = response.getEntity().getContent().read(buffer);
		String content = new String(buffer, 0, num);
		assertEquals("<html><body>index</body></html>", content);
	}
	
	// =============================================================================/

	private HttpResponse send(String method, String target) throws Exception
	{
		HTTPHandler handler = new HTTPHandler(provider);

		HttpRequest request = new BasicHttpRequest(new BasicRequestLine(method, target, HttpVersion.HTTP_1_1));

		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "");
		HttpContext context = new BasicHttpContext();
		
		handler.handle(request, response, context);
		
		return response;
	}
}
