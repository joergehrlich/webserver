package server.resource;

import java.io.FileNotFoundException;

public interface ResourceFactory
{
	void initialize(String root);
	
	/**
	 * Checks if the requested path points to a directory or a file and returns
	 * the appropriate implementation.
	 * 
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	Resource create(String path) throws Exception;

}