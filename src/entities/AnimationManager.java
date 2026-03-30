package entities;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.ArrayList;

public class AnimationManager {
    private HashMap<String, ArrayList<BufferedImage>> animations;
    private String currentState;
    private int currentFrame;
    private long lastFrameTime;
    private long frameDuration; // milliseconds per frame
    private String lastDirection; // "right" or "left"
    private boolean animationFinished; // track if current animation completed

    public AnimationManager(long frameDuration) {
        this.animations = new HashMap<>();
        this.frameDuration = frameDuration;
        this.currentFrame = 0;
        this.lastFrameTime = System.currentTimeMillis();
        this.lastDirection = "right"; // default direction
        this.animationFinished = false;
    }

    public void addAnimation(String stateName, ArrayList<BufferedImage> frames) {
        animations.put(stateName, frames);
    }

    public void setState(String newState) {
        if (!newState.equals(currentState)) {
            currentState = newState;
            currentFrame = 0;
            lastFrameTime = System.currentTimeMillis();
            animationFinished = false;
        }
    }

    public void setDirection(String direction) {
        if (direction.equals("right") || direction.equals("left")) {
            this.lastDirection = direction;
        }
    }

    public String getLastDirection() {
        return lastDirection;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= frameDuration) {
            if (currentState != null && animations.containsKey(currentState)) {
                ArrayList<BufferedImage> frames = animations.get(currentState);
                
                // For attack animations, don't loop - stop at last frame
                if (currentState.startsWith("attack_")) {
                    if (currentFrame < frames.size() - 1) {
                        currentFrame++;
                    } else {
                        animationFinished = true;
                    }
                } else {
                    // Other animations loop normally
                    currentFrame = (currentFrame + 1) % frames.size();
                }
                lastFrameTime = currentTime;
            }
        }
    }

    public BufferedImage getCurrentFrame() {
        if (currentState == null || !animations.containsKey(currentState)) {
            return null;
        }

        ArrayList<BufferedImage> frames = animations.get(currentState);
        if (frames.isEmpty()) return null;
        
        return frames.get(currentFrame);
    }

    public BufferedImage getFrame(String state, int frameIndex) {
        if (!animations.containsKey(state)) {
            return null;
        }
        ArrayList<BufferedImage> frames = animations.get(state);
        if (frameIndex < 0 || frameIndex >= frames.size()) {
            return null;
        }
        return frames.get(frameIndex);
    }

    public void resetAnimation() {
        currentFrame = 0;
        lastFrameTime = System.currentTimeMillis();
    }

    public boolean isAttackAnimating() {
        return currentState != null && currentState.startsWith("attack_");
    }

    public boolean isAttackFinished() {
        return animationFinished && currentState != null && currentState.startsWith("attack_");
    }

    public String getCurrentState() {
        return currentState;
    }

    public int getCurrentFrameIndex() {
        return currentFrame;
    }
}
