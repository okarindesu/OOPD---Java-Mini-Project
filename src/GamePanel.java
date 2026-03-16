import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements Runnable {

    Thread gameThread;

    Player p1 = new Player(200,400,Color.RED);
    Player p2 = new Player(500,400,Color.BLUE);

    KeyHandler keyH = new KeyHandler();

    int ground = 450;

    public GamePanel(){

        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(keyH);
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run(){

        double drawInterval = 1000000000/60;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update(){

        // PLAYER 1 CONTROLS
        if(keyH.aPressed) p1.moveLeft();
        if(keyH.dPressed) p1.moveRight();
        if(keyH.wPressed) p1.jump();

        if(keyH.fPressed){
            if(p1.attack(p2)){
                p2.knockback(20);
            }
        }

        // PLAYER 2 CONTROLS
        if(keyH.leftPressed) p2.moveLeft();
        if(keyH.rightPressed) p2.moveRight();
        if(keyH.upPressed) p2.jump();

        if(keyH.slashPressed){
            if(p2.attack(p1)){
                p1.knockback(-20);
            }
        }

        p1.update(ground);
        p2.update(ground);

        checkFall(p1);
        checkFall(p2);
    }

    public void checkFall(Player p){

        if(p.y > 600){
            p.lives--;
            p.respawn();
        }
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0,450,800,20);

        p1.draw(g);
        p2.draw(g);

        g.drawString("P1 Lives: "+p1.lives,20,20);
        g.drawString("P2 Lives: "+p2.lives,700,20);

        if(p1.lives <= 0){
            g.drawString("PLAYER 2 WINS",350,200);
        }

        if(p2.lives <= 0){
            g.drawString("PLAYER 1 WINS",350,200);
        }
    }
}