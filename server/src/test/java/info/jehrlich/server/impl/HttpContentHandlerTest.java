package info.jehrlich.server.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import org.junit.Test;

public class HttpContentHandlerTest
{
	private final static String GET = "GET";
	private final static String HEAD = "HEAD";
	
	// --- GET ---
//	@Test
//	public void get_existingFile_200() throws Exception
//	{
//		HttpResponse response = send(GET, "/index.html");
//		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
//		assertEquals("text/html", response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue());
//		
//		byte[] buffer = new byte[100];
//		int num = response.getEntity().getContent().read(buffer);
//		String content = new String(buffer, 0, num);
//		assertEquals("<html><body>index</body></html>", content);
//	}

	@Test
	public void get_nonExistingFile_404() throws Exception
	{
		HttpResponse response = send(GET, "/notexisting");
		assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
		assertNull(response.getEntity());
	}
	
	// --- HEAD ---
//	@Test
//	public void head_existingFile_200() throws Exception
//	{
//		HttpResponse response = send(HEAD, "/index.html");
//		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
//		assertEquals("text/html", response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue());
//		
//		assertNull(response.getEntity());
//	}

	@Test
	public void head_nonExistingFile_404() throws Exception
	{
		HttpResponse response = send(HEAD, "/notexisting");
		assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
		assertNull(response.getEntity());
	}
	

	// --- POST ---
	

	
	// =============================================================================/

	private HttpResponse send(String method, String target) throws Exception
	{
		Map<String, String> config = new HashMap<String, String>();
		config.put("rootPath", SampleFiles.DIR.getAbsolutePath());
		
		FileResourceProvider provider = new FileResourceProvider();
		provider.activate(config);
		
		HTTPHandler handler = new HTTPHandler(provider);

		HttpRequest request = new BasicHttpRequest(new BasicRequestLine(method, target, HttpVersion.HTTP_1_1));

		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "");
		HttpContext context = new BasicHttpContext();
		
		handler.handle(request, response, context);
		
		return response;
	}

}
