package entities;

public class RobotSystem {
    Robot robot1 ;
    Robot robot2 ;

    private boolean gameOver = false ;
    private String winner = "" ;

    public RobotSystem(Robot robot1, Robot robot2) {
        this.robot1 = robot1 ;
        this.robot2 = robot2 ;
    }

    public void checkAttacksRobots() {
        if(gameOver) return ;

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
        float x1 = robot1.getPosition().getVector2DX() ;
        float y1 = robot1.getPosition().getVector2DY() ;
        float w1 = robot1.getRoboWidth() ;
        float h1 = robot1.getRoboHeight() ;

        float x2 = robot2.getPosition().getVector2DX() ;
        float y2 = robot2.getPosition().getVector2DY() ;
        float w2 = robot2.getRoboWidth() ;
        float h2 = robot2.getRoboHeight() ;

        boolean overlapX = x1 < x2 + w2 && x1 + w1 > x2;
        boolean overlapY = y1 < y2 + h2 && y1 + h1 > y2;

        return overlapX && overlapY;
    }

    public void checkRespawns() {
        if(gameOver) return ;

        if(robot1.isDead()) {
            if(robot1.getLives() > 1) {
                robot1.loseLife();
                robot1.respawn();
            } else if(robot1.getLives() == 1) {
                robot1.loseLife();
            }
        }

        if(robot2.isDead()) {
            if(robot2.getLives() > 1) {
                robot2.loseLife();
                robot2.respawn();
            } else if(robot2.getLives() == 1) {
                robot2.loseLife();
            }
        }
    }

    public void checkWinCondition() {
        if(gameOver) return ;

        if(!robot1.hasLivesRemaining() && robot1.isDead()) {
            robot1.gameOverforRobo = true ;
            robot2.gameOverforRobo = false ;
            winner = "Player 2" ;
            gameOver = true ;
        }
        else if(!robot2.hasLivesRemaining() && robot2.isDead()) {
            robot2.gameOverforRobo = true ;
            robot1.gameOverforRobo = false ;
            winner = "Player 1" ;
            gameOver = true ;
        }
    }

    public boolean isGameOver() {
        return gameOver ;
    }

    public String getWinner() {
        return winner ;
    }

    public void reset() {
        gameOver = false ;
        winner = "" ;
        robot1.reset();
        robot2.reset();
    }
}
