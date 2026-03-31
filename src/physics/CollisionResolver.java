package physics;

import entities.Robot;
import entities.Tile;
import utils.Vector2D;

public class CollisionResolver {
    public void resolveTileCollisions(Tile tile1 , Tile tile2) {
        float x1 = tile1.getPosition().getVector2DX() ;
        float y1 = tile1.getPosition().getVector2DY() ;
        float w1 = tile1.getTileWidth() ;
        float h1 = tile1.getTileHeight() ;

        float x2 = tile2.getPosition().getVector2DX() ;
        float y2 = tile2.getPosition().getVector2DY() ;
        float w2 = tile2.getTileWidth() ;
        float h2 = tile2.getTileHeight() ;

        float overlapX = Math.min(x1 + w1 , x2 + w2) - Math.max(x1 , x2) ;
        float overlapY = Math.min(y1 + h1 , y2 + h2) - Math.max(y1 , y2) ;

        if(overlapX <= 0 || overlapY <= 0) return ;

        Vector2D pos1 = tile1.getPosition() ;
        Vector2D pos2 = tile2.getPosition() ;

        if(overlapX < overlapY) {
            float seperation = overlapX / 2.0f ;
            if(x1 < x2) {
                pos1.addLocal(new Vector2D(-seperation , 0));
                pos2.addLocal(new Vector2D(seperation , 0));
            }
            else {
                pos1.addLocal(new Vector2D(seperation , 0));
                pos2.addLocal(new Vector2D(-seperation , 0));
            }
        }
        else {
            float seperation = overlapY / 2.0f ;
            if(y1 < y2) {
                pos1.addLocal(new Vector2D(0 , -seperation));
                pos2.addLocal(new Vector2D(0 , seperation));
            }
            else {
                pos1.addLocal(new Vector2D(0 , seperation));
                pos2.addLocal(new Vector2D(0 , -seperation));
            }
        }
    }

    public void resolveRobotTileCollisions(Robot robot, Tile tile) {
        float tx = tile.getPosition().getVector2DX();
        float ty = tile.getPosition().getVector2DY();
        float tw = tile.getTileWidth();
        float th = tile.getTileHeight();

        float rx = robot.getPosition().getVector2DX();
        float ry = robot.getPosition().getVector2DY();
        float rw = robot.getRoboWidth();
        float rh = robot.getRoboHeight();

        float overlapX = Math.min(rx + rw, tx + tw) - Math.max(rx, tx);
        float overlapY = Math.min(ry + rh, ty + th) - Math.max(ry, ty);

        if (overlapX <= 0 || overlapY <= 0) return;

        Vector2D pos = robot.getPosition();
        Vector2D vel = robot.getVelocity();

        float epsilon = 0.5f;

        if (overlapX < overlapY) {
            if (rx < tx) pos.addLocal(new Vector2D(-overlapX - epsilon, 0));
            else pos.addLocal(new Vector2D(overlapX + epsilon, 0));
            vel.set(0, vel.getVector2DY());
        }
        else {
            if (ry < ty && vel.getVector2DY() > 0) {
                pos.addLocal(new Vector2D(0, -overlapY - epsilon));
                vel.set(vel.getVector2DX(), -vel.getVector2DY() * tile.restitution);

                robot.onSurface = true;
                robot.currentPlatform = tile;
            }
            else if (ry > ty && vel.getVector2DY() < 0) {
                pos.addLocal(new Vector2D(0, overlapY + epsilon));
                vel.set(vel.getVector2DX(), 0);
            }
        }
    }

    public void resolveRobotRobotCollisions(Robot robot1 , Robot robot2) {
        float x1 = robot1.getPosition().getVector2DX();
        float y1 = robot1.getPosition().getVector2DY();
        float w1 = robot1.getRoboWidth();
        float h1 = robot1.getRoboHeight();

        float x2 = robot2.getPosition().getVector2DX();
        float y2 = robot2.getPosition().getVector2DY();
        float w2 = robot2.getRoboWidth();
        float h2 = robot2.getRoboHeight();

        float overlapX = Math.min(x1 + w1, x2 + w2) - Math.max(x1, x2);
        float overlapY = Math.min(y1 + h1, y2 + h2) - Math.max(y1, y2);

        if (overlapX <= 0 || overlapY <= 0) return;

        Vector2D pos1 = robot1.getPosition();
        Vector2D pos2 = robot2.getPosition();
        Vector2D vel1 = robot1.getVelocity();
        Vector2D vel2 = robot2.getVelocity();

        float epsilon = 0.5f;

        if (overlapX < overlapY) {
            // Separate horizontally
            float separation = (overlapX + epsilon) / 2.0f;
            if (x1 < x2) {
                pos1.addLocal(new Vector2D(-separation, 0));
                pos2.addLocal(new Vector2D(separation, 0));
            } else {
                pos1.addLocal(new Vector2D(separation, 0));
                pos2.addLocal(new Vector2D(-separation, 0));
            }
            // Bounce horizontally
            vel1.set(-vel1.getVector2DX() * 0.5f, vel1.getVector2DY());
            vel2.set(-vel2.getVector2DX() * 0.5f, vel2.getVector2DY());
        } else {
            // Separate vertically
            float separation = (overlapY + epsilon) / 2.0f;
            if (y1 < y2) {
                pos1.addLocal(new Vector2D(0, -separation));
                pos2.addLocal(new Vector2D(0, separation));
            } else {
                pos1.addLocal(new Vector2D(0, separation));
                pos2.addLocal(new Vector2D(0, -separation));
            }
            // Bounce vertically
            vel1.set(vel1.getVector2DX(), -vel1.getVector2DY() * 0.5f);
            vel2.set(vel2.getVector2DX(), -vel2.getVector2DY() * 0.5f);
        }
    }

    public void resolveRobotBoundaryCollisions(Robot robot , int screenWidth , int screenHeight) {
        Vector2D pos = robot.getPosition();
        float x = pos.getVector2DX();
        float y = pos.getVector2DY();
        float w = robot.getRoboWidth();

        if (x < 0) {
            pos.set(0, y);
            robot.getVelocity().set(0, robot.getVelocity().getVector2DY());
        }
        if (x + w > screenWidth) {
            pos.set(screenWidth - w, y);
            robot.getVelocity().set(0, robot.getVelocity().getVector2DY());
        }

        if (y > screenHeight) {
            robot.respawn();
        }
    }
}
