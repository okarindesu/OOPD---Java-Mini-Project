package physics;

import entities.Level;
import entities.Robot;
import entities.Tile;
import utils.Vector2D;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class GameRenderer {
    private Canvas canvas ;
    private int width ;
    private int height ;

    public GameRenderer(Canvas canvas , int width , int height) {
        this.canvas = canvas ;
        this.width = width ;
        this.height = height ;
    }

    public void render(Robot robot , Level level) {
        BufferStrategy bs = canvas.getBufferStrategy() ;
        if (bs == null) return ;

        Graphics g = bs.getDrawGraphics() ;
        g.setColor(Color.BLACK) ;
        g.fillRect(0 , 0 , width , height) ;

        drawLevel(g , level) ;
        drawRobot(g , robot) ;

        g.setColor(Color.WHITE) ;
        g.drawString("RoboWars" , 10 , 20) ;

        g.dispose() ;
        bs.show() ;
    }

    private void drawRobot(Graphics g , Robot robot) {
        g.setColor(Color.RED) ;
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
