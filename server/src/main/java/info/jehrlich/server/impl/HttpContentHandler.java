package info.jehrlich.server.impl;

import info.jehrlich.server.resource.Resource;
import info.jehrlich.server.resource.ResourceAccessException;
import info.jehrlich.server.resource.ResourceNotFoundException;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

/**
 * Handles the actual content part of the request.
 * The implementation is pretty minimal and handles only a few HEAD, GET and POST cases.
 *
 */
public class HttpContentHandler implements HttpRequestHandler
{
	/**
	 * @see HttpRequestHandler.(HttpRequest, HttpResponse, HttpContext)
	 */
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException
	{
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		if (!"GET".equals(method) && !"HEAD".equals(method)  && !"POST".equals(method))
		{
			throw new MethodNotSupportedException(method + " method not supported");
		}

		Object resourceAttribute = context.getAttribute("resource");
		if( resourceAttribute != null )
		{
			Resource res = (Resource) resourceAttribute;
	
			try
			{
				if ("GET".equals(method) || "HEAD".equals(method) )
				{
					if (!res.exists())
					{
						throw new ResourceNotFoundException();
					}
					if( !res.canRead() )
					{
						throw new ResourceAccessException(); 
					}
					
					if( "GET".equals(method) )
					{
						InputStreamEntity body = new InputStreamEntity(res.getContent());
						body.setContentType(new BasicHeader(HttpHeaders.CONTENT_TYPE, res.getContentType()));
						response.setEntity(body);
					}
					else
					{
						response.setHeader(HttpHeaders.CONTENT_TYPE, res.getContentType());
						response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(res.getContentLength()));
					}
				}
				else if ("POST".equals(method))
				{
					if (request instanceof HttpEntityEnclosingRequest)
					{
						if( !res.canWrite() )
						{
							throw new ResourceAccessException(); 
						}
						
						HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
						entity.writeTo(res.getContentWriter());
					}
					else
					{
						response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
					}
				}
				
				response.setStatusCode(HttpStatus.SC_OK);
			}
			catch (ResourceNotFoundException e)
			{
				response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			}
			catch (ResourceAccessException e)
			{
				response.setStatusCode(HttpStatus.SC_FORBIDDEN);
			}
		}
		else
		{
			// If no resource is available, return 404
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
		}
		
		// Flag that the request is handled
		context.setAttribute("isHandled", true);
	}

}
