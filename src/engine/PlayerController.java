package engine;

import entities.*;
import utils.Vector2D;
import java.awt.event.KeyEvent;
import java.util.List;

public class PlayerController {
    private Robot robot1;
    private Robot robot2;
    private InputHandler input;
    private GameOverContext gameOverContext;

    private long lastStartScreenInputTime = 0;
    private static final long START_SCREEN_COOLDOWN = 200; 
    private long lastLevelSelectInputTime = 0;
    private static final long LEVEL_SELECT_COOLDOWN = 200; 
    private long lastGameOverInputTime = 0;
    private static final long GAME_OVER_COOLDOWN = 200; 
    private boolean enterWasPressed = false;

    public PlayerController(Robot robot1, Robot robot2, InputHandler input) {
        this.robot1 = robot1;
        this.robot2 = robot2;
        this.input = input;
    }

    public void control(StartScreenContext startScreenContext, LevelSelectionContext levelSelectionContext, GameOverContext gameOverContext) {
        this.gameOverContext = gameOverContext;
        GameState gameState = levelSelectionContext.getGameStateHandler().getGameState();
        
        switch (gameState) {
            case START_SCREEN_STATE:
                controlStartScreenInput(startScreenContext);
                break;
            case LEVEL_SELECTION_STATE:
                controlLevelSelectionInput(levelSelectionContext);
                break;
            case GAME_PLAYING_STATE:
                controlGameInput();
                break;
            case GAME_OVER_STATE:
                controlGameOverInput();
                break;
        }
    }

    private void controlStartScreenInput(StartScreenContext startScreenContext) {
        long currentTime = System.currentTimeMillis();
        boolean canInput = (currentTime - lastStartScreenInputTime) >= START_SCREEN_COOLDOWN;

        if (canInput) {
            if (input.isKeyPressed(KeyEvent.VK_UP)) {
                SoundManager.play("move_ui");
                startScreenContext.moveUp();
                lastStartScreenInputTime = currentTime;
            }
            if (input.isKeyPressed(KeyEvent.VK_DOWN)) {
                SoundManager.play("move_ui");
                startScreenContext.moveDown();
                lastStartScreenInputTime = currentTime;
            }

            if (!enterWasPressed && input.isKeyPressed(KeyEvent.VK_ENTER)) {
                SoundManager.play("select_ui");
                startScreenContext.select();
                if(startScreenContext.isStartPressed()) {
                    startScreenContext.getGameHandler().setGameState(GameState.LEVEL_SELECTION_STATE);
                } else if(startScreenContext.isQuitPressed()) {
                    System.exit(0);
                }
                lastStartScreenInputTime = currentTime;
            }
        }
        enterWasPressed = input.isKeyPressed(KeyEvent.VK_ENTER);
    }

    private void controlLevelSelectionInput(LevelSelectionContext ctx) {
        int total = ctx.getLevels().length;
        int selected = ctx.getSelectedLevel();
        int columns = 4; 
        int rows = (int) Math.ceil((double) total / columns);

        int row = selected / columns;
        int col = selected % columns;

        long currentTime = System.currentTimeMillis();
        boolean canMove = (currentTime - lastLevelSelectInputTime) >= LEVEL_SELECT_COOLDOWN;

        if (canMove) {
            if (input.isKeyPressed(KeyEvent.VK_LEFT)) {
                SoundManager.play("move_ui");
                col--;
                lastLevelSelectInputTime = currentTime;
            } else if (input.isKeyPressed(KeyEvent.VK_RIGHT)) {
                SoundManager.play("move_ui");
                col++;
                lastLevelSelectInputTime = currentTime;
            }

            if (input.isKeyPressed(KeyEvent.VK_UP)) {
                SoundManager.play("move_ui");
                row--;
                lastLevelSelectInputTime = currentTime;
            } else if (input.isKeyPressed(KeyEvent.VK_DOWN)) {
                SoundManager.play("move_ui");
                row++;
                lastLevelSelectInputTime = currentTime;
            }
        }

        // Clamp values
        if (col < 0) col = 0;
        if (col >= columns) col = columns - 1;
        if (row < 0) row = 0;
        if (row >= rows) row = rows - 1;

        int newIndex = row * columns + col;
        if (newIndex >= total) newIndex = total - 1;

        ctx.setSelectedLevel(newIndex);

        if (!enterWasPressed && input.isKeyPressed(KeyEvent.VK_ENTER) && canMove) {
            SoundManager.play("select_ui");
            LevelInfo[] levels = ctx.getLevels();
            ctx.setLevel(LevelLoader.loadlevel(levels[newIndex].getFilePath()));
            ctx.getGameStateHandler().setGameState(GameState.GAME_PLAYING_STATE);
            lastLevelSelectInputTime = currentTime;
        }

        enterWasPressed = input.isKeyPressed(KeyEvent.VK_ENTER);
    }

    private void controlGameInput() {
        // --- Player 1 (Robot 1) ---
        if(input.isKeyPressed(KeyEvent.VK_A)) robot1.moveLeft();
        else if(input.isKeyPressed(KeyEvent.VK_D)) robot1.moveRight();
        else if(!robot1.getAnimationManager().isAttackAnimating()) robot1.idle();

        if(input.isKeyPressed(KeyEvent.VK_W)) robot1.jump();

        if(input.isKeyPressed(KeyEvent.VK_Q)) {
            robot1.shoot();
        } else if(input.isKeyPressed(KeyEvent.VK_E)) {
            robot1.attack();
        }

        // --- Player 2 (Robot 2) ---
        if(input.isKeyPressed(KeyEvent.VK_LEFT)) robot2.moveLeft();
        else if(input.isKeyPressed(KeyEvent.VK_RIGHT)) robot2.moveRight();
        else if(!robot2.getAnimationManager().isAttackAnimating()) robot2.idle();

        if(input.isKeyPressed(KeyEvent.VK_UP)) robot2.jump();

        // P2 Space/Numpad0 logic
        if(input.isKeyPressed(KeyEvent.VK_SPACE)) {
            robot2.attack();
        } else if(input.isKeyPressed(KeyEvent.VK_NUMPAD0)) {
            robot2.shoot();
        }
    }

    private void controlGameOverInput() {
        if (gameOverContext == null) return;

        long currentTime = System.currentTimeMillis();
        boolean canInput = (currentTime - lastGameOverInputTime) >= GAME_OVER_COOLDOWN;

        if (canInput) {
            if (input.isKeyPressed(KeyEvent.VK_UP)) {
                SoundManager.play("move_ui");
                gameOverContext.moveUp();
                lastGameOverInputTime = currentTime;
            } else if (input.isKeyPressed(KeyEvent.VK_DOWN)) {
                SoundManager.play("move_ui");
                gameOverContext.moveDown();
                lastGameOverInputTime = currentTime;
            }

            if (!enterWasPressed && input.isKeyPressed(KeyEvent.VK_ENTER)) {
                SoundManager.play("select_ui");
                gameOverContext.select();
                lastGameOverInputTime = currentTime;
            }
        }
        enterWasPressed = input.isKeyPressed(KeyEvent.VK_ENTER);
    }
}