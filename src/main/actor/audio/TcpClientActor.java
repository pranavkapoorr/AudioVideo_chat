package main.actor.audio;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.*;
import org.opencv.video.Video;

import akka.actor.*;
import akka.io.Tcp;
import akka.io.Tcp.*;
import akka.io.TcpMessage;
import akka.util.ByteString;

public class TcpClientActor extends AbstractActor {
	private final static Logger log = LogManager.getLogger(TcpClientActor.class);
	private final ActorRef tcpActor;
    private final InetSocketAddress remote;
    private ActorRef player;
    
 
    public static Props props(InetSocketAddress remote ) {
        return Props.create(TcpClientActor.class, remote);
    }

    private TcpClientActor(InetSocketAddress terminalIPandPort) {
        this.remote = terminalIPandPort;
        this.tcpActor = Tcp.get(getContext().system()).manager();
        	log.trace("starting TCP Client");
        	tcpActor.tell(TcpMessage.connect(remote), getSelf());
        	
    }
    
    @Override
    public void preStart() throws Exception {
    	log.info("Starting TCP client Actor");
    	this.player = context().actorOf(PlayingActor.props());
    }

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(CommandFailed.class, conn->{
					log.fatal("connectin Failed:"+conn);
					getContext().stop(getSelf());
				})
				.match(ByteString.class, b->{
                    log.info("bytestring in client1");
                    getSender().tell(TcpMessage.write(b), getSelf());
                })
				.match(Connected.class, conn->{
					log.info("connected :"+conn);
					//RecordingActor.isClientOn = true;
		            getSender().tell(TcpMessage.register(getSelf()), getSelf());
		            getContext().become(connected(getSender()));
		            
				}).build();
	}
	 private Receive connected(ActorRef sender) {
	        return receiveBuilder()
	               .match(Received.class, msg->{
	                    log.info("received in client");
	                   player.tell(msg.data().toArray(), getSelf());
	                    
	               }).match(ByteString.class, b->{
	                   log.info("bytestring in client");
	                   sender.tell(TcpMessage.write(b), getSelf());
	               }).match(String.class, s->{
	            	   log.info("String: "+s);
	               }).match(ConnectionClosed.class, closed->{
	            	   log.fatal("connectin cLOSED:"+closed);
	            	 
	               }).match(CommandFailed.class, conn->{
						log.fatal("connectin Failed:"+conn);
						
					})
	               .build();
	    }
	 @Override
	public void postStop() throws Exception {
		log.info("Stopping TCP client Actor");
	}
	
}



