import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author shreyapalit
 * This class deals with the server interface
 *
 */
public class ImageServer {

	BufferedImage bufferedImage=new BufferedImage(400,400,BufferedImage.TYPE_INT_RGB);
	PeerList peerList=new PeerList();
	ArrayList<PrintWriter> PrintWriters=new ArrayList<PrintWriter>();
	ArrayList<ObjectOutputStream> OutputStreams=new ArrayList<ObjectOutputStream>();
	int[] subPixel=new int[400];


	/**
	 * This is the main method from where the whole program starts
	 *
	 */
	public static void main(String[] args) {
		ImageServer imageServer = new ImageServer();
		imageServer.go();

	}

	/**
	 * Starts all the threads of a new peer, server GUI and for uploading server
	 *
	 */
	public void go() {

		Thread newPeer = new Thread(new newPeer());
		newPeer.start();

		Thread serverGUI = new Thread(new serverGUI());
		serverGUI.start();

		Thread uploadServer=new Thread(new uploadServer());
		uploadServer.start();
	}

	/**
	 * Class for connecting a new peer to the server by implementing Runnable
	 *
	 */
	public class newPeer implements Runnable {

		/**
		 * Driver method for the newPeer class
		 *
		 */
		public void run() {

			try {
				ServerSocket ss = new ServerSocket(9000);

				while(true) {
					Socket newP = ss.accept(); 

					ObjectInputStream peerIn=new ObjectInputStream(newP.getInputStream());
					ObjectOutputStream peerOut=new ObjectOutputStream(newP.getOutputStream());

					PeerInfo thisP=new PeerInfo(newP.getInetAddress().getHostAddress().toString(),(int)peerIn.readObject());
					thisP.print();
					peerList.addPeer(thisP);

					peerOut.flush();
					peerOut.close();
					peerIn.close();

					newP.close();
				}
			}catch(Exception ex) {
			}
		}
	}
	
	/**
	 * Class which contains the GUI of the server interface, also implements Runnable
	 *
	 */
	public class serverGUI implements Runnable {
		private JFrame jf;
		private JButton jb;
		private ImageCanvas jp;
		JFileChooser jfc;
		BufferedImage origImg=null;
		int[] pixels=new int[400*400];

		/**
		 * Driver method for the serverGUI class
		 *
		 */
		public void run() {

			try {
				origImg = ImageIO.read(new File("Flower.png"));
				} catch (IOException e) {
			}
			Graphics g=bufferedImage.getGraphics();
			g.drawImage(origImg, 0,0,400,400,null);
			g.dispose();
			pixels=bufferedImage.getRGB(0,0,400,400,pixels,0,400);

			jfc=new JFileChooser();
			try{
				if (jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
					try {
						origImg = ImageIO.read(new File(jfc.getSelectedFile().getPath()));
					} catch (IOException ex) {
					}
					g=bufferedImage.getGraphics();
					g.drawImage(origImg, 0,0,400,400,null);
					g.dispose();
				}
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(null, "", "Picture Load failed", JOptionPane.ERROR_MESSAGE);
			}
			pixels=bufferedImage.getRGB(0,0,400,400,pixels,0,400);

			jf=new JFrame();
			jb=new JButton("Load another image");
			jb.addActionListener(new fileChooserListener());
			jp=new ImageCanvas();
			jf.getContentPane().add(BorderLayout.CENTER,jp);
			jf.getContentPane().add(BorderLayout.SOUTH,jb);
			jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jf.setSize(410,450);
			jf.setLocationRelativeTo(null);
			jf.setVisible(true);
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
			public void paint(Graphics g) {
				g.drawImage(bufferedImage,5,5,this);

			}
		}
		
		/**
		 * Class which shows the file chooser and enables user to choose file by implementing ActionListener
		 *
		 */
		public class fileChooserListener implements ActionListener { 
			public void actionPerformed(ActionEvent e){
				jfc=new JFileChooser();
				try{
					if (jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
						try {
							origImg = ImageIO.read(new File(jfc.getSelectedFile().getPath()));
						} catch (IOException ex) {
						}
						Graphics g=bufferedImage.getGraphics();
						g.drawImage(origImg, 0,0,400,400,null);
						g.dispose();
					}
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "", "Picture Load failed", JOptionPane.ERROR_MESSAGE);
				}
				pixels=bufferedImage.getRGB(0,0,400,400,pixels,0,400);
				jp.repaint();
			}
		}
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
			while(ss==null && port < 65536) {
				try{
					ss=new ServerSocket(port);
				}catch(IOException e) {

				}
			}
			while(true) {
				try {
					Socket peerIn = ss.accept();
					Thread t = new Thread(new Upload(peerIn));
					t.start();
				} catch (IOException e) {
				}
			}
		}

		/**
		 * Class for uploading image to a server by splitting the image by implementing Runnable
		 *
		 */
		public class Upload implements Runnable {   
			Socket peerIn;
			ObjectOutputStream peerOut;

			/**
			 * Parameterized Constructor
			 *
			 */
			public Upload(Socket peerIn) throws IOException {
				this.peerIn=peerIn;
				peerOut=new ObjectOutputStream(peerIn.getOutputStream());
				OutputStreams.add(peerOut);
			}

			/**
			 * Driver method for the Upload class
			 *
			 */
			public void run() {
				try{
					int i=new Random().nextInt(20), j=new Random().nextInt(20);
					while (true) 
					{
						Thread.sleep(150);
						peerOut.reset();
						if (i<19) i++; 
						else if (j<19) {i=0; j++;}
						else {i=0;j=0;}
						subPixel=bufferedImage.getRGB(i*20,j*20,20,20,subPixel,0,20);
						peerOut.writeObject(new ImageList(new ImageSub(i,j,subPixel),peerList));  
						peerOut.flush();
					}
				}	
				catch (Exception ex){
				}
			}
		}
	}
}
