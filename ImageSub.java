import java.io.Serializable;


/**
 * @author shreyapalit
 * Class which handles sub parts of images
 *
 */
public class ImageSub implements Serializable {
	
	int x;
	int y;
	int[] pixels;

	/**
	 * Parameterized Constructor
	 *
	 */
	public ImageSub(int x, int y, int[] pixels) {
		this.x=x;
		this.y=y;
		this.pixels=pixels;
	}
}
