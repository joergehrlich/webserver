package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
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

import server.resource.FileResourceProvider;
import server.resource.Resource;
import server.resource.FileResourceFactory;
import server.resource.ResourceFactory;
import server.resource.ResourceProvider;

public class HTTPHandler implements Handler, HttpRequestHandler
{
	private final Logger LOG = LoggerFactory.getLogger(HTTPHandler.class);

	private HttpService httpService;
	private ResourceFactory resourceFactory;
	
    //private final HttpServerConnection conn;
    
	public HTTPHandler()
	{
		// Set up the HTTP protocol processor
        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("info.jehrlich.server/1.1"))
                .add(new ResponseContent())
                .add(new ResponseConnControl()).build();

        // Set up request handlers
        UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
        reqistry.register("*", this);

        // Set up the HTTP service
        httpService = new HttpService(httpproc, reqistry);
        
        ResourceProvider provider = new FileResourceProvider();
        resourceFactory.initialize("C:\\Users\\jehrlich\\Desktop\\server");
	}
	
	public void handle(HttpServerConnection conn)
	{
		LOG.info(this + "Start handling request.");

		try
		{
			HttpContext context = new BasicHttpContext(null);
			httpService.handleRequest(conn, context);
		} catch (Exception ex)
		{
			LOG.error("Error handling request");
		}

		LOG.info(this + "Finished handling connection.");
	}

	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException
	{
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (!method.equals("GET")) {
            throw new MethodNotSupportedException(method + " method not supported");
        }
        String target = request.getRequestLine().getUri();

        try
        {
        	Resource res = resourceFactory.create(URLDecoder.decode(target, "UTF-8"));
        	
        	response.setStatusCode(HttpStatus.SC_OK);
            InputStreamEntity body = new InputStreamEntity(res.getContent());
            response.setEntity(body);
            response.setHeader("Content-Type", res.getContentType());
            LOG.info("Serving file " + target);
        }
        catch (Exception e)
        {
        	 response.setStatusCode(HttpStatus.SC_NOT_FOUND);
             StringEntity entity = new StringEntity(
                     "<html><body><h1>File" + target +
                     " not found</h1></body></html>",
                     ContentType.create("text/html", "UTF-8"));
             response.setEntity(entity);
        }
        
        
//        if (!file.canRead() || file.isDirectory()) {
//
//            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
//            StringEntity entity = new StringEntity(
//                    "<html><body><h1>Access denied</h1></body></html>",
//                    ContentType.create("text/html", "UTF-8"));
//            response.setEntity(entity);
//            System.out.println("Cannot read file " + file.getPath());
//
//        } else {
//
//            response.setStatusCode(HttpStatus.SC_OK);
//            FileEntity body = new FileEntity(file, ContentType.create("text/html", (Charset) null));
//            response.setEntity(body);
//            System.out.println("Serving file " + file.getPath());
//        }
        
//		 response.setStatusCode(HttpStatus.SC_OK);
//		 StringEntity entity = new StringEntity(
//                 "<html><body><h1>Hello World</h1></body></html>",
//                 ContentType.create("text/html", "UTF-8"));
//         response.setEntity(entity);
	}

}
