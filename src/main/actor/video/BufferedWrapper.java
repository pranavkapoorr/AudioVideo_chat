package main.actor.video;

import java.awt.image.BufferedImage;

final public class BufferedWrapper {
    private final BufferedImage img;
    public BufferedWrapper(BufferedImage image) {
        this.img = image;
    }
    public BufferedImage getImage(){
        return this.img;
    }
}
