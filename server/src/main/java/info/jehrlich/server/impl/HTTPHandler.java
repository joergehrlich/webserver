package info.jehrlich.server.impl;

import info.jehrlich.server.Connection;
import info.jehrlich.server.Handler;
import info.jehrlich.server.resource.Resource;
import info.jehrlich.server.resource.ResourceProvider;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete {@link Handler} implementation to handle Http requests. It uses the Apache HttpCore
 * library. This is a wrapping handler that contains a list of {@link HttpRequestHandler} which are
 * executed in order on the request. It also retrieves the {@link Resource} from the provided
 * {@link ResourceProvider} and hands it to the request handling chain.
 * 
 * @author jehrlich
 * 
 */
public class HTTPHandler implements Handler, HttpRequestHandler
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private HttpService httpService;
	private ResourceProvider provider;
	/**
	 * The list of handlers that shall be invoked in order on incoming requests. Currently there is
	 * only the content handler but there could be others that control access or session handling as
	 * well as caching with Etag mechanism
	 */
	private List<HttpRequestHandler> handlerList;

	public HTTPHandler(ResourceProvider provider)
	{
		// Set up the HTTP protocol processor
		HttpProcessor httpproc = HttpProcessorBuilder.create()
										.add(new ResponseDate())
										.add(new ResponseServer("info.jehrlich.server/1.1"))
										.add(new ResponseContent())
										.add(new ResponseConnControl())
										.build();

		// Set up request handlers
		UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
		// Map all requests to this handler
		reqistry.register("*", this);

		// Set up the HTTP service
		httpService = new HttpService(httpproc, reqistry);

		this.provider = provider;

		// set up list of handlers that shall be used
		handlerList = new ArrayList<HttpRequestHandler>();
		handlerList.add(new HttpContentHandler());
	}

	/**
	 * @see Handler.handle(Connection)
	 */
	public void handle(Connection conn)
	{
		LOG.info("Start handling request.");

		try
		{
			HttpServerConnection httpConnection = ((ConnectionWrapper) conn).getHttpConnection();
			HttpContext context = new BasicHttpContext(null);
			httpService.handleRequest(httpConnection, context);
		}
		catch (Exception e)
		{
			LOG.error("Error handling request", e);
		}
	}

	/**
	 * Is invoked by the HttpCore {@link HttpService}. Retrieves the Resource and invokes the
	 * handler chain.
	 */
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException,
			IOException
	{
		String target = request.getRequestLine().getUri();

		if (provider != null)
		{
			Resource res = provider.create(URLDecoder.decode(target, "UTF-8"));
			context.setAttribute("resource", res);
		}

		try
		{
			for (HttpRequestHandler handler : handlerList)
			{
				handler.handle(request, response, context);
				// If a handler flags the request as done, do not continue invoking the other handlers
				if( context.getAttribute("isHandled") != null )
				{
					break;
				}
			}
		}
		catch(MethodNotSupportedException e)
		{
			response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
		}
	}

}
