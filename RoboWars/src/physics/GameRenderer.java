package physics;

import entities.Robot;
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

    public void render(Robot robot) {
        BufferStrategy bs = canvas.getBufferStrategy() ;
        if (bs == null) return ;

        Graphics g = bs.getDrawGraphics() ;
        g.setColor(Color.BLACK) ;
        g.fillRect(0 , 0 , width , height) ;

        drawRobot(g , robot) ;
        g.setColor(Color.WHITE) ;
        g.drawString("RoboWars" , 10 , 20) ;

        g.dispose() ;
        bs.show() ;
    }

    private void drawRobot(Graphics g , Robot robot) {
        g.setColor(Color.RED) ;
        Vector2D vec = robot.getPosition() ;
        g.fillRect(
                (int) vec.getVector2DX() ,
                (int) vec.getVector2DY() ,
                100 ,
                100
        );
    }
}
