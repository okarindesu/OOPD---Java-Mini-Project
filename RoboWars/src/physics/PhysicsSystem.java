package physics;

import entities.Robot;
import utils.Vector2D;

public class PhysicsSystem {
    private float inputDT = 0.016f ;
    public PhysicsSystem() {}

    public void update(Robot robot) {
        Vector2D vec = robot.getPosition() ;
        Vector2D velscale = robot.getVelocity().scale(inputDT) ;
        robot.setPosition(vec.add(velscale)) ; ;
    }
}
