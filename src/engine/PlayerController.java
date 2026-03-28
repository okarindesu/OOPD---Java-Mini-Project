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
        // ========== PLAYER 1 CONTROLS (WASD + Q) ==========
        // WASD for movement, W for jump, Q for attack
        
        if (!robot1.gameOverforRobo) {
            if (input.isKeyPressed(KeyEvent.VK_A)) {
                robot1.moveLeft();  // Move left + animation
            } else if (input.isKeyPressed(KeyEvent.VK_D)) {
                robot1.moveRight(); // Move right + animation
            } else {
                // Only call idle if not currently in an attack animation
                if (!robot1.getAnimationManager().isAttackAnimating()) {
                    robot1.idle();      // Idle animation
                }
            }

            if (input.isKeyPressed(KeyEvent.VK_W)) {
                robot1.jump();      // Jump + animation
            }

            if (input.isKeyPressed(KeyEvent.VK_Q)) {
                robot1.attack();    // Attack + weapon fire + animation
            } else {
                // If Q is not pressed and attack animation finished, go to idle
                if (robot1.getAnimationManager().isAttackFinished()) {
                    robot1.idle();
                }
            }
        }

        // ========== PLAYER 2 CONTROLS (Arrow Keys + Space) ==========
        // Arrow keys for movement, Up for jump, Space for attack
        
        if (!robot2.gameOverforRobo) {
            if (input.isKeyPressed(KeyEvent.VK_LEFT)) {
                robot2.moveLeft();  // Move left + animation
            } else if (input.isKeyPressed(KeyEvent.VK_RIGHT)) {
                robot2.moveRight(); // Move right + animation
            } else {
                // Only call idle if not currently in an attack animation
                if (!robot2.getAnimationManager().isAttackAnimating()) {
                    robot2.idle();      // Idle animation
                }
            }

            if (input.isKeyPressed(KeyEvent.VK_UP)) {
                robot2.jump();      // Jump + animation
            }

            if (input.isKeyPressed(KeyEvent.VK_SPACE)) {
                robot2.attack();    // Attack + weapon fire + animation
            } else {
                // If SPACE is not pressed and attack animation finished, go to idle
                if (robot2.getAnimationManager().isAttackFinished()) {
                    robot2.idle();
                }
            }
        }
    }
}
