import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author shreyapalit
 * This class deals with the peer interface
 */
public class ImagePeer {
	BufferedImage bufferedImage = new BufferedImage(400,400,BufferedImage.TYPE_INT_RGB);
	private ImageCanvas jp = new ImageCanvas();
	int[] pixels = new int[400*400];
	int[] subPixel = new int[400];
	ArrayList<ObjectOutputStream> OutputStreams=new ArrayList<ObjectOutputStream>();

	PeerList peerList=new PeerList();
	int myPort;
	String myIP, serverAddress;

	/**
	 * This is the main method from where the whole program starts
	 *
	 */
	public static void main(String[] args) {
		ImagePeer imagePeer=new ImagePeer();
		imagePeer.go();

	}

	/**
	 * Sets the port number
	 * @param port of type Int which has the port value
	 */
	synchronized public void setMyPort(int port) {
		System.out.println("I'm Port " + port);
		myPort=port;
	}

	/**
	 * Sets the IP number
	 * @param IP of type String which has the IP addrress value
	 */
	synchronized public void setMyIP(String IP) {
		myIP=IP;
	}

	/**
	 * Returns the port number
	 * @return myPort of type Int which is the port value
	 */
	synchronized public int returnMyPort() {
		return myPort;
	}

	/**
	 * Returns the port number
	 * @return myPort of type Int which is the port value
	 */
	synchronized public String returnMyIP() {
		return myIP;
	}
	
	/**
	 * Starts all the threads of a new peer, server GUI and for uploading server
	 *
	 */
	public void go() {
		String serverAddress = JOptionPane.showInputDialog(null,
				"Connect to server:",
				"Input IP Address",
				JOptionPane.QUESTION_MESSAGE);

		Thread guiThread = new Thread(new GUIThread());
		guiThread.start();

		Thread uploadServer=new Thread(new uploadServer());
		uploadServer.start();

		Thread connectServer=new Thread(new connectServer());
		connectServer.start();

		Thread downloader=new Thread(new Downloader(serverAddress,9000));
		downloader.start();
	}
	
	/**
	 * Class which contains the GUI of the peer interface, also implements Runnable
	 *
	 */
	public class GUIThread implements Runnable {
		private JFrame jf;
		BufferedImage origImg=null;
		BufferedImage grid[][]=new BufferedImage[20][20];

		/**
		 * Driver method for the serverGUI class
		 *
		 */
		public void run() {

			for (int i=0;i<20;i++)
				for(int j=0;j<20;j++)
					grid[i][j]=new BufferedImage(20,20,BufferedImage.TYPE_INT_RGB);
			try {
				origImg = ImageIO.read(new File("Default.png"));
			} catch (IOException e) {
			}
			Graphics g=bufferedImage.getGraphics();
			g.drawImage(origImg, 0,0,400,400,null);
			g.dispose();

			jf=new JFrame();
			jf.getContentPane().add(BorderLayout.CENTER,jp);
			jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jf.setSize(410,440);
			jf.setLocationRelativeTo(null);
			jf.setVisible(true);
		}
		
		/**
		 * Method which sets the pixels
		 *
		 */
		public void setPixel() {
			pixels=bufferedImage.getRGB(0,0,400,400,pixels,0,400);
		}
	}

	/**
	 * Class for painting the Canvas
	 *
	 */
	public class ImageCanvas extends Canvas {
		/**
		 * Driver method for the Upload class
		 * @param g of type Graphics 
		 */
		synchronized public void paint(Graphics g) {
			g.drawImage(bufferedImage,5,5,this);
			setPixel();
		}
	}

	/**
	 * Method which sets the pixels
	 *
	 */
	public void setPixel() {
		pixels=bufferedImage.getRGB(0,0,400,400,pixels,0,400);
	}

	/**
	 * Class for uploading image to a server by implementing Runnable
	 *
	 */
	public class uploadServer implements Runnable {

