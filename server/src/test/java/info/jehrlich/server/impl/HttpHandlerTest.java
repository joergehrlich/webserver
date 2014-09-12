package info.jehrlich.server.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import info.jehrlich.server.resource.file.FileResourceProvider;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
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
	private HttpResponse send(HttpRequest request) throws Exception
	{
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "");
		HttpContext context = new BasicHttpContext();
		
		handler.handle(request, response, context);
		
		return response;
	}
	
	private HttpResponse head(String target) throws Exception
	{
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("HEAD", target, HttpVersion.HTTP_1_1));
		return send(request);
	}
	
	private HttpResponse get(String target) throws Exception
	{
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("GET", target, HttpVersion.HTTP_1_1));
		return send(request);
	}
	
	private HttpResponse post(String target) throws Exception
	{
		HttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(new BasicRequestLine("POST", target, HttpVersion.HTTP_1_1));
		
		HttpEntity entity = new StringEntity("content");
		request.setEntity(entity);
		
		return send(request);
	}
	
	// =============================================================================/

	// --- GET ---
	@Test
	public void get_existingFile_200() throws Exception
	{
		HttpResponse response = get("/index.html");
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
		
		assertThat(EntityUtils.toString(response.getEntity()), equalTo("<html><body>index</body></html>"));
		assertThat(response.getEntity().getContentType().getValue(), equalTo("text/html"));
	}

	@Test
	public void get_nonExistingFile_404() throws Exception
	{
		HttpResponse response = get("/notexisting");
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));
		assertThat(response.getEntity(), nullValue());
	}
	
	// --- HEAD ---
	@Test
	public void head_existingFile_200() throws Exception
	{
		HttpResponse response = head("/index.html");
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
		assertThat(response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue(), equalTo("text/html"));
		assertThat(response.getEntity(), nullValue());
	}

	@Test
	public void head_nonExistingFile_404() throws Exception
	{
		HttpResponse response = head("/notexisting");
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));
		assertThat(response.getEntity(), nullValue());
	}
	
	// --- POST ---
	@Test
	public void post_file_200() throws Exception
	{
		HttpResponse response = post("/newFile.jpg");
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
		
		response = get("/newFile.jpg");
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
		assertThat(response.getEntity().getContentType().getValue(), equalTo("image/jpeg"));
		assertThat(EntityUtils.toString(response.getEntity()), equalTo("content"));
	}
	
	// --- Unsupported ---
	@Test
	public void unsupportedMethod_501() throws Exception
	{
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("DELETE", "/notexisting", HttpVersion.HTTP_1_1));
		HttpResponse response = send(request);
		assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_IMPLEMENTED));
	}
}
