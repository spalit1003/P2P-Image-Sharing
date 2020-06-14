import java.io.Serializable;
import java.util.ArrayList;


/**
 * @author shreyapalit
 * Class which handles the list of different Peers
 */
public class PeerList implements Serializable { 
	
	ArrayList<PeerInfo> peerList=new ArrayList<PeerInfo>();

	/**
	 * Synchronized Method to update the Peer List 
	 * @param newList of type PeerList
	 */
	synchronized public void updatePeerList(PeerList newList){
		peerList = newList.getPeerList();
	}

	/**
	 * Synchronized Method to get the Peer List
	 * @return peerList of type ArrayList PeerInfo
	 */
	synchronized public ArrayList<PeerInfo> getPeerList() {
		return peerList;
	}
	
	/**
	 * Synchronized Method to get size of the Peer List
	 * @return size of Peer List of type Int
	 */
	synchronized public int getSize() {
		return peerList.size();
	}
	
	/**
	 * Synchronized Method to print the Peer List
	 * 
	 */
	synchronized public void printPeerList() {
		
		System.out.println("There are " + peerList.size()+ " in the peer list");
		for (PeerInfo i:peerList)
			i.print();
	}

	/**
	 * Synchronized Method to add a Peer to Peer List 
	 * @param p of type PeerInfo
	 */
	synchronized public void addPeer(PeerInfo p) {
		peerList.add(p);
		System.out.println("Peer added! Current List:");
		printPeerList();
	}

	/**
	 * Synchronized Method to get IP of a peer in Peer List 
	 * @param i of type Int
	 * @return IP of type String of ith peer in Peer List
	 */
	synchronized public String getiIP(int i) {
		return peerList.get(i).getIP();
	}

	/**
	 * Synchronized Method to get Port of a peer in Peer List 
	 * @param i of type Int
	 * @return port of type int of ith peer in Peer List
	 */
	synchronized public int getiPort(int i) {
		return peerList.get(i).getPort();
	}

}
