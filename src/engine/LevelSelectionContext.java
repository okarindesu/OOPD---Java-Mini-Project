package engine;

import entities.Level;
import entities.LevelInfo;

public class LevelSelectionContext {
    private GameState gameState ;
    private int selectedLevel ;
    private LevelInfo[] levels ;
    private Level level ;

    public LevelSelectionContext(GameState gameState , LevelInfo[] levels , int selectedLevel) {
        this.gameState = gameState ;
        this.levels = levels ;
        this.level = null ;
        this.selectedLevel = selectedLevel ;
    }

    public GameState getGameState() { return gameState; }
    public void setGameState(GameState gameState) { this.gameState = gameState; }

    public int getSelectedLevel() { return selectedLevel; }
    public void setSelectedLevel(int selectedLevel) { this.selectedLevel = selectedLevel; }

    public LevelInfo[] getLevels() { return levels; }

    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }

}
