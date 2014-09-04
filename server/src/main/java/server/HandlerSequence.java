package server;

import java.util.List;

import org.apache.http.HttpServerConnection;

public class HandlerSequence implements Handler
{

	private List<Handler> handlerList;
	
	public void add(Handler handler)
	{
		handlerList.add(handler);
	}
	
	public void remove(int index)
	{
		handlerList.remove(index);
	}
	
	public void handle(HttpServerConnection conn)
	{
		for(Handler handler : handlerList)
		{
			handler.handle(conn);
		}
	}

}
