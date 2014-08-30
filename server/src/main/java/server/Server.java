package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = true)
public class Server 
{
	private final Logger LOG = LoggerFactory.getLogger(Server.class);
	
	@Property(value = "8080")
	static final String PORT = "port";
	
	@Property(value = "20")
	static final String THREADPOOL_SIZE = "threadpoolSize";
	
	private ServerSocket serverSocket;
	private int port;
	private RequestListenerThread listener;
	
	/**
	 * 
	 * @param config
	 */
	@Activate
	protected void activate(Map<String, String> config)
	{
		try 
		{
			String portConfig = config.get(PORT);
			int port = portConfig != null ? Integer.parseInt(portConfig) : 0;
			
			this.serverSocket = new ServerSocket(port);
			
			listener = new RequestListenerThread(serverSocket);
			listener.setDaemon(false);
			listener.start();
		} 
		catch (IOException e) 
		{
			LOG.error("Cannot open Server on port " + port, e);
		}
	}

	/**
	 * 
	 */
	@Deactivate
	protected void deactivate()
	{
		try 
		{
			listener.stopIt();
        } 
		catch (IOException e) 
		{
			LOG.error("Error closing server", e);
        }
	}
	
	/**
	 * 
	 */
	@Modified
	private void modified(Map<String, String> config)
	{
		String portConfig = config.get(PORT);
		this.port = portConfig != null ? Integer.parseInt(portConfig) : 0;
		LOG.info("Updating server with new port" + port );
		
		//deactivate();
	}
	
	static class RequestListenerThread extends Thread {

		private final Logger LOG = LoggerFactory.getLogger(this.getClass());
        private final ServerSocket serversocket;
        private boolean isStopped = false;
        
        public RequestListenerThread( final ServerSocket sf){
            this.serversocket = sf;
        }

        public synchronized void stopIt() throws IOException
        {
            this.isStopped = true;
            this.serversocket.close();
        }
        
        @Override
        public void run() {
        	LOG.info("Listening on port " + this.serversocket.getLocalPort());
            while (!isStopped) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    LOG.info("Incoming connection from " + socket.getInetAddress());

                    // Start worker thread
                    Thread t = new WorkerThread(socket);
                    t.setDaemon(true);
                    t.start();
                } catch (InterruptedIOException ex) {
                    break;
                } catch (IOException e) {
                	LOG.error("I/O error initialising connection thread: "
                            + e.getMessage());
                    break;
                }
            }
        }
    }
	
	
	static class WorkerThread extends Thread {

		private final Logger LOG = LoggerFactory.getLogger(this.getClass());
		
		private Socket socket;
        
		public WorkerThread(final Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
        	LOG.info("New connection thread");
            try {
            	InputStream input  = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                long time = System.currentTimeMillis();
                output.write(("HTTP/1.1 200 OK\n\nWorkerThread: " +
                        time +
                        "").getBytes());
                output.close();
                input.close();
                LOG.info("Request processed: " + time);
            } catch (Exception ex) {
            	LOG.error("Error handling request");
            } 
        }

    }
}
