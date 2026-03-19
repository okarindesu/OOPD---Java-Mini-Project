package Weapons;

import Player.Player;
import java.awt.Rectangle;

public class Attack {

    public static boolean hit(Player attacker, Player defender) {

        Rectangle hitbox;

        int size = 30; // attack size

        switch (attacker.direction) {

            case "LEFT":
                hitbox = new Rectangle(
                        attacker.x - size,
                        attacker.y,
                        size,
                        attacker.height);
                break;

            case "RIGHT":
                hitbox = new Rectangle(
                        attacker.x + attacker.width,
                        attacker.y,
                        size,
                        attacker.height);
                break;

            case "UP":
                hitbox = new Rectangle(
                        attacker.x,
                        attacker.y - size,
                        attacker.width,
                        size);
                break;

            case "DOWN":
                hitbox = new Rectangle(
                        attacker.x,
                        attacker.y + attacker.height,
                        attacker.width,
                        size);
                break;

            default:
                hitbox = new Rectangle(
                        attacker.x + attacker.width,
                        attacker.y,
                        size,
                        attacker.height);
        }

        Rectangle enemy = new Rectangle(
                defender.x,
                defender.y,
                defender.width,
                defender.height);

        return hitbox.intersects(enemy);
    }
}