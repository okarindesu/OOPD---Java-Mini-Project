package physics;

import Weapons.Projectile;
import entities.*;
import exceptions.*;
import utils.Vector2D;

import java.util.List;

public class CollisionHandler {

    public void handleCollisions(CollisionResolver collisionResolver,
                                 Level level,
                                 Robot robot1,
                                 Robot robot2,
                                 int screenWidth,
                                 int screenHeight) {

        if (collisionResolver == null)
            throw new CollisionException("CollisionResolver is null");

        if (level == null)
            throw new CollisionException("Level is null");

        if (robot1 == null || robot2 == null)
            throw new InvalidPhysicsObjectException("Robot is null");

        handleLeveltoLevelCollisions(collisionResolver, level);

        robotLevelCollision(collisionResolver, robot1, level);
        robotLevelCollision(collisionResolver, robot2, level);

        robottoRobotCollisions(collisionResolver, robot1, robot2);

        robotBoundaryCollisions(collisionResolver, robot1, robot2,
                screenWidth, screenHeight);

        handleProjectileCollisions(robot1, robot2, level, screenWidth, screenHeight);
        handleProjectileCollisions(robot2, robot1, level, screenWidth, screenHeight);
    }

    public void handleProjectileCollisions(Robot shooterRobo,
                                           Robot shotRobo,
                                           Level level,
                                           int screenWidth,
                                           int screenHeight) {

        if (shooterRobo.getHandGun() == null)
            throw new CollisionException("Shooter handgun is null");

        List<Projectile> projectiles =
                shooterRobo.getHandGun().getProjectileSystem().getProjectiles();

        if (projectiles == null)
            throw new CollisionException("Projectile list is null");

        int levelSize = level.getLevelSize();

        for (Projectile proj : projectiles) {

            if (proj == null)
                throw new CollisionException("Null projectile detected");

            if (proj.getHasHitRobot() ||
                    proj.getHasReachedBoundary() ||
                    proj.getHasHitTile()) continue;

            for (int i = 0; i < levelSize; i++) {
                Tile tile = level.findTile(i);
                if (tile != null) projectileTileCollision(proj, tile);
            }

            if (!proj.getHasHitTile())
                projectileRobotCollision(proj, shotRobo);

            if (!proj.getHasHitTile() && !proj.getHasHitRobot())
                projectileBoundaryCollision(proj, screenWidth, screenHeight);
        }
    }

    public void robotLevelCollision(CollisionResolver resolver,
                                    Robot robot,
                                    Level level) {

        if (robot == null)
            throw new InvalidPhysicsObjectException("Robot is null");

        int levelSize = level.getLevelSize();

        for (int i = 0; i < levelSize; i++) {
            Tile tile = level.findTile(i);
            if (tile != null)
                robotTileCollision(resolver, robot, tile);
        }
    }

    private void handleLeveltoLevelCollisions(CollisionResolver resolver,
                                              Level level) {

        int levelSize = level.getLevelSize();

        for (int i = 0; i < levelSize; i++) {
            Tile tile1 = level.findTile(i);

            for (int j = i + 1; j < levelSize; j++) {
                Tile tile2 = level.findTile(j);

                if (tile1 != null && tile2 != null)
                    tileCollision(resolver, tile1, tile2);
            }
        }
    }

    private void tileCollision(CollisionResolver resolver,
                               Tile tile1,
                               Tile tile2) {

        Vector2D pos1 = tile1.getPosition();
        Vector2D pos2 = tile2.getPosition();

        if (pos1 == null || pos2 == null)
            throw new CollisionException("Tile position null");

        float x1 = pos1.getVector2DX();
        float y1 = pos1.getVector2DY();
        float w1 = tile1.getTileWidth();
        float h1 = tile1.getTileHeight();

        float x2 = pos2.getVector2DX();
        float y2 = pos2.getVector2DY();
        float w2 = tile2.getTileWidth();
        float h2 = tile2.getTileHeight();

        boolean overlapX = x1 < x2 + w2 && x1 + w1 > x2;
        boolean overlapY = y1 < y2 + h2 && y1 + h1 > y2;

        if (overlapX && overlapY) {
            resolver.resolveTileCollisions(tile1, tile2);

            tile1.getTileVel().set(-tile1.getTileVel().getVector2DX(),
                    -tile1.getTileVel().getVector2DY());

            tile2.getTileVel().set(-tile2.getTileVel().getVector2DX(),
                    -tile2.getTileVel().getVector2DY());
        }
    }

