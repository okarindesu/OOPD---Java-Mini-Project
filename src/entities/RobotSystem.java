package entities;

public class RobotSystem {
    Robot robot1 ;
    Robot robot2 ;

    private float attackRange ;
    private String winner;

    public RobotSystem(Robot robot1, Robot robot2) {
        this.robot1 = robot1 ;
        this.robot2 = robot2 ;
        this.attackRange = 150.0f ;  // Increased from 50 to 150 pixels
    }

    public void checkAttacksRobots() {
        // Don't allow attacks if game is over
        if (winner != null) return;
        
        // Don't allow attacks from robots that are game over
        if (robot1.gameOverforRobo || robot2.gameOverforRobo) return;
        
        if(robot1.isAttacking) {
            if(checkIfInAttackRange(robot1, robot2)) robot2.takeDamage(robot1.damageInflicted);
            robot1.isAttacking = false;
        }
        if(robot2.isAttacking) {
            if(checkIfInAttackRange(robot2, robot1)) robot1.takeDamage(robot2.damageInflicted);
            robot2.isAttacking = false ;
        }
    }

    public boolean checkIfInAttackRange(Robot attacker, Robot target) {
        float dx = target.getPosition().getVector2DX() - attacker.getPosition().getVector2DX();
        float dy = target.getPosition().getVector2DY() - attacker.getPosition().getVector2DY();

        float distanceSq = dx * dx + dy * dy;

        if (distanceSq > attackRange * attackRange) return false;

        String dir = attacker.direction;

        if ("right".equals(dir) && dx < 0) return false;
        if ("left".equals(dir) && dx > 0) return false;
        if ("up".equals(dir) && dy > 0) return false;
        if ("down".equals(dir) && dy < 0) return false;

        return true;
    }

    public void checkRespawns() {
        if(robot1.isDead() && robot1.hasLivesRemaining()) robot1.respawn();
        if(robot2.isDead() && robot2.hasLivesRemaining()) robot2.respawn();
    }

    public String getWinner(){
        return winner;
    }

    public void resetWinner() {
        winner = null;
    }

    public void checkWinCondition() {
        if(!robot1.hasLivesRemaining() && robot1.isDead()) {
            robot1.gameOverforRobo = true ;
            winner = "Player 2 Wins !";
        }
        else if(!robot2.hasLivesRemaining() && robot2.isDead()) {
            robot2.gameOverforRobo = true ;
            winner = "Player 1 Wins !";
        }
    }
}
