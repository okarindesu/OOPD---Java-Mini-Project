package entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PowerUpSystem {
    private static final float FALL_SPEED = 190.0f;
    private static final float SPAWN_Y = -40.0f;
    private static final long SPAWN_INTERVAL_MS = 15000;
    private static final int MAX_POWER_UPS_PER_LEVEL = 2;
    /** Floor segments use tall colliders (e.g. 80px); thin platforms stay below this. */
    private static final float GROUND_MIN_TILE_HEIGHT = 45.0f;
    private static final float SPAWN_X_MARGIN = 8.0f;
    private static final float POWERUP_WIDTH = 26.0f;

    private final List<PowerUp> powerUps;
    private final Random random;
    private long nextSpawnAt;
    private int spawnedThisLevel;

    public PowerUpSystem() {
        this.powerUps = new ArrayList<>();
        this.random = new Random();
        this.spawnedThisLevel = 0;
        scheduleNextSpawn();
    }

    public void update(Level level, Robot robot1, Robot robot2) {
        long now = System.currentTimeMillis();
        if (spawnedThisLevel < MAX_POWER_UPS_PER_LEVEL && now >= nextSpawnAt) {
            Float spawnX = pickSpawnXOverGround(level);
            if (spawnX != null) {
                PowerUpType type = random.nextBoolean() ? PowerUpType.SPEED_BOOST : PowerUpType.DAMAGE_BOOST;
                powerUps.add(new PowerUp(type, spawnX, SPAWN_Y, FALL_SPEED));
                spawnedThisLevel++;
            }
            scheduleNextSpawn();
        }

        updateFallAndLanding(level);
        applyPickups(robot1, robot2);
    }

    private Float pickSpawnXOverGround(Level level) {
        List<Tile> grounds = new ArrayList<>();
        int n = level.getLevelSize();
        for (int i = 0; i < n; i++) {
            Tile t = level.findTile(i);
            if (isGroundTile(t)) {
                grounds.add(t);
            }
        }
        if (grounds.isEmpty()) {
            return null;
        }
        Tile tile = grounds.get(random.nextInt(grounds.size()));
        float tx = tile.getPosition().getVector2DX();
        float tw = tile.getTileWidth();
        float minX = tx + SPAWN_X_MARGIN;
        float maxX = tx + tw - POWERUP_WIDTH - SPAWN_X_MARGIN;
        if (maxX <= minX) {
            float centered = tx + (tw - POWERUP_WIDTH) * 0.5f;
            return Math.max(tx, Math.min(centered, tx + tw - POWERUP_WIDTH));
        }
        return minX + random.nextFloat() * (maxX - minX);
    }

    private static boolean isGroundTile(Tile tile) {
        if (tile == null) {
            return false;
        }
        if (tile.getIsMoving()) {
            return false;
        }
        return tile.getTileHeight() >= GROUND_MIN_TILE_HEIGHT;
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
                if (!isGroundTile(tile)) continue;

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
        spawnedThisLevel = 0;
        scheduleNextSpawn();
    }
}