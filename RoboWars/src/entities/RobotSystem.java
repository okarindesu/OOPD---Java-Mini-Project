package entities;

public class RobotSystem {
    Robot robot1 ;
    Robot robot2 ;

    private float attackRange ;

    public RobotSystem(Robot robot1, Robot robot2) {
        this.robot1 = robot1 ;
        this.robot2 = robot2 ;
        this.attackRange = 5.0f ;
    }

    public void checkAttacksRobots() {
        if(robot1.isAttacking) {
            if(checkIfInAttackRange()) robot2.takeDamage();
            robot1.isAttacking = false;
        }
        if(robot2.isAttacking) {
            if(checkIfInAttackRange()) robot1.takeDamage();
            robot2.isAttacking = false ;
        }
    }

    public boolean checkIfInAttackRange() {
        float disX = Math.abs(robot1.getPosition().getVector2DX() - robot2.getPosition().getVector2DX()) ;
        float disY = Math.abs(robot1.getPosition().getVector2DY() - robot2.getPosition().getVector2DY()) ;

        return disX <= attackRange || disY <= attackRange;
    }

    public void checkRespawns() {
        if(robot1.isDead() && robot1.hasLivesRemaining()) robot1.respawn();
        if(robot2.isDead() && robot2.hasLivesRemaining()) robot2.respawn();
    }

    public void checkWinCondition() {
        if(!robot1.hasLivesRemaining() && robot1.isDead()) {
            robot1.gameOverforRobo = true ;
            IO.println("Player 2 Wins !");
        }
        else if(!robot2.hasLivesRemaining() && robot2.isDead()) {
            robot2.gameOverforRobo = true ;
            IO.println("Player 1 Wins !");
        }
    }
}
