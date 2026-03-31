package engine;

import entities.Level;
import entities.LevelInfo;

public class LevelSelectionContext {
    private GameStateHandler gameStateHandler ;
    private int selectedLevel ;
    private LevelInfo[] levels ;
    private Level level ;

    public LevelSelectionContext(GameStateHandler gameStateHandler , LevelInfo[] levels , int selectedLevel) {
        this.gameStateHandler = gameStateHandler ;
        this.levels = levels ;
        this.level = null ;
        this.selectedLevel = selectedLevel ;
    }

    public GameStateHandler getGameStateHandler() { return gameStateHandler ; }
    public void setGameStateHandler(GameStateHandler gameStateHandler) { this.gameStateHandler = gameStateHandler; }

    public int getSelectedLevel() { return selectedLevel; }
    public void setSelectedLevel(int selectedLevel) { this.selectedLevel = selectedLevel; }

    public LevelInfo[] getLevels() { return levels; }

    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }

}
