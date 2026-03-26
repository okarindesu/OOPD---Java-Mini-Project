package main;
import engine.GameLoop;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        GameLoop game = new GameLoop() ;

        JFrame frame = new JFrame("RoboWars") ;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
        frame.setResizable(false) ;
        frame.add(game) ;
        frame.pack() ;
        frame.setLocationRelativeTo(null) ;
        frame.setVisible(true) ;

        game.start() ;
    }
}

/*
    Anshul : spawning points of powerups and powerups design
    Ballu : Weapons , Bullet System / Heatlh System
    Heramb : Texture and Traps on Levels
    Sam : Sprites
 */