package engine;

public class StartScreenContext {

    private int selectedIndex ;
    private GameStateHandler gameStateHandler ;
    private boolean startPressed ;
    private boolean quitPressed ;

    private final String[] options = {
            "Start Game",
            "Quit Game"
    };

    public StartScreenContext(GameStateHandler gameStateHandler) {
        this.gameStateHandler = gameStateHandler ;
        this.selectedIndex = 0 ;
        this.startPressed = false ;
        this.quitPressed = false ;
    }

    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + options.length) % options.length ;
    }

    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % options.length ;
    }

    public void select() {
        if (selectedIndex == 0) startPressed = true ;
        if (selectedIndex == 1) quitPressed = true ;
    }

    public void resetActions() {
        startPressed = false ;
        quitPressed = false ;
    }

    public int getSelectedIndex() { return selectedIndex ; }
    public String[] getOptions() { return options ; }

    public boolean isStartPressed() { return startPressed ; }
    public boolean isQuitPressed() { return quitPressed ; }

    public GameStateHandler getGameHandler() { return gameStateHandler ; }
}
