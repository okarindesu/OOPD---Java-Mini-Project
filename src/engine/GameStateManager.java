package engine;

import java.awt.event.KeyEvent;

public class GameStateManager {

    public enum GameState {
        START_SCREEN,
        RUNNING,
        PAUSED,
        GAME_OVER
    }

    private GameState currentState;

    public GameStateManager() {
        this.currentState = GameState.START_SCREEN;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setState(GameState newState) {
        this.currentState = newState;
    }

    public void handleInput(InputHandler inputHandler) {
        switch (currentState) {
            case START_SCREEN:
                if (inputHandler.isKeyPressed(KeyEvent.VK_ENTER)) {
                    currentState = GameState.RUNNING;
                }
                break;
            case GAME_OVER:
                if (inputHandler.isKeyPressed(KeyEvent.VK_R)) {
                    currentState = GameState.START_SCREEN;
                }
                break;
            default:
                // Other states can be handled here
                break;
        }
    }

    public boolean isRunning() {
        return currentState == GameState.RUNNING;
    }

    public boolean isStartScreen() {
        return currentState == GameState.START_SCREEN;
    }

    public boolean isGameOver() {
        return currentState == GameState.GAME_OVER;
    }
}


