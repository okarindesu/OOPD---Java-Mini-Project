package physics;

import entities.*;
import entities.Robot;
import utils.Vector2D;

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

    public void render(Robot robot1 , Robot robot2 , Level level , Camera camera , entities.RobotSystem robotSystem) {
        BufferStrategy bs = canvas.getBufferStrategy() ;
        if (bs == null) return ;

        camera.setCameraX(robot1.getPosition().getVector2DX() - width / 2) ;
        camera.setCameraY(robot1.getPosition().getVector2DY() - height / 2) ;

        Graphics g = bs.getDrawGraphics() ;
        g.setColor(Color.BLACK) ;
        g.fillRect(0 , 0 , width , height) ;

        drawLevel(g , level , camera) ;
        drawRobot(g , robot1 , Color.RED) ;
        drawRobot(g , robot2 , Color.CYAN) ;
        drawUI(g , robot1 , robot2, robotSystem) ;

        g.setColor(Color.WHITE) ;
        g.drawString("RoboWars" , 10 , 20) ;

        g.dispose() ;
        bs.show() ;
    }

    private void drawRobot(Graphics g , Robot robot , Color color) {
        if(robot.gameOverforRobo) return ;
        g.setColor(color) ;
        Vector2D vec = robot.getPosition() ;
        float roboWidth = robot.getRoboWidth() ;
        float roboHeight = robot.getRoboHeight() ;
        g.fillRect(
                (int) vec.getVector2DX() ,
                (int) vec.getVector2DY() ,
                (int) roboWidth ,
                (int) roboHeight
        );
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

    private void drawUI(Graphics g, Robot robot1 , Robot robot2, entities.RobotSystem robotSystem) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // Title
        g.drawString("RoboWars - 2 Player", 10, 20);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("P1: WASD + Q | P2: Arrows + Space", 10, 35);

        // Player 1 Stats
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P1", 10, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", robot1.getHealth()) + "/" + (int)Robot.getMaxHealth(), 10, 75);
        g.drawString("Lives: " + robot1.getLives(), 10, 90);

        // Health bar for P1
        drawHealthBar(g, 10, 100, 150, 15, robot1.getHealth(), Robot.getMaxHealth(), Color.RED);

        // Player 2 Stats
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P2", width - 50, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", robot2.getHealth()) + "/" + (int)Robot.getMaxHealth(), width - 180, 75);
        g.drawString("Lives: " + robot2.getLives(), width - 180, 90);

        // Health bar for P2
        drawHealthBar(g, width - 160, 100, 150, 15, robot2.getHealth(), Robot.getMaxHealth(), Color.CYAN);

        if (robotSystem.isGameOver()) {
            g.setColor(new Color(0, 0, 0, 170));
            g.fillRect(0, 0, width, height);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            String result = robotSystem.getWinner() + " Wins!";
            int strW = g.getFontMetrics().stringWidth(result);
            g.drawString(result, (width - strW) / 2, height / 2 - 20);

            g.setFont(new Font("Arial", Font.BOLD, 20));
            String replay = "Press R to Replay";
            int replayW = g.getFontMetrics().stringWidth(replay);
            g.drawString(replay, (width - replayW) / 2, height / 2 + 20);
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