    private void robotTileCollision(CollisionResolver resolver,
                                    Robot robot,
                                    Tile tile) {

        Vector2D rPos = robot.getPosition();
        Vector2D tPos = tile.getPosition();

        if (rPos == null || tPos == null)
            throw new CollisionException("Position null");

        float x1 = tPos.getVector2DX();
        float y1 = tPos.getVector2DY();
        float w1 = tile.getTileWidth();
        float h1 = tile.getTileHeight();

        float x2 = rPos.getVector2DX();
        float y2 = rPos.getVector2DY();
        float w2 = robot.getRoboWidth();
        float h2 = robot.getRoboHeight();

        boolean overlapX = x1 < x2 + w2 && x1 + w1 > x2;
        boolean overlapY = y1 < y2 + h2 && y1 + h1 > y2;

        if (overlapX && overlapY)
            resolver.resolveRobotTileCollisions(robot, tile);
    }

    private void robottoRobotCollisions(CollisionResolver resolver,
                                        Robot r1,
                                        Robot r2) {

        Vector2D p1 = r1.getPosition();
        Vector2D p2 = r2.getPosition();

        if (p1 == null || p2 == null)
            throw new CollisionException("Robot position null");

        float x1 = p1.getVector2DX();
        float y1 = p1.getVector2DY();
        float w1 = r1.getRoboWidth();
        float h1 = r1.getRoboHeight();

        float x2 = p2.getVector2DX();
        float y2 = p2.getVector2DY();
        float w2 = r2.getRoboWidth();
        float h2 = r2.getRoboHeight();

        boolean overlapX = x1 < x2 + w2 && x1 + w1 > x2;
        boolean overlapY = y1 < y2 + h2 && y1 + h1 > y2;

        if (overlapX && overlapY)
            resolver.resolveRobotRobotCollisions(r1, r2);
    }

    private void robotBoundaryCollisions(CollisionResolver resolver,
                                         Robot r1,
                                         Robot r2,
                                         int screenWidth,
                                         int screenHeight) {

        resolver.resolveRobotBoundaryCollisions(r1, screenWidth, screenHeight);
        resolver.resolveRobotBoundaryCollisions(r2, screenWidth, screenHeight);
    }

    private void projectileTileCollision(Projectile proj, Tile tile) {

        float px = proj.getPosition().getVector2DX();
        float py = proj.getPosition().getVector2DY();

        float tx = tile.getPosition().getVector2DX();
        float ty = tile.getPosition().getVector2DY();

        float tw = tile.getTileWidth();
        float th = tile.getTileHeight();

        if (px >= tx && px <= tx + tw &&
                py >= ty && py <= ty + th) {

            proj.setHasHitTile(true);
        }
    }

    private void projectileRobotCollision(Projectile proj, Robot robot) {

        float px = proj.getPosition().getVector2DX();
        float py = proj.getPosition().getVector2DY();

        float rx = robot.getPosition().getVector2DX();
        float ry = robot.getPosition().getVector2DY();

        float rw = robot.getRoboWidth();
        float rh = robot.getRoboHeight();

        float pad = 5f;

        if (px >= rx - pad && px <= rx + rw + pad &&
                py >= ry - pad && py <= ry + rh + pad) {

            proj.setHasHitRobot(true);
        }
    }

    private void projectileBoundaryCollision(Projectile proj,
                                             int screenWidth,
                                             int screenHeight) {

        float px = proj.getPosition().getVector2DX();
        float py = proj.getPosition().getVector2DY();

        if (px <= 0 || px >= screenWidth ||
                py <= 0 || py >= screenHeight) {

            proj.setHasReachedBoundary(true);
        }
    }
}