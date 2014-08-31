package server;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HTTPHandler implements Handler 
{
	private final Logger LOG = LoggerFactory.getLogger(HTTPHandler.class);
	
	public void handle(Connection conn) 
	{
		LOG.info(this + "Start handling request.");
        
	    try {
	        InputStream input  = conn.getInputStream();
	        OutputStream output = conn.getOutputStream();
	        long time = System.currentTimeMillis();
	        output.write(("HTTP/1.1 200 OK\n\nConnection: " +
	                time +
	                "").getBytes());
	        output.close();
	        input.close();
	        LOG.info("Request processed: " + time);
	    } catch (Exception ex) {
	    	LOG.error("Error handling request");
	    } 
	    
	    LOG.info(this + "Finished handling connection.");
	}


}
