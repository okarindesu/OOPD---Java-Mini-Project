import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {

        JFrame window = new JFrame("Mini Smash Game");

        GamePanel gamePanel = new GamePanel();

        window.add(gamePanel);
        window.setSize(800,600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setVisible(true);

        gamePanel.startGameThread();
    }
}