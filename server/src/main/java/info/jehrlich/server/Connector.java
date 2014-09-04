package info.jehrlich.server;

/**
 * Connector interface. 
 */
public interface Connector {
  /**
   * Set socket port.
   * @param port Socket port.
   */
  public void setPort(int port);
  
  /**
   * Get socket port.
   * @return Socket port.
   */
  public int getPort();
  
  /**
   * Set socket host name.
   * @param host Host name.
   */
  public void setHost(String host);
  
  /**
   * Get socket host name.
   * @return Socket host name.
   */
  public String getHost();
  
  /**
   * Set the server handling the requests.
   * @param server 
   */
  public void setServer(Server server);
  
  /**
   * Get the server handling requests.
   * @return server
   */
  public Server getServer();

  /**
   * Start connector.
   * @throws Exception
   */
  public void start() throws Exception;
  
  /**
   * Stop connector.
   * @throws Exception
   */
  public void stop() throws Exception;
}
