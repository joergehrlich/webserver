package info.jehrlich.server.resource;

public class ResourceException extends Exception
{
	private static final long serialVersionUID = -2710624289935801026L;

	public ResourceException()
	{
		super();
	}

	public ResourceException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public ResourceException(String arg0)
	{
		super(arg0);
	}

	public ResourceException(Throwable arg0)
	{
		super(arg0);
	}
}