		int port;
		/**
		 * Driver method for the uploadServer class
		 *
		 */
		public void run() {
			
			port=9000;
			ServerSocket ss=null;
			while(ss==null&&port<65536) {
				try{
					ss=new ServerSocket(port);
				}catch(IOException e) {
				}
			}
			setMyPort(port);
			while(true) {
				Socket peerIn;
				try {
					peerIn = ss.accept();
					Thread t = new Thread(new Upload(peerIn));
					t.start();
				} catch (IOException e) {
				}
			}

		}
		public int returnPort() {
			System.out.println("return port invoked"+port);
			return port;
		}

		/**
		 * Class for uploading image to a server by splitting the image by implementing Runnable
		 *
		 */
		public class Upload implements Runnable{
			Socket peerIn;
			ObjectOutputStream peerOut;

			/**
			 * Parameterized Constructor
			 *
			 */
			public Upload(Socket peerInSocket) throws IOException {
				this.peerIn = peerInSocket;
				peerOut=new ObjectOutputStream(peerInSocket.getOutputStream());
			}
			
			/**
			 * Driver method for the Upload class
			 *
			 */
			public void run() {
				try{
					System.out.println("P2P Uploader set to send data to port:" + peerIn.getPort());

					int i=new Random().nextInt(20), j=new Random().nextInt(20);
					while (true) {
						Thread.sleep(350);
						peerOut.reset();
						int[] subPixel=new int[400];
						if (i<19) i++; 
						else if (j<19) {i=0; j++;}
						else {i=0;j=0;}
						subPixel=bufferedImage.getRGB(i*20,j*20,20,20,subPixel,0,20);
						peerOut.writeObject(new ImageList(new ImageSub(i,j,subPixel),peerList));  
						peerOut.flush();
					}
				}catch (Exception ex) {
				}
			}
		}
	}
	

	/**
	 * Class for connecting server to peer by implementing Runnable
	 *
	 */
	public class connectServer implements Runnable { 

		/**
		 * Driver method for the conectServer class
		 *
		 */
		synchronized public void run() {
			try {
				Socket sock = new Socket(serverAddress, 9000);
				ObjectOutputStream peerOut=new ObjectOutputStream(sock.getOutputStream());
				ObjectInputStream peerIn=new ObjectInputStream(sock.getInputStream());

				setMyIP(sock.getLocalAddress().getHostAddress());
				peerOut.writeObject(returnMyPort());

				peerIn.close();
				peerOut.close();

				sock.close();
			} catch(Exception ex) {
			}
		}
	}

	/**
	 * Class for downloading image from server by implementing Runnable
	 *
	 */
	public class Downloader implements Runnable {
		String serverIP;
		int serverPort;

		/**
		 * Parameterized Constructor
		 *
		 */
		public Downloader(String serverIP,int serverPort) {
			this.serverIP = serverIP;
			this.serverPort = serverPort;
		}
		
		/**
		 * Driver method for the Downloader class
		 *
		 */
		public void run() {
			try {
				Socket sock = new Socket(serverIP, serverPort);
				System.out.println("P2P Downloader set to download from " + serverIP + ": " + serverPort);

				ObjectInputStream peerIn=new ObjectInputStream(sock.getInputStream());

				while (true) {
					ImageList newBlock=(ImageList)peerIn.readObject();
					PeerList newPeerList=newBlock.getPeerList();
					if (newPeerList!=null&&peerList.getSize()<newPeerList.getSize())
						downloadAdd(newBlock.getPeerList());
					ImageSub sub=newBlock.getImageBlock();
					bufferedImage.setRGB(sub.x*20, sub.y*20, 20,20,sub.pixels,0,20);
					jp.repaint();
				}

			} catch(Exception ex) {
			}
		}
	}
	
	/**
	 * Synchronized method for updating the PeerList after downloading image
	 * @param newPeerList of type PeerList
	 */
	synchronized public void downloadAdd(PeerList newPeerList) {

		for(int i=peerList.getSize();i<newPeerList.getSize();i++) {
			if (newPeerList.getiIP(i)!=myIP&&newPeerList.getiPort(i)!=myPort) 
			{
				try {
					Thread newDownloader=new Thread(new Downloader(newPeerList.getiIP(i),newPeerList.getiPort(i)));
					newDownloader.start();
				}catch (Exception ex){ }
			}

		}
		System.out.println(myPort + "'s List updated!!!");
		peerList.updatePeerList(newPeerList);
	}
}
