package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Connection {

	public void close() throws IOException;
	public InputStream getInputStream() throws IOException;
	public OutputStream getOutputStream() throws IOException;
}