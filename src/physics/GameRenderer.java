package physics;

import entities.*;
import entities.Robot;
import utils.Vector2D;
import weapons.Projectile;
import weapons.ProjectileSystem;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class GameRenderer {
    private Canvas canvas ;
    private int width ;
    private int height ;

    public GameRenderer(Canvas canvas , int width , int height) {
        this.canvas = canvas ;
        this.width = width ;
        this.height = height ;
    }

    public void render(Robot robot1 , Robot robot2 , Level level , Camera camera, String winner, ProjectileSystem ps) {
        BufferStrategy bs = canvas.getBufferStrategy() ;
        if (bs == null) return ;

        camera.setCameraX(robot1.getPosition().getVector2DX() - width / 2) ;
        camera.setCameraY(robot1.getPosition().getVector2DY() - height / 2) ;

        Graphics g = bs.getDrawGraphics() ;
        g.setColor(Color.BLACK) ;
        g.fillRect(0 , 0 , width , height) ;

        drawLevel(g , level , camera) ;
        
        // Render robots using their draw() method (handles sprites + animations)
        robot1.draw(g);
        robot2.draw(g);
        
        drawUI(g , robot1 , robot2) ;

        g.setColor(Color.WHITE) ;
        g.drawString("RoboWars" , 10 , 20) ;

        if (winner != null) {
            // Create pixelated font effect
            Font pixelFont = new Font("Monospaced", Font.BOLD, 48);
            g.setFont(pixelFont);
            g.setColor(Color.YELLOW);

            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(winner);

            int x = (width - textWidth) / 2;
            int y = height / 2 - 50;

            // Draw winner text with shadow for better visibility
            g.setColor(Color.BLACK);
            g.drawString(winner, x + 2, y + 2);
            g.setColor(Color.YELLOW);
            g.drawString(winner, x, y);

            // Draw instructions in cute boxes
            g.setFont(new Font("Monospaced", Font.BOLD, 20));
            FontMetrics fm2 = g.getFontMetrics();
            
            String replayText = "Press R to Replay";
            String exitText = "Press E to Exit";
            
            int replayWidth = fm2.stringWidth(replayText);
            int exitWidth = fm2.stringWidth(exitText);
            
            int boxHeight = 40;
            int boxPadding = 20;
            
            // Replay box
            int replayBoxWidth = replayWidth + boxPadding * 2;
            int replayX = (width - replayBoxWidth) / 2;
            int replayY = y + 40;
            
            // Draw replay box with rounded corners and gradient
            g.setColor(new Color(100, 200, 100)); // Light green
            g.fillRoundRect(replayX, replayY, replayBoxWidth, boxHeight, 15, 15);
            g.setColor(new Color(50, 150, 50)); // Darker green border
            g.drawRoundRect(replayX, replayY, replayBoxWidth, boxHeight, 15, 15);
            
            // Replay text
            g.setColor(Color.WHITE);
            int replayTextX = replayX + (replayBoxWidth - replayWidth) / 2;
            int replayTextY = replayY + (boxHeight + fm2.getAscent() - fm2.getDescent()) / 2;
            g.drawString(replayText, replayTextX, replayTextY);
            
            // Exit box
            int exitBoxWidth = exitWidth + boxPadding * 2;
            int exitX = (width - exitBoxWidth) / 2;
            int exitY = y + 90;
            
            // Draw exit box with rounded corners and gradient
            g.setColor(new Color(200, 100, 100)); // Light red
            g.fillRoundRect(exitX, exitY, exitBoxWidth, boxHeight, 15, 15);
            g.setColor(new Color(150, 50, 50)); // Darker red border
            g.drawRoundRect(exitX, exitY, exitBoxWidth, boxHeight, 15, 15);
            
            // Exit text
            g.setColor(Color.WHITE);
            int exitTextX = exitX + (exitBoxWidth - exitWidth) / 2;
            int exitTextY = exitY + (boxHeight + fm2.getAscent() - fm2.getDescent()) / 2;
            g.drawString(exitText, exitTextX, exitTextY);
        }

        for (Projectile p : ps.getProjectiles()) {
            if (!p.isActive()) continue;

            int x = (int)p.getPosition().getVector2DX();
            int y = (int)p.getPosition().getVector2DY();

            g.setColor(p.getColor());
            g.fillOval(x, y, 12, 12);
        }

        g.dispose() ;
        bs.show() ;
    }

    private void drawLevel(Graphics g , Level level , Camera camera) {
        drawBackground(g , level , camera) ;

        g.setColor(Color.BLUE) ;
        int levelSize = level.getLevelSize() ;
        for(int i = 0 ; i < levelSize ; i++) {
            Tile tile = level.findTile(i) ;
            if(tile != null) drawTile(g , tile) ;
        }
    }

    private void drawUI(Graphics g, Robot robot1 , Robot robot2) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // Title
        g.drawString("RoboWars - 2 Player", 10, 20);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("P1: WASD + Q | P2: Arrows + Space", 10, 35);

        // Player 1 Stats (BLUE)
        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P1", 10, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", robot1.getHealth()) + "/" + (int)Robot.getMaxHealth(), 10, 75);
        g.drawString("Lives: " + robot1.getLives(), 10, 90);

        // Health bar for P1 (BLUE)
        drawHealthBar(g, 10, 100, 150, 15, robot1.getHealth(), Robot.getMaxHealth(), Color.BLUE);

        // Player 2 Stats (GREEN)
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P2", width - 50, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", robot2.getHealth()) + "/" + (int)Robot.getMaxHealth(), width - 180, 75);
        g.drawString("Lives: " + robot2.getLives(), width - 180, 90);

        // Health bar for P2 (GREEN)
        drawHealthBar(g, width - 160, 100, 150, 15, robot2.getHealth(), Robot.getMaxHealth(), Color.GREEN);
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

    private void drawBackground(Graphics g , Level level , Camera camera) {
        float scale = 5.0f ;
        for(ParallaxObject obj : level.getParallaxObjects()) {
            float depth = obj.getDepth() ;

            float drawX = obj.getPosition().getVector2DX() - camera.getCameraX() * depth ;
            float drawY = obj.getPosition().getVector2DY() - camera.getCameraY() * depth ;
            int w = (int)(obj.getImage().getWidth() * scale);
            int h = (int)(obj.getImage().getHeight() * scale);

            g.drawImage(
                   obj.getImage(),
                    (int) drawX,
                    (int) drawY,
                    w,
                    h,
                    null
            );
        }
    }

    private void drawTile(Graphics g , Tile tile) {
        BufferedImage tex = tile.getTexture() ;

        int tileX = (int) tile.getPosition().getVector2DX() ;
        int tileY = (int) tile.getPosition().getVector2DY() ;

        int tileW = (int) tile.getTileWidth() ;
        int tileH = (int) tile.getTileHeight() ;

        int texW = tex.getWidth() ;
        int texH = tex.getHeight() ;

        for(int x = 0 ; x < tileW ; x += texW) {
            for(int y = 0 ; y < tileH ; y += texH) {
                int drawW = Math.min(texW , tileW - x) ;
                int drawH = Math.min(texH , tileH - y) ;

                g.drawImage(
                        tex,
                        tileX + x,
                        tileY + y,
                        tileX + x + drawW,
                        tileY + y + drawH,
                        0,
                        0,
                        drawW,
                        drawH,
                        null
                );
            }
        }
    }
}
