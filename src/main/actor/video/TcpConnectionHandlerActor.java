package main.actor.video;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.io.TcpMessage;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.util.ByteString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class TcpConnectionHandlerActor extends AbstractActor {

	private final static Logger log = LogManager.getLogger(TcpConnectionHandlerActor.class); 
	private final InetSocketAddress clientIP;
	private ActorRef sender;

	
	public TcpConnectionHandlerActor(InetSocketAddress clientIP) {
		this.clientIP = clientIP;
		
	}

	public static Props props(InetSocketAddress clientIP) {
		return Props.create(TcpConnectionHandlerActor.class, clientIP);
	}
	
	@Override
	public void preStart() throws Exception {
		VideoCaptureActor.isServerOn = true;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Received.class, msg->{
					sender = getSender();
					log.info("received in server");
					BufferedImage image = Array2Image(msg.data().toArray());
					//if(image!=null){
					    MainFrame.screen2.setIcon(new ImageIcon(image));
					//}else{
					    //log.info("received NUL in server");
				//	}
					
				})
				.match(ByteString.class, b->{
				    log.info("bytestring in server");
	                sender.tell(TcpMessage.write(b), getSelf());
	            })
				.match(String.class, s->{
				    log.info("received STRING to send in server");
					sender.tell(TcpMessage.write(ByteString.fromString(s)), getSelf());
				})
				.match(BufferedWrapper.class, img->{
				    log.info("received image to send in server");
					sender.tell(TcpMessage.write(ByteString.fromArray(Image2Array(img.getImage()))), sender);
				})
				.match(ConnectionClosed.class, closed->{
					log.debug("Server: Connection Closure"+closed);
					getContext().stop(getSelf());
				})
				.match(CommandFailed.class, conn->{
					log.fatal("Server: "+conn);
					getContext().stop(getSelf());
				})
				.build();
	}
	public static byte[] Image2Array(BufferedImage img) throws IOException{
		BufferedImage originalImage = img;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( originalImage, "jpg", baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
	   // byte[] imageInByte = ((DataBufferByte) img.getData().getDataBuffer()).getData();
		return imageInByte;
	}
	public static  BufferedImage Array2Image(byte[] bytes) throws IOException{
		InputStream in = new ByteArrayInputStream(bytes);
		BufferedImage bImageFromConvert = ImageIO.read(in);
		return bImageFromConvert;
	}
	@Override
	public void postStop() throws Exception {
		getContext().parent().tell(clientIP, getSelf());
	}
	
}