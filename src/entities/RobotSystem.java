package entities;

import Weapons.MeleeWeapon;
import Weapons.Projectile;

import java.util.Iterator;
import java.util.List;

public class RobotSystem {
    Robot robot1 ;
    Robot robot2 ;

    private float attackRange ;

    public RobotSystem(Robot robot1, Robot robot2) {
        this.robot1 = robot1 ;
        this.robot2 = robot2 ;
        this.attackRange = 100.0f ;
    }

    public void checkAttacksRobots() {
        if(robot1.isAttacking) {
            MeleeWeapon meleeWeapon = robot1.getMeleeWeapon() ;
            meleeWeapon.use(robot1) ;
            if(checkIfInAttackRange()) {
                if(robot1.getPosition().getVector2DX() < robot2.getPosition().getVector2DX() && robot1.getDirection() == 1 && robot2.getDirection() == -1) robot2.takeDamage(meleeWeapon.getMeleeDamage());
                else if(robot1.getPosition().getVector2DX() > robot2.getPosition().getVector2DX() && robot1.getDirection() == -1 && robot2.getDirection() == 1) robot2.takeDamage(meleeWeapon.getMeleeDamage());
            }
            robot1.isAttacking = false;
        }
        if(robot2.isAttacking) {
            MeleeWeapon meleeWeapon = robot2.getMeleeWeapon() ;
            meleeWeapon.use(robot2) ;
            if(checkIfInAttackRange()) {
                if(robot1.getPosition().getVector2DX() < robot2.getPosition().getVector2DX() && robot1.getDirection() == 1 && robot2.getDirection() == -1) robot1.takeDamage(meleeWeapon.getMeleeDamage());
                else if(robot1.getPosition().getVector2DX() > robot2.getPosition().getVector2DX() && robot1.getDirection() == -1 && robot2.getDirection() == 1) robot1.takeDamage(meleeWeapon.getMeleeDamage());
            }
            robot2.isAttacking = false ;
        }
    }

    public void robotsShooting() {
        if(robot1.isShooting) {
            robot1.shoot();
            robot1.isShooting = false ;
        }
        if(robot2.isShooting) {
            robot2.shoot();
            robot2.isShooting = false ;
        }
    }

    public void checkShootingRobots() {
        List<Projectile> p1 = robot1.getHandGun().getProjectileSystem().getProjectiles() ;
        List<Projectile> p2 = robot2.getHandGun().getProjectileSystem().getProjectiles() ;

        Iterator<Projectile> it = p1.iterator();
        while(it.hasNext()) {
            Projectile p = it.next();

            if(p.getHasHitRobot()) {
                robot2.takeDamage(robot1.getHandGun().getBulletDamage());
                it.remove();
            }
            else if(p.getHasHitTile() || p.getHasReachedBoundary()) {
                it.remove();
            }
        }

        it = p2.iterator();
        while(it.hasNext()) {
            Projectile p = it.next();

            if(p.getHasHitRobot()) {
                robot1.takeDamage(robot2.getHandGun().getBulletDamage());
                it.remove();
            }
            else if(p.getHasHitTile() || p.getHasReachedBoundary()) {
                it.remove();
            }
        }
    }

    public boolean checkIfInAttackRange() {
        float disX = Math.abs(robot1.getPosition().getVector2DX() - robot2.getPosition().getVector2DX()) ;
        float disY = Math.abs(robot1.getPosition().getVector2DY() - robot2.getPosition().getVector2DY()) ;

        return (disX * disX + disY * disY) <= (attackRange * attackRange) ;
    }

    public void checkRespawns() {
        if(robot1.isDead() && robot1.hasLivesRemaining()) robot1.respawn();
        if(robot2.isDead() && robot2.hasLivesRemaining()) robot2.respawn();
    }

    public void checkWinCondition() {
        if(!robot1.hasLivesRemaining() && robot1.isDead()) {
            robot1.gameOverforRobo = true ;
            robot1.death() ;
            IO.println("Player 2 Wins !");
        }
        else if(!robot2.hasLivesRemaining() && robot2.isDead()) {
            robot2.gameOverforRobo = true ;
            robot2.death() ;
            IO.println("Player 1 Wins !");
        }
    }
}
