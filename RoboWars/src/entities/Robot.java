package entities;

import utils.Vector2D;

public class Robot {
    private float health ;
    private Vector2D position ;
    private Vector2D velocity ;

    private float roboWidth ;
    private float roboHeight ;

    public float jumpForce = 600.0f ;
    public float defaultVel = 200.0f ;
    public boolean onSurface = false ;
    public Tile currentPlatform = null ;

    public Robot(float x , float y) {
        this.position = new Vector2D(x , y) ;
        this.velocity = new Vector2D() ;

        this.roboWidth = 20 ;
        this.roboHeight = 20 ;
    }

    public Robot(Vector2D vec) {
        this.position = vec ;
        this.velocity = new Vector2D() ;

        this.roboWidth = 20 ;
        this.roboHeight = 20 ;
    }

    public void jump() {
        if(onSurface) {
            this.velocity.subLocal(new Vector2D(0,jumpForce)) ;
            onSurface = false ;
        }
    }

    public void shoot() {

    }

    public void takeDamage() {

    }

    public Vector2D getPosition() { return this.position ; }
    public void setPosition(float x , float y) { position.set(x , y) ; }
    public void setPosition(Vector2D vec) { position.set(vec) ; }

    public Vector2D getVelocity() { return this.velocity ; }
    public void setVelocity(float xvel , float yvel) { velocity.set(xvel , yvel) ; }
    public void setVelocity(Vector2D vec) { velocity.set(vec) ; }

    public float getRoboWidth() { return this.roboWidth ; }
    public void setRoboWidth(float roboWidth) { this.roboWidth = roboWidth ; }
    public float getRoboHeight() { return this.roboHeight ; }
    public void setRoboHeight(float roboHeight) { this.roboHeight = roboHeight ; }
}
