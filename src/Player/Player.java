package Player;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.awt.*;

public class Player {

    public String direction = "RIGHT";

    BufferedImage spriteLeft;
    BufferedImage spriteRight;
    BufferedImage currentSprite;

    public int x, y;
    public int width = 40, height = 40;

    public int velocityY = 0;
    public boolean onGround = false;

    public int lives = 3;

    Color color;

    public Player(int x, int y, Color color, String leftPath, String rightPath) {
        this.x = x;
        this.y = y;
        this.color = color;

        try {
            spriteLeft = ImageIO.read(new java.io.File("src/" + leftPath));
            spriteRight = ImageIO.read(new java.io.File("src/" + rightPath));

            currentSprite = spriteRight; // default direction
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveRight() {
        x += 5;
        direction = "RIGHT";
        currentSprite = spriteRight;
    }

    public void moveLeft() {
        x -= 5;
        direction = "LEFT";
        currentSprite = spriteLeft;
    }

    public void jump() {
        if (onGround) {
            velocityY = -15;
            onGround = false;
        }
    }

    public void knockback(int power) {
        x += power;
    }

    public void respawn() {
        x = 300;
        y = 200;
        velocityY = 0;
    }

    public void death() {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
        velocityY = 0;
        lives = 0;
    }

    public void draw(Graphics g) {

        if (currentSprite != null) {
            g.drawImage(currentSprite, x, y, width, height, null);
        } else {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
    }
}