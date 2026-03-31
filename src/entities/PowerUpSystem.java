package entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PowerUpSystem {
    private static final float FALL_SPEED = 190.0f;
    private static final float SPAWN_Y = -40.0f;
    private static final long SPAWN_INTERVAL_MS = 15000;

    private final List<PowerUp> powerUps;
    private final Random random;
    private long nextSpawnAt;

    public PowerUpSystem() {
        this.powerUps = new ArrayList<>();
        this.random = new Random();
        scheduleNextSpawn();
    }

    public void update(Level level, Robot robot1, Robot robot2, int worldWidth) {
        long now = System.currentTimeMillis();
        if (now >= nextSpawnAt) {
            spawnRandom(worldWidth);
            scheduleNextSpawn();
        }

        updateFallAndLanding(level);
        applyPickups(robot1, robot2);
    }

    private void spawnRandom(int worldWidth) {
        float spawnX = 40 + random.nextInt(Math.max(1, worldWidth - 80));
        PowerUpType type = random.nextBoolean() ? PowerUpType.SPEED_BOOST : PowerUpType.DAMAGE_BOOST;
        powerUps.add(new PowerUp(type, spawnX, SPAWN_Y, FALL_SPEED));
    }

    private void scheduleNextSpawn() {
        nextSpawnAt = System.currentTimeMillis() + SPAWN_INTERVAL_MS;
    }

    private void updateFallAndLanding(Level level) {
        int levelSize = level.getLevelSize();

        for (PowerUp powerUp : powerUps) {
            if (powerUp.isLanded()) {
                continue;
            }

            float oldY = powerUp.getPosition().getVector2DY();
            float newY = oldY + powerUp.getVelocity().getVector2DY() * 0.016f;
            float xLeft = powerUp.getPosition().getVector2DX();
            float xRight = xLeft + powerUp.getWidth();

            Tile landingTile = null;
            float landingY = newY;
            float bestTop = Float.MAX_VALUE;

            for (int i = 0; i < levelSize; i++) {
                Tile tile = level.findTile(i);
                if (tile == null) continue;

                float tx = tile.getPosition().getVector2DX();
                float ty = tile.getPosition().getVector2DY();
                float tw = tile.getTileWidth();

                boolean overlapX = xRight > tx && xLeft < tx + tw;
                boolean crossesTop = oldY + powerUp.getHeight() <= ty && newY + powerUp.getHeight() >= ty;

                if (overlapX && crossesTop && ty < bestTop) {
                    bestTop = ty;
                    landingTile = tile;
                    landingY = ty - powerUp.getHeight();
                }
            }

            if (landingTile != null) {
                powerUp.getPosition().set(powerUp.getPosition().getVector2DX(), landingY);
                powerUp.getVelocity().set(0, 0);
                powerUp.setLanded(true);
            } else {
                powerUp.getPosition().set(powerUp.getPosition().getVector2DX(), newY);
            }
        }
    }

    private void applyPickups(Robot robot1, Robot robot2) {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            if (intersects(powerUp, robot1)) {
                applyToRobot(robot1, powerUp.getType());
                iterator.remove();
                continue;
            }
            if (intersects(powerUp, robot2)) {
                applyToRobot(robot2, powerUp.getType());
                iterator.remove();
            }
        }
    }

    private boolean intersects(PowerUp powerUp, Robot robot) {
        float px = powerUp.getPosition().getVector2DX();
        float py = powerUp.getPosition().getVector2DY();
        float pw = powerUp.getWidth();
        float ph = powerUp.getHeight();

        float rx = robot.getPosition().getVector2DX();
        float ry = robot.getPosition().getVector2DY();
        float rw = robot.getRoboWidth();
        float rh = robot.getRoboHeight();

        return px < rx + rw && px + pw > rx && py < ry + rh && py + ph > ry;
    }

    private void applyToRobot(Robot robot, PowerUpType type) {
        if (type == PowerUpType.SPEED_BOOST) {
            robot.applySpeedMultiplier(1.5f);
        } else {
            robot.applyDamageBonus(15.0f);
        }
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public void reset() {
        powerUps.clear();
        scheduleNextSpawn();
    }
}
