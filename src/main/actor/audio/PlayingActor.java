package main.actor.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class PlayingActor extends AbstractActor{
private SourceDataLine sourceDataLine;
    public static Props props() {
        return Props.create(PlayingActor.class);
    }
    @Override
    public void preStart() throws Exception {
        DataLine.Info dataLineInfo = new DataLine.Info( SourceDataLine.class , getAudioFormat() ) ;
        sourceDataLine = (SourceDataLine)AudioSystem.getLine( dataLineInfo );
        AudioSystem.getLine(dataLineInfo);
        sourceDataLine.open(getAudioFormat()) ;
        sourceDataLine.start();
    }
    private AudioFormat getAudioFormat() {
        float sampleRate = 44100.0F;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = false;        
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
    @Override
    public Receive createReceive() {
       return receiveBuilder()
               .match(byte[].class,b->{
                  // for(int x=0;x<b.length;x++){
                   sourceDataLine.write(b, 0, b.length);
                   System.out.println("playing sound: "+b);
                   sourceDataLine.drain() ;
                 //  }
                   
               }).build();
               
    }

}
