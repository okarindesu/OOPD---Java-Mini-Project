package engine;

import entities.Robot;
import utils.Vector2D;

import java.awt.event.KeyEvent;

public class PlayerController {
    private Robot robot ;
    private InputHandler input ;

    public PlayerController(Robot robot , InputHandler input) {
        this.robot = robot ;
        this.input = input ;
    }

    public void control() {
        if(robot.onSurface) robot.getVelocity().set(0 , robot.getVelocity().getVector2DY()) ;
        if(input.isKeyPressed(KeyEvent.VK_A)) {
            Vector2D vec = robot.getVelocity() ;
            vec.set(new Vector2D(-robot.defaultVel , vec.getVector2DY()));
        }

        if(input.isKeyPressed(KeyEvent.VK_D)) {
            Vector2D vec = robot.getVelocity() ;
            vec.set(new Vector2D(robot.defaultVel , vec.getVector2DY()));
        }

        if(input.isKeyPressed(KeyEvent.VK_W)) {
            robot.jump() ;
        }
    }
}
