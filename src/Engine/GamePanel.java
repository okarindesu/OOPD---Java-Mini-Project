package Engine;

import javax.swing.JPanel;
import java.awt.*;
import Player.*;
import Physics.*;
import Weapons.*;

public class GamePanel extends JPanel implements Runnable {

    Thread gameThread;

    Player p1 = new Player(200, 400, Color.RED, "player1_left.png", "player1_right.png");
    Player p2 = new Player(500, 400, Color.BLUE, "player2_left.png", "player2_right.png");

    KeyHandler keyH = new KeyHandler();
    PhysicsEngine physics = new PhysicsEngine();

    // Platforms

    Platform ground = new Platform(150, 500, 500, 20);
    Platform pform1 = new Platform(200, 400, 100, 20);
    Platform pform2 = new Platform(450, 350, 120, 20);

    public GamePanel() {

        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(keyH);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {

        while (gameThread != null) {
            update();
            repaint();

            try {
                Thread.sleep(16);
            } catch (Exception e) {
            }
        }
    }

    public void update() {

        // Player 1
        if (keyH.aPressed)
            p1.moveLeft();
        if (keyH.dPressed)
            p1.moveRight();
        if (keyH.wPressed) {
            p1.jump();
            p1.direction = "UP";
        }

        if (keyH.fPressed) {
            if (Attack.hit(p1, p2)) {
                p2.knockback(20);
                p2.lives--; // Reduce lives on hit
                if (p2.lives == 0) {
                    paintComponent(getGraphics()); // Update the display to show the win message
                    p2.death();
                } else {
                    p2.respawn(); // Respawn the player after being hit
                }
            }
        }

        // Player 2
        if (keyH.leftPressed)
            p2.moveLeft();
        if (keyH.rightPressed)
            p2.moveRight();
        if (keyH.upPressed) {
            p2.jump();
            p2.direction = "UP";
        }

        if (keyH.slashPressed) {
            if (Attack.hit(p2, p1)) {
                p1.knockback(-20);
                p1.lives--; // Reduce lives on hit
                if (p1.lives == 0) {
                    paintComponent(getGraphics()); // Update the display to show the win message
                    p1.death();
                } else {
                    p1.respawn(); // Respawn the player after being hit
                }
            }
        }

        p1.onGround = false;
        p2.onGround = false;

        physics.applyGravity(p1); // just fall
        physics.applyGravity(p2); // just fall

        physics.applyPlatformCollision(p1, ground);
        physics.applyPlatformCollision(p1, pform1);
        physics.applyPlatformCollision(p1, pform2);

        physics.applyPlatformCollision(p2, ground);
        physics.applyPlatformCollision(p2, pform1);
        physics.applyPlatformCollision(p2, pform2);

        checkFall(p1);
        checkFall(p2);
    }

    public void checkFall(Player p) {

        if (p.y > 600) { // below screen
            p.lives--;

            if (p.lives > 0) {
                p.respawn();
            } else {
                p.death();
            }
        }
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        p1.draw(g);
        p2.draw(g);

        ground.draw(g);
        pform1.draw(g);
        pform2.draw(g);

        g.drawString("P1 Lives: " + p1.lives, 20, 20);
        g.drawString("P2 Lives: " + p2.lives, 700, 20);

        if (p1.lives <= 0) {
            g.drawString("PLAYER 2 WINS", 350, 200);
        }

        if (p2.lives <= 0) {
            g.drawString("PLAYER 1 WINS", 350, 200);
        }
    }
}