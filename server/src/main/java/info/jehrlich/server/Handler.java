package info.jehrlich.server;

import org.apache.http.HttpServerConnection;

public interface Handler
{
	public void handle(HttpServerConnection conn);

}
