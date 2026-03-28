package engine;

import entities.*;
import utils.Vector2D;

import java.awt.event.KeyEvent;

public class PlayerController {
    private Robot robot1 ;
    private Robot robot2 ;
    private InputHandler input ;

    public PlayerController(Robot robot1 , Robot robot2 , InputHandler input) {
        this.robot1 = robot1 ;
        this.robot2 = robot2 ;
        this.input = input ;
    }

    public void control() {
        if(robot1.onSurface) robot1.getVelocity().set(0 , robot1.getVelocity().getVector2DY()) ;
        if(robot2.onSurface) robot2.getVelocity().set(0 , robot2.getVelocity().getVector2DY()) ;

        if(input.isKeyPressed(KeyEvent.VK_A)) {
            Vector2D vec = robot1.getVelocity() ;
            vec.set(new Vector2D(-robot1.defaultVel , vec.getVector2DY()));
            robot1.direction = "Left";
        }

        if(input.isKeyPressed(KeyEvent.VK_D)) {
            Vector2D vec = robot1.getVelocity() ;
            vec.set(new Vector2D(robot1.defaultVel , vec.getVector2DY()));
            robot1.direction = "Right";
        }

        if(input.isKeyPressed(KeyEvent.VK_W)) {
            robot1.jump() ;
        }

        if(input.isKeyPressed(KeyEvent.VK_Q)) {
            robot1.getWeapon().use(robot1);
        }

        if(input.isKeyPressed(KeyEvent.VK_LEFT)) {
            Vector2D vec = robot2.getVelocity() ;
            vec.set(new Vector2D(-robot2.defaultVel , vec.getVector2DY()));
            robot2.direction = "Left";
        }

        if(input.isKeyPressed(KeyEvent.VK_RIGHT)) {
            Vector2D vec = robot2.getVelocity() ;
            vec.set(new Vector2D(robot2.defaultVel , vec.getVector2DY()));
            robot2.direction = "Right";
        }

        if(input.isKeyPressed(KeyEvent.VK_UP)) {
            robot2.jump() ;
        }

        if(input.isKeyPressed(KeyEvent.VK_SPACE)) {
            robot2.getWeapon().use(robot2);
        }

    }
}
