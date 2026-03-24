package physics;

import entities.Level;
import entities.Robot;
import entities.Tile;
import utils.Vector2D;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.List;

public class GameRenderer {
    private Canvas canvas ;
    private int width ;
    private int height ;

    public GameRenderer(Canvas canvas , int width , int height) {
        this.canvas = canvas ;
        this.width = width ;
        this.height = height ;
    }

    public void render(List<Robot> robots , Level level, String gameStatus) {
        BufferStrategy bs = canvas.getBufferStrategy() ;
        if (bs == null) return ;

        Graphics g = bs.getDrawGraphics() ;
        g.setColor(Color.BLACK) ;
        g.fillRect(0 , 0 , width , height) ;

        drawLevel(g , level) ;
        drawRobots(g , robots) ;
        drawUI(g, robots, gameStatus);

        g.dispose() ;
        bs.show() ;
    }

    private void drawRobots(Graphics g, List<Robot> robots) {
        for (int i = 0; i < robots.size(); i++) {
            Robot robot = robots.get(i);
            if (i == 0) {
                g.setColor(Color.RED) ;
            } else {
                g.setColor(Color.CYAN) ;
            }
            Vector2D vec = robot.getPosition() ;
            float roboWidth = robot.getRoboWidth() ;
            float roboHeight = robot.getRoboHeight() ;
            g.fillRect(
                    (int) vec.getVector2DX() ,
                    (int) vec.getVector2DY() ,
                    (int) roboWidth ,
                    (int) roboHeight
            );
            
            // Draw attack state
            if (robot.isAttacking) {
                g.setColor(Color.YELLOW);
                g.drawRect(
                        (int) vec.getVector2DX() - 5,
                        (int) vec.getVector2DY() - 5,
                        (int) roboWidth + 10,
                        (int) roboHeight + 10
                );
            }
        }
    }
    
    private void drawUI(Graphics g, List<Robot> robots, String gameStatus) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Title
        g.drawString("RoboWars - 2 Player", 10, 20);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("P1: WASD + Q | P2: Arrows + Space", 10, 35);
        
        // Player 1 Stats
        Robot p1 = robots.get(0);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P1", 10, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", p1.getHealth()) + "/" + (int)Robot.getMaxHealth(), 10, 75);
        g.drawString("Lives: " + p1.getLives(), 10, 90);
        
        // Health bar for P1
        drawHealthBar(g, 10, 100, 150, 15, p1.getHealth(), Robot.getMaxHealth(), Color.RED);
        
        // Player 2 Stats
        Robot p2 = robots.get(1);
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P2", width - 50, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", p2.getHealth()) + "/" + (int)Robot.getMaxHealth(), width - 180, 75);
        g.drawString("Lives: " + p2.getLives(), width - 180, 90);
        
        // Health bar for P2
        drawHealthBar(g, width - 160, 100, 150, 15, p2.getHealth(), Robot.getMaxHealth(), Color.CYAN);
        
        // Game Status
        if (!gameStatus.equals("Playing")) {
            g.setColor(new Color(255, 255, 0, 200));
            g.setFont(new Font("Arial", Font.BOLD, 32));
            FontMetrics fm = g.getFontMetrics();
            int x = (width - fm.stringWidth(gameStatus)) / 2;
            int y = height / 2;
            g.drawString(gameStatus, x, y);
        }
    }
    
    private void drawHealthBar(Graphics g, int x, int y, int width, int height, float currentHealth, float maxHealth, Color color) {
        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);
        
        float percentage = currentHealth / maxHealth;
        int filledWidth = (int) (width * percentage);
        
        g.setColor(color);
        g.fillRect(x, y, filledWidth, height);
        
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }

    private void drawLevel(Graphics g , Level level) {
        g.setColor(Color.BLUE) ;
        int levelSize = level.getLevelSize() ;
        for(int i = 0 ; i < levelSize ; i++) {
            Tile tile = level.findTile(i) ;
            if(tile != null) {
                float tileX = tile.getPosition().getVector2DX() ;
                float tileY = tile.getPosition().getVector2DY() ;
                float tileWidth = tile.getTileWidth() ;
                float tileHeight = tile.getTileHeight() ;

                g.fillRect(
                        (int) tileX ,
                        (int) tileY ,
                        (int) tileWidth ,
                        (int) tileHeight
                );
            }
        }
    }
}
