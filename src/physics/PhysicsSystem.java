package physics;

import entities.Camera;
import entities.Level;
import entities.Robot;
import entities.Tile;
import utils.Vector2D;
import weapons.*;

public class PhysicsSystem {
    private float inputDT = 0.016f ;
    private float gravity = 1200.0f ;

    public void update(Robot robot1 , Robot robot2 , Level level, ProjectileSystem ps) {
        Vector2D vel1 = robot1.getVelocity();
        vel1.set(vel1.getVector2DX(), vel1.getVector2DY() + gravity * inputDT);
        robot1.getPosition().addLocal(new Vector2D(vel1.getVector2DX() * inputDT , vel1.getVector2DY() * inputDT));

        Vector2D vel2 = robot2.getVelocity();
        vel2.set(vel2.getVector2DX(), vel2.getVector2DY() + gravity * inputDT);
        robot2.getPosition().addLocal(new Vector2D(vel2.getVector2DX() * inputDT , vel2.getVector2DY() * inputDT));

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

        if(robot1.onSurface && robot1.currentPlatform != null) {
            Vector2D tileVel = robot1.currentPlatform.getTileVel() ;
            robot1.getPosition().addLocal(new Vector2D(tileVel.getVector2DX() * inputDT , tileVel.getVector2DY() * inputDT));
        }

        if(robot2.onSurface && robot2.currentPlatform != null) {
            Vector2D tileVel = robot2.currentPlatform.getTileVel() ;
            robot2.getPosition().addLocal(new Vector2D(tileVel.getVector2DX() * inputDT , tileVel.getVector2DY() * inputDT));
        }

        ps.update(inputDT);

    }


    public static void updateProjectilePosition(Projectile p, float dt) {
        Vector2D displacement = new Vector2D(
                p.getVelocity().getVector2DX() * dt,
                p.getVelocity().getVector2DY() * dt
        );
        p.getPosition().addLocal(displacement);
    }


}
