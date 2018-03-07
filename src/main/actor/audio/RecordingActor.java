package main.actor.audio;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.util.ByteString;

public class RecordingActor extends AbstractActor{
    private final static Logger log = LogManager.getLogger(RecordingActor.class);
    public volatile static boolean isClientOn;
    public volatile static boolean isServerOn;
    private  ActorRef communicationActor = null;
    private TargetDataLine targetDataLine;
    private AudioFormat audioFormat;
    
    
    private RecordingActor(ActorRef comActor) throws LineUnavailableException {
            this.communicationActor = comActor;
                
    }
      
    private AudioFormat getAudioFormat() {
        float sampleRate = 44100.0F;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = false;        
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public static Props props(ActorRef comActor) {
        return Props.create(RecordingActor.class,comActor);
    }

      @Override
    public void preStart() throws Exception {
          isClientOn = false;
          isServerOn = false;
          log.info("starting Audio Capture Actor");
          getSelf().tell("start", getSelf());
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(byte[].class, b->{
                    log.trace("received myself");
                })
                .match(String.class,s->{
                    if(s.equalsIgnoreCase("start")){
                        getAudioRunning();
                    }else{
                        log.trace(s);
                    }
                    
                })
                .build();
    }
  
    private void getAudioRunning() throws IOException, LineUnavailableException{
        log.trace("starting audio");
             
                Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
                System.out.println("Available mixers:");
                audioFormat = getAudioFormat();
                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
                Mixer mixer = AudioSystem.getMixer(mixerInfo[3]);           
                targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
                targetDataLine.open(audioFormat);
                targetDataLine.start();     
                while(true){  
                    byte tempBuffer[] = new byte[100000];
                    targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    if(isClientOn || isServerOn){
                    communicationActor.tell((ByteString.fromArray(tempBuffer)),getSelf());
                        //log.info("sending frame");
                    }
                }   
            }
    
    @Override
    public void postStop() throws Exception {
        log.info("stopping Audio Capture Actor");
    }
   
}
