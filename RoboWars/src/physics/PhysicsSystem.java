package physics;

import entities.Level;
import entities.Robot;
import entities.Tile;
import utils.Vector2D;

public class PhysicsSystem {
    private float inputDT = 0.016f ;
    private float gravity = 1200.0f ;

    public void update(Robot robot , Level level) {
        Vector2D vel = robot.getVelocity();
        vel.set(vel.getVector2DX(), vel.getVector2DY() + gravity * inputDT);
        robot.getPosition().addLocal(new Vector2D(vel.getVector2DX() * inputDT , vel.getVector2DY() * inputDT));

        int levelSize = level.getLevelSize() ;
        for(int i = 0 ; i < levelSize ; i++) {
            Tile tile = level.findTile(i) ;
            if(tile != null) {
                Vector2D tilePos = tile.getPosition() ;
                Vector2D tileVel = tile.getTileVel() ;
                Vector2D tilePosNew = new Vector2D(tileVel.getVector2DX() * inputDT , tileVel.getVector2DY() * inputDT) ;

                tilePos.addLocal(tilePosNew) ;
                tile.changeVelocity() ;
            }
        }

        if(robot.onSurface && robot.currentPlatform != null) {
            Vector2D tileVel = robot.currentPlatform.getTileVel() ;
            robot.getPosition().addLocal(new Vector2D(tileVel.getVector2DX() * inputDT , tileVel.getVector2DY() * inputDT));
        }
    }
}
