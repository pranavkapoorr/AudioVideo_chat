package main.actor.audio;

import java.net.InetSocketAddress;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.io.Tcp;

public class serverLauncher {

    public static void main(String[] args) {
       ActorSystem system = ActorSystem.create();
       ActorRef tcpMnager =Tcp.get(system).manager();
       system.actorOf(TcpServerActor.props(tcpMnager, new InetSocketAddress(40001)));
      

    }

}
