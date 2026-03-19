package Main;

import javax.swing.JFrame;
import Engine.GamePanel;

public class Main {

    public static void main(String[] args) {

        JFrame window = new JFrame("Mini Smash Game");

        GamePanel gamePanel = new GamePanel();

        window.add(gamePanel);
        window.setSize(1000, 1000);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setVisible(true);

        gamePanel.startGameThread();
    }
}