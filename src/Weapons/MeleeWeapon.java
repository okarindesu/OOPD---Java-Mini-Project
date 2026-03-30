package Weapons;

import entities.Robot;

public class MeleeWeapon {
    private float meleeDamage ;

    public MeleeWeapon(float meleeDamage) {
        this.meleeDamage = meleeDamage ;
    }

    public void use(Robot robot) {
        robot.attack() ;
    }

    public float getMeleeDamage() {
        return meleeDamage;
    }
}
