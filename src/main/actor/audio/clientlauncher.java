package main.actor.audio;

import java.net.InetSocketAddress;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.io.Tcp;

public class clientlauncher {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create();
        system.actorOf(TcpClientActor.props(new InetSocketAddress(40001)));

     }
}
