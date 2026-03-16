import java.awt.*;

public class Player {

    int x;
    int y;

    int width = 40;
    int height = 40;

    int velocityY = 0;

    boolean onGround = false;

    int lives = 3;

    Color color;

    public Player(int x,int y,Color color){
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void moveLeft(){
        x -= 5;
    }

    public void moveRight(){
        x += 5;
    }

    public void jump(){

        if(onGround){
            velocityY = -15;
            onGround = false;
        }
    }

    public void knockback(int power){
        x += power;
    }

    public boolean attack(Player other){

        Rectangle hitbox = new Rectangle(x-10,y,width+20,height);
        Rectangle enemy = new Rectangle(other.x,other.y,other.width,other.height);

        return hitbox.intersects(enemy);
    }

    public void update(int ground){

        velocityY += 1;
        y += velocityY;

        if(y >= ground - height){
            y = ground - height;
            velocityY = 0;
            onGround = true;
        }
    }

    public void respawn(){

        x = 300;
        y = 200;
        velocityY = 0;
    }

    public void draw(Graphics g){

        g.setColor(color);
        g.fillRect(x,y,width,height);
    }
}