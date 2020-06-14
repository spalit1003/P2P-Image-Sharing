import java.io.Serializable;

/**
 * @author shreyapalit
 * Class which handles list of all sub images
 *
 */
public class ImageList implements Serializable {
	PeerList peerList;
	ImageSub imageBlock;
	
	/**
	 * Parameterized Constructor
	 *
	 */
	public ImageList(ImageSub imageBlock,PeerList peerList) {
		this.imageBlock=imageBlock;
		this.peerList=peerList;
	}
	
	/**
	 * Synchronized Getter Method to get the Peer List
	 * @return peerList of type PeerList
	 */
	synchronized public PeerList getPeerList() {
		return peerList;
	}
	
	/**
	 * Synchronized Getter Method to get the Image Block
	 * @return imageBlock of type ImageSub
	 */
	synchronized public ImageSub getImageBlock() {
		return imageBlock;
	}
}
