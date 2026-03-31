package engine;

import entities.Level;
import entities.LevelInfo;
import entities.LevelLoader;
import entities.Robot;
import utils.Vector2D;

import java.awt.event.KeyEvent;
import java.util.List;

public class PlayerController {
    private Robot robot1 ;
    private Robot robot2 ;
    private InputHandler input ;

    public PlayerController(Robot robot1 , Robot robot2 , InputHandler input) {
        this.robot1 = robot1 ;
        this.robot2 = robot2 ;
        this.input = input ;
    }

    public void control(StartScreenContext startScreenContext , LevelSelectionContext levelSelectionContext) {
        GameState gameState = levelSelectionContext.getGameStateHandler().getGameState() ; ;
        switch (gameState) {
            case START_SCREEN_STATE:
                controlStartScreenInput(startScreenContext) ;
                break ;
            case LEVEL_SELECTION_STATE:
                controlLevelSelectionInput(levelSelectionContext);
                break ;
            case GAME_PLAYING_STATE:
                controlGameInput() ;
                break ;
        }
    }

    private void controlStartScreenInput(StartScreenContext startScreenContext) {
        if (input.isKeyPressed(KeyEvent.VK_UP)) startScreenContext.moveUp();
        if (input.isKeyPressed(KeyEvent.VK_DOWN)) startScreenContext.moveDown();

        if (input.isKeyPressed(KeyEvent.VK_ENTER)) {
            startScreenContext.select();
            if(startScreenContext.isStartPressed()) {
                GameStateHandler gameStateHandler = startScreenContext.getGameHandler() ;
                gameStateHandler.setGameState(GameState.LEVEL_SELECTION_STATE) ;
            }
            else if(startScreenContext.isQuitPressed()) {
                System.exit(0) ;
            }
        }
    }


    private void controlLevelSelectionInput(LevelSelectionContext ctx) {

        int total = ctx.getLevels().length;
        int selected = ctx.getSelectedLevel();

        int columns = 4; // SAME as render
        int rows = (int) Math.ceil((double) total / columns);

        int row = selected / columns;
        int col = selected % columns;

        // ===== MOVEMENT =====
        if (input.isKeyPressed(KeyEvent.VK_LEFT)) {
            col--;
        }

        if (input.isKeyPressed(KeyEvent.VK_RIGHT)) {
            col++;
        }

        if (input.isKeyPressed(KeyEvent.VK_UP)) {
            row--;
        }

        if (input.isKeyPressed(KeyEvent.VK_DOWN)) {
            row++;
        }

        // ===== CLAMP VALUES =====
        if (col < 0) col = 0;
        if (col >= columns) col = columns - 1;

        if (row < 0) row = 0;
        if (row >= rows) row = rows - 1;

        int newIndex = row * columns + col;

        // Handle incomplete last row
        if (newIndex >= total) {
            newIndex = total - 1;
        }

        ctx.setSelectedLevel(newIndex);

        // ===== ENTER =====
        if (input.isKeyPressed(KeyEvent.VK_ENTER)) {
            LevelInfo[] levels = ctx.getLevels();
            ctx.setLevel(LevelLoader.loadlevel(levels[newIndex].getFilePath()));
            GameStateHandler gameStateHandler = ctx.getGameStateHandler() ;
            gameStateHandler.setGameState(GameState.GAME_PLAYING_STATE) ;
        }
    }

    private void controlGameInput() {
        if(input.isKeyPressed(KeyEvent.VK_A)) robot1.moveLeft();
        else if(input.isKeyPressed(KeyEvent.VK_D)) robot1.moveRight();
        else if(!robot1.getAnimationManager().isAttackAnimating()) robot1.idle();

        if(input.isKeyPressed(KeyEvent.VK_W)) robot1.jump() ;

        if(input.isKeyPressed((KeyEvent.VK_Q))) robot1.shoot();
        else if(robot1.getAnimationManager().isAttackFinished()) robot1.idle();

        if(input.isKeyPressed(KeyEvent.VK_E)) robot1.attack(); ;

        if(input.isKeyPressed(KeyEvent.VK_LEFT)) robot2.moveLeft();
        else if(input.isKeyPressed(KeyEvent.VK_RIGHT)) robot2.moveRight();
        else if(!robot2.getAnimationManager().isAttackAnimating()) robot2.idle();

        if(input.isKeyPressed(KeyEvent.VK_UP)) robot2.jump() ;

        if(input.isKeyPressed(KeyEvent.VK_SPACE)) robot2.shoot();
        else if (robot2.getAnimationManager().isAttackFinished()) robot2.idle() ;

        if(input.isKeyPressed(KeyEvent.VK_NUMPAD0)) robot2.attack();
    }
}
