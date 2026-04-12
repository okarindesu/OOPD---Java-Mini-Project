package entities;

import utils.Vector2D;

import java.awt.image.BufferedImage;

public class ParallaxObject {
    private BufferedImage image ;
    private Vector2D position ;
    private float depth ;

    public ParallaxObject(BufferedImage image , Vector2D position , float depth) {
        this.image = image;
        this.position = position;
        this.depth = depth;
    }

    public BufferedImage getImage() { return image; }
    public Vector2D getPosition() { return position; }
    public float getDepth() { return depth; }

}
