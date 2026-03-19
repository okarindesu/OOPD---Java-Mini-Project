package Physics;

import Player.Player;

public class PhysicsEngine {

    public void applyGravity(Player p) {

        p.velocityY += 1;
        p.y += p.velocityY;
    }

    public void applyPlatformCollision(Player p, Platform plat) {

        int playerBottom = p.y + p.height;
        int prevBottom = playerBottom - p.velocityY;

        // Only when falling
        if (p.velocityY >= 0) {

            boolean withinX = p.x + p.width > plat.x &&
                    p.x < plat.x + plat.width;

            boolean crossedPlatform = prevBottom <= plat.y && // was above
                    playerBottom >= plat.y; // now at/below

            if (withinX && crossedPlatform) {
                p.y = plat.y - p.height;
                p.velocityY = 0;
                p.onGround = true;
            }
        }
    }
}