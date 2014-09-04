package info.jehrlich.server;

import java.io.IOException;

public interface Connection {

	public void close() throws IOException;
	public <T> T getBaseConnection();
}