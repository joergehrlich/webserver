package info.jehrlich.server.impl;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import info.jehrlich.server.resource.file.FileResourceProvider;

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
import org.apache.http.util.EntityUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class HttpHandlerTest
{
	private final static String GET = "GET";
	private final static String HEAD = "HEAD";
	
	private static FileResourceProvider provider;
	private static HTTPHandler handler;
	
	@BeforeClass
	public static void setup()
	{
		Map<String, String> config = new HashMap<String, String>();
		config.put("rootPath", SampleFiles.DIR.getAbsolutePath());
		
		provider = new FileResourceProvider();
		provider.activate(config);
		
		handler = new HTTPHandler(provider);
	}
	
	// Helper to create request
	private HttpResponse send(String method, String target) throws Exception
	{
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine(method, target, HttpVersion.HTTP_1_1));

		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "");
		HttpContext context = new BasicHttpContext();
		
		handler.handle(request, response, context);
		
		return response;
	}
		
	// =============================================================================/

	// --- GET ---
	@Test
	public void get_existingFile_200() throws Exception
	{
		HttpResponse response = send(GET, "/index.html");
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
		
		assertThat(EntityUtils.toString(response.getEntity()), equalTo("<html><body>index</body></html>"));
		assertThat(response.getEntity().getContentType().getValue(), equalTo("text/html"));
	}

	@Test
	public void get_nonExistingFile_404() throws Exception
	{
		HttpResponse response = send(GET, "/notexisting");
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));
		assertThat(response.getEntity(), nullValue());
	}
	
	// --- HEAD ---
	@Test
	public void head_existingFile_200() throws Exception
	{
		HttpResponse response = send(HEAD, "/index.html");
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
		assertThat(response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue(), equalTo("text/html"));
		assertThat(response.getEntity(), nullValue());
	}

	@Test
	public void head_nonExistingFile_404() throws Exception
	{
		HttpResponse response = send(HEAD, "/notexisting");
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));
		assertThat(response.getEntity(), nullValue());
	}
	
	// --- POST ---
	
	// --- Unsupported ---
	@Test
	public void unsupportedMethod_501() throws Exception
	{
		HttpResponse response = send("DELETE", "/notexisting");
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_IMPLEMENTED));
	}
}
