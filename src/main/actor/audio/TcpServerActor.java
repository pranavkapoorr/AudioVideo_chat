package main.actor.audio;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.io.TcpMessage;
import akka.io.Tcp.Bound;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.util.ByteString;

public class TcpServerActor  extends AbstractActor {
	private final static Logger log = LogManager.getLogger(TcpServerActor.class); 
	private  InetSocketAddress clientIP;
	public static volatile int clientnum = 0;
	final ActorRef manager;
	ActorRef sender;
	 private TcpServerActor(ActorRef manager,InetSocketAddress serverAddress) {
	        this.manager = manager;
	        	log.trace("starting TCP Server");
	        	manager.tell(TcpMessage.bind(getSelf(),serverAddress,100), getSelf());
	        	context().actorOf(RecordingActor.props(getSelf()));
	      
	    }

	  
	  
	  public static Props props(ActorRef tcpMnager, InetSocketAddress serverAddress) {
	    return Props.create(TcpServerActor.class, tcpMnager, serverAddress);
	  }

	  @Override
	public void preStart() throws Exception {
		  log.info("starting server");
	}

	  @Override
	  public Receive createReceive() {
	    return receiveBuilder()
	    	.match(Bound.class, msg -> {
	    		 log.trace("Server Status: "+msg);

	      })
	    	.match(String.class, s->log.info(s))
	    	
	      .match(CommandFailed.class, msg -> {
	    	  log.error(msg);
	    	  getContext().stop(getSelf());
	      
	      })
	      .match(Connected.class, conn -> {
	    	  log.trace("Server :"+conn);
	    	  clientIP = conn.remoteAddress();
	    	  sender = getSender();
	    	  final ActorRef handler = getContext().actorOf(TcpConnectionHandlerActor.props(clientIP),"handler"+clientIP.getHostString()+":"+clientIP.getPort());
	                /**
	                 * !!NB:
	                 * telling the aforesaid akka internal connection actor that the actor "handler"
	                 * is the one that shall receive its (the internal actor) messages.
	                 */
	                sender().tell(TcpMessage.register(handler), self());
	    	// }
	      })
	      .match(ByteString.class, b->{
	          log.trace("sending out frm server");
	          sender.tell(TcpMessage.write(b), getSelf());
	      })
	      .build();
	  }
	@Override
	public void postStop() throws Exception {
		log.info("stopping server");
	}
}