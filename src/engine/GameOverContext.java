package engine;

public class GameOverContext {

    private final GameStateHandler gameStateHandler;

    private int selectedIndex;
    private boolean replayPressed;
    private boolean levelSelectPressed;
    private boolean quitPressed;
    private GameOverState gameOverState;

    public long startTime;
    public long fadeDuration = 5000; // ms
    public boolean justEntered = true;

    private final String[] options = {
            "Replay Level",
            "Level Select",
            "Quit Game"
    };

    public GameOverContext(GameStateHandler gameStateHandler) {
        this.gameStateHandler = gameStateHandler;
        this.selectedIndex = 0;
        this.replayPressed = false;
        this.levelSelectPressed = false;
        this.quitPressed = false;
        this.gameOverState = GameOverState.NONE;
    }

    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + options.length) % options.length;
    }

    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % options.length;
    }

    public void select() {
        if (selectedIndex == 0) replayPressed = true;
        if (selectedIndex == 1) levelSelectPressed = true;
        if (selectedIndex == 2) quitPressed = true;
    }

    public void resetActions() {
        replayPressed = false;
        levelSelectPressed = false;
        quitPressed = false;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String[] getOptions() {
        return options;
    }

    public boolean isReplayPressed() {
        return replayPressed;
    }

    public boolean isLevelSelectPressed() {
        return levelSelectPressed;
    }

    public boolean isQuitPressed() {
        return quitPressed;
    }

    public GameStateHandler getGameStateHandler() {
        return gameStateHandler;
    }

    public void setGameOverState(GameOverState gameOverState) {
        this.gameOverState = gameOverState;
    }

    public GameOverState getGameOverState() {
        return gameOverState;
    }

    public String getWinnerText() {
        switch (gameOverState) {
            case PLAYER1_WINS:
                return "Player 1 Wins!";
            case PLAYER2_WINS:
                return "Player 2 Wins!";
            case DRAW:
                return "Draw!";
            default:
                return "";
        }
    }
}

