import java.io.Serializable;


/**
 * @author shreyapalit
 * Class which handles all the information of each Peer
 */
public class PeerInfo implements Serializable {
	
	private String IP;
	private int port;
	
	/**
	 * Parameterized Constructor
	 * 
	 */
	public PeerInfo (String IP,int port) {
		
		this.IP=IP;
		this.port=port;
	}
	
	/**
	 * Function to print the information of the Peer
	 * 
	 */
	public void print() {
		System.out.println("This peer's Info: " + IP+": " + port);
	}
	
	/**
	 * Getter function to return the IP addrress
	 * @return IP of type String
	 */
	public String getIP() {
		return IP;
	}
	
	/**
	 * Getter function to return the port number
	 * @return port of type Int
	 */
	public int getPort() {
		return port;
	}

}
