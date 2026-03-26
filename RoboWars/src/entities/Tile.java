package entities;

import utils.Vector2D;

import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Tile {
    private BufferedImage texture ;
    private final Vector2D initPosition ;
    private Vector2D position ;
    private float tileWidth ;
    private float tileHeight ;

    private boolean isMoving ;
    private Vector2D tileVel ;
    private final Vector2D amplitude ;

    public float restitution = 0.2f ;

    public Tile(Vector2D position , float tileWidth , float tileHeight , String texturePath) {
        this.initPosition = new Vector2D(position.getVector2DX(), position.getVector2DY());
        this.position = new Vector2D(position.getVector2DX(), position.getVector2DY());
        this.tileWidth = tileWidth ;
        this.tileHeight = tileHeight ;

        this.isMoving = false ;
        this.tileVel = new Vector2D(0.0f , 0.0f) ;
        this.amplitude = new Vector2D(0,0) ;
        this.texture = TextureManager.getTexture(texturePath) ;
    }

    public Tile(Vector2D position , float tileWidth , float tileHeight , Vector2D amplitude , Vector2D tileVel , String texturePath) {
        this.initPosition = new Vector2D(position.getVector2DX(), position.getVector2DY());
        this.position = new Vector2D(position.getVector2DX(), position.getVector2DY());
        this.tileWidth = tileWidth ;
        this.tileHeight = tileHeight ;
        this.amplitude = amplitude ;

        this.tileVel = tileVel ;
        if(tileVel.getVector2DX() == 0.0f && tileVel.getVector2DY() == 0.0f) this.isMoving = false ;
        else this.isMoving = true ;

        this.texture = TextureManager.getTexture(texturePath) ;
    }

    public void changeVelocity() {
        float dx = position.getVector2DX() - initPosition.getVector2DX();
        if(tileVel.getVector2DX() > 0 && dx >= amplitude.getVector2DX()) {
            tileVel.set(-tileVel.getVector2DX(), tileVel.getVector2DY());
        }
        else if(tileVel.getVector2DX() < 0 && dx <= -amplitude.getVector2DX()) {
            tileVel.set(-tileVel.getVector2DX(), tileVel.getVector2DY());
        }

        float dy = position.getVector2DY() - initPosition.getVector2DY();
        if(tileVel.getVector2DY() > 0 && dy >= amplitude.getVector2DY()) {
            tileVel.set(tileVel.getVector2DX(), -tileVel.getVector2DY());
        }
        else if(tileVel.getVector2DY() < 0 && dy <= -amplitude.getVector2DY()) {
            tileVel.set(tileVel.getVector2DX(), -tileVel.getVector2DY());
        }
    }

    public Vector2D getPosition() { return this.position ; }
    public void setPosition(Vector2D vec) { this.position.set(vec) ; }

    public float getTileWidth() { return this.tileWidth ; }
    public void setTileWidth(float tileWidth) { this.tileWidth = tileWidth ; }

    public float getTileHeight() { return this.tileHeight ; }
    public void setTileHeight(float tileHeight) { this.tileHeight = tileHeight ; }

    public Vector2D getTileVel() { return this.tileVel ; }
    public void setTileVel(Vector2D tileVel) { this.tileVel.set(tileVel) ; }

    public boolean getIsMoving() { return this.isMoving ; }
    public void setIsMoving(boolean isMoving) { this.isMoving = isMoving ; }

    public BufferedImage getTexture() { return this.texture ; }
}
