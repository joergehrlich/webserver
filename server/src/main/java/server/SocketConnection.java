package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketConnection implements Connection
{
	private final Logger LOG = LoggerFactory.getLogger(SocketConnection.class);
    private final Socket socket;
    
    public SocketConnection(Socket socket) {
      this.socket = socket;
    }
    
    public String toString() {
      return "[" + socket.getRemoteSocketAddress().toString() + "] ";
    }
    
    /* (non-Javadoc)
	 * @see server.Connection#close()
	 */
    public void close() throws IOException {
      if (socket != null) {
        socket.close();
      }
    }
    

	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}
	
    
}
