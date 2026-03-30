package entities;

import weapons.MeleeWeapon;
import weapons.Projectile;

import java.util.Iterator;
import java.util.List;

public class RobotSystem {
    Robot robot1 ;
    Robot robot2 ;

    private float attackRange ;
    private String winner = null;

    public RobotSystem(Robot robot1, Robot robot2) {
        this.robot1 = robot1 ;
        this.robot2 = robot2 ;
        this.attackRange = 100.0f ;
        this.winner = null;
    }

    public void checkAttacksRobots() {
        if(robot1.isAttacking) {
            MeleeWeapon meleeWeapon = robot1.getMeleeWeapon() ;
            meleeWeapon.use(robot1) ;
            if(checkIfInAttackRange()) {
                float damage = robot1.getMeleeDamage();
                if(robot1.getPosition().getVector2DX() < robot2.getPosition().getVector2DX() && robot1.getDirection() == 1 && robot2.getDirection() == -1) robot2.takeDamage(damage);
                else if(robot1.getPosition().getVector2DX() > robot2.getPosition().getVector2DX() && robot1.getDirection() == -1 && robot2.getDirection() == 1) robot2.takeDamage(damage);
            }
            robot1.isAttacking = false;
        }
        if(robot2.isAttacking) {
            MeleeWeapon meleeWeapon = robot2.getMeleeWeapon() ;
            meleeWeapon.use(robot2) ;
            if(checkIfInAttackRange()) {
                float damage = robot2.getMeleeDamage();
                if(robot1.getPosition().getVector2DX() < robot2.getPosition().getVector2DX() && robot1.getDirection() == 1 && robot2.getDirection() == -1) robot1.takeDamage(damage);
                else if(robot1.getPosition().getVector2DX() > robot2.getPosition().getVector2DX() && robot1.getDirection() == -1 && robot2.getDirection() == 1) robot1.takeDamage(damage);
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
        List<Projectile> p1 = robot1.getProjectileSystem().getProjectiles();
        List<Projectile> p2 = robot2.getProjectileSystem().getProjectiles();

        Iterator<Projectile> it = p1.iterator();
        while(it.hasNext()) {
            Projectile p = it.next();

            if(!p.isActive()) {
                it.remove();
                continue;
            }

            if(p.getOwner() != null && p.getOwner() != robot1 && robot2.isDead() == false) {
                // collision handled in CollisionHandler
            }
        }

        it = p2.iterator();
        while(it.hasNext()) {
            Projectile p = it.next();

            if(!p.isActive()) {
                it.remove();
                continue;
            }

            if(p.getOwner() != null && p.getOwner() != robot2 && robot1.isDead() == false) {
                // collision handled in CollisionHandler
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
        if (winner != null) return; // already decided

        if(!robot1.hasLivesRemaining() && robot1.isDead()) {
            robot1.gameOverforRobo = true ;
            robot1.death() ;
            winner = "Player 2 Wins!";
        }
        else if(!robot2.hasLivesRemaining() && robot2.isDead()) {
            robot2.gameOverforRobo = true ;
            robot2.death() ;
            winner = "Player 1 Wins!";
        }
    }

    public String getWinner() {
        return winner;
    }

    public void reset() {
        winner = null;
        robot1.gameOverforRobo = false;
        robot2.gameOverforRobo = false;
    }
}
