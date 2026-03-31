package physics;

import Weapons.Projectile;
import entities.Level;
import entities.Robot;
import entities.Tile;
import utils.Vector2D;

import java.util.List;

public class CollisionHandler {
    public void handleCollisions(CollisionResolver collisionResolver , Level level , Robot robot1 , Robot robot2 , int screenWidth , int screenHeight) {
        handleLeveltoLevelCollisions(collisionResolver , level) ;
        robotLevelCollision(collisionResolver , robot1 , level) ;
        robotLevelCollision(collisionResolver , robot2 , level) ;
        robottoRobotCollisions(collisionResolver , robot1 , robot2) ;
        robotBoundaryCollisions(collisionResolver , robot1 , robot2 , screenWidth , screenHeight) ;
        handleProjectileCollisions(robot1 , robot2 , level , screenWidth , screenHeight) ;
        handleProjectileCollisions(robot2 , robot1 , level , screenWidth , screenHeight) ;
    }

    public void handleProjectileCollisions(Robot shooterRobo , Robot shotRobo , Level level , int screenWidth , int screenHeight) {
        List<Projectile> p = shooterRobo.getHandGun().getProjectileSystem().getProjectiles();
        int levelSize = level.getLevelSize() ;
        for(Projectile proj : p) {
            if(proj.getHasHitRobot() || proj.getHasReachedBoundary() || proj.getHasHitTile()) continue;
            for(int i = 0 ; i < levelSize ; i++) {
                Tile tile = level.findTile(i) ;
                projectileTileCollision(proj , tile) ;
            }
            if(!proj.getHasHitTile()) projectileRobotCollision(proj , shotRobo) ;
            if(!proj.getHasHitTile() && !proj.getHasHitRobot()) projectileBoundaryCollision(proj , screenWidth , screenHeight) ;
        }
    }

    public void robotLevelCollision(CollisionResolver collisionResolver ,Robot robot , Level level) {
        int levelSize = level.getLevelSize() ;
        for(int i = 0 ; i < levelSize ; i++) {
            Tile tile = level.findTile(i) ;
            robotTileCollision(collisionResolver , robot , tile) ;
        }
    }

    private void handleLeveltoLevelCollisions(CollisionResolver collisionResolver , Level level) {
        int levelSize = level.getLevelSize() ;
        for(int i = 0 ; i < levelSize ; i++) {
            Tile tilei = level.findTile(i) ;
            for(int j = i + 1 ; j < levelSize ; j++) {
                Tile tilej = level.findTile(j) ;
                tileCollision(collisionResolver , tilei , tilej) ;
            }
        }
    }

    private void tileCollision(CollisionResolver collisionResolver , Tile tile1 , Tile tile2) {
        float x1 = tile1.getPosition().getVector2DX() ;
        float y1 = tile1.getPosition().getVector2DY() ;
        float w1 = tile1.getTileWidth() ;
        float h1 = tile1.getTileHeight() ;

        float x2 = tile2.getPosition().getVector2DX() ;
        float y2 = tile2.getPosition().getVector2DY() ;
        float w2 = tile2.getTileWidth() ;
        float h2 = tile1.getTileHeight() ;

        boolean overlapX = x1 < x2 + w2 && x1 + w1 > x2;
        boolean overlapY = y1 < y2 + h2 && y1 + h1 > y2;

        if(overlapX && overlapY) {
            collisionResolver.resolveTileCollisions(tile1 , tile2);

            Vector2D tileVel1 = tile1.getTileVel();
            tileVel1.set(-tileVel1.getVector2DX(), -tileVel1.getVector2DY());

            Vector2D tileVel2 = tile2.getTileVel();
            tileVel2.set(-tileVel2.getVector2DX(), -tileVel2.getVector2DY());
        }
    }

    private void robotTileCollision(CollisionResolver collisionResolver , Robot robot , Tile tile) {
        float x1 = tile.getPosition().getVector2DX() ;
        float y1 = tile.getPosition().getVector2DY() ;
        float w1 = tile.getTileWidth() ;
        float h1 = tile.getTileHeight() ;

        float x2 = robot.getPosition().getVector2DX() ;
        float y2 = robot.getPosition().getVector2DY() ;
        float w2 = robot.getRoboWidth() ;
        float h2 = robot.getRoboHeight() ;

        boolean overlapX = x1 < x2 + w2 && x1 + w1 > x2;
        boolean overlapY = y1 < y2 + h2 && y1 + h1 > y2;

        if(overlapX && overlapY) collisionResolver.resolveRobotTileCollisions(robot , tile) ;
    }

    private void robottoRobotCollisions(CollisionResolver collisionResolver , Robot robot1 , Robot robot2) {
        float x1 = robot1.getPosition().getVector2DX() ;
        float y1 = robot1.getVelocity().getVector2DY() ;
        float w1 = robot1.getRoboWidth() ;
        float h1 = robot1.getRoboHeight() ;

        float x2 = robot2.getPosition().getVector2DX() ;
        float y2 = robot2.getVelocity().getVector2DY() ;
        float w2 = robot2.getRoboWidth() ;
        float h2 = robot2.getRoboHeight() ;

        boolean overlapX = x1 < x2 + w2 && x1 + w1 > x2;
        boolean overlapY = y1 < y2 + h2 && y1 + h1 > y2;

        if(overlapX && overlapY) collisionResolver.resolveRobotRobotCollisions(robot1 , robot2) ;
    }

    private void robotBoundaryCollisions(CollisionResolver collisionResolver , Robot robot1 , Robot robot2 , int screenWidth , int screenHeight) {
        collisionResolver.resolveRobotBoundaryCollisions(robot1 , screenWidth , screenHeight) ;
        collisionResolver.resolveRobotBoundaryCollisions(robot2 , screenWidth , screenHeight) ;
    }

    private void projectileTileCollision(Projectile proj, Tile tile) {

        float px = proj.getPosition().getVector2DX();
        float py = proj.getPosition().getVector2DY();

        float tx = tile.getPosition().getVector2DX();
        float ty = tile.getPosition().getVector2DY();
        float tw = tile.getTileWidth();
        float th = tile.getTileHeight();

        boolean insideX = px >= tx && px <= tx + tw;
        boolean insideY = py >= ty && py <= ty + th;

        if (insideX && insideY) proj.setHasHitTile(true) ;
    }

    private void projectileRobotCollision(Projectile proj, Robot robot) {

        float px = proj.getPosition().getVector2DX();
        float py = proj.getPosition().getVector2DY();

        float rx = robot.getPosition().getVector2DX();
        float ry = robot.getPosition().getVector2DY();
        float rw = robot.getRoboWidth();
        float rh = robot.getRoboHeight();

        float padding = 5.0f;

        boolean insideX = px >= (rx - padding) && px <= (rx + rw + padding);
        boolean insideY = py >= (ry - padding) && py <= (ry + rh + padding);

        if (insideX && insideY) proj.setHasHitRobot(true);
    }

    private void projectileBoundaryCollision(Projectile proj , int screenWidth , int screenHeight) {
        float px = proj.getPosition().getVector2DX();
        float py = proj.getPosition().getVector2DY();

        if(px <= 0 || px >= screenWidth) proj.setHasReachedBoundary(true) ;
        if(py <= 0 || py >= screenHeight) proj.setHasReachedBoundary(true) ;
    }
}
