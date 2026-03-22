package physics;

import entities.Level;
import entities.Robot;
import entities.Tile;
import utils.Vector2D;

public class CollisionHandler {
    public void handleCollisions(CollisionResolver collisionResolver , Level level , Robot robot) {
        handleLeveltoLevelCollisions(collisionResolver , level) ;
        robotLevelCollision(collisionResolver , robot , level) ;
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
}
