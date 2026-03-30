package engine;

import entities.Level;
import entities.LevelInfo;

public class LevelSelectionContext {
    private GameState gameState ;
    private int selectedLevel ;
    private LevelInfo[] levels ;
    private Level level ;
    private String winnerMessage ;

    public LevelSelectionContext(GameState gameState , LevelInfo[] levels , int selectedLevel) {
        this.gameState = gameState ;
        this.levels = levels ;
        this.level = null ;
        this.selectedLevel = selectedLevel ;
        this.winnerMessage = null;
    }

    public GameState getGameState() { return gameState; }
    public void setGameState(GameState gameState) { this.gameState = gameState; }

    public int getSelectedLevel() { return selectedLevel; }
    public void setSelectedLevel(int selectedLevel) { this.selectedLevel = selectedLevel; }

    public LevelInfo[] getLevels() { return levels; }

    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }

    public String getWinnerMessage() { return winnerMessage; }
    public void setWinnerMessage(String winnerMessage) { this.winnerMessage = winnerMessage; }
}
