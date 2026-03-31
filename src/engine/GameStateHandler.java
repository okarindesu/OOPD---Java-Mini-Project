package engine;

public class GameStateHandler {
    GameState gameState ;
    public GameStateHandler(GameState gameState) {
        this.gameState = gameState ;
    }

    public GameState getGameState() { return gameState ; }
    public void setGameState(GameState gameState) { this.gameState = gameState ; }
}
