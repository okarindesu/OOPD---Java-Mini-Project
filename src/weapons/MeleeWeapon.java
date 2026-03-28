package weapons;

import entities.Robot;

public class MeleeWeapon implements Weapon {

    @Override
    public void use(Robot user) {
        user.attack();
    }
}