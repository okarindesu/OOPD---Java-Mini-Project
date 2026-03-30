package entities;

public class AnimationInitializer {
    AnimationManager animationManager1 ;
    AnimationManager animationManager2 ;

    public AnimationInitializer(AnimationManager animationManager1 , AnimationManager animationManager2) {
        this.animationManager1 = animationManager1 ;
        this.animationManager2 = animationManager2 ;
    }

    public void initializeRoboAnimation() {
        animationManager1.addAnimation("idle_right",
                SpriteLoader.loadFrames("/sprites/player1/idle_right/frame1.png"));
        animationManager1.addAnimation("idle_left",
                SpriteLoader.loadFrames("/sprites/player1/idle_left/frame1.png"));
        animationManager1.addAnimation("walk_right",
                SpriteLoader.loadFrames(
                        "/sprites/player1/walk_right/frame1.png",
                        "/sprites/player1/walk_right/frame2.png",
                        "/sprites/player1/walk_right/frame3.png",
                        "/sprites/player1/walk_right/frame4.png"
                ));
        animationManager1.addAnimation("walk_left",
                SpriteLoader.loadFrames(
                        "/sprites/player1/walk_left/frame1.png",
                        "/sprites/player1/walk_left/frame2.png",
                        "/sprites/player1/walk_left/frame3.png",
                        "/sprites/player1/walk_left/frame4.png"
                ));
        animationManager1.addAnimation("jump_left",
                SpriteLoader.loadFrames("/sprites/player1/jump_left/frame1.png"));
        animationManager1.addAnimation("jump_right",
                SpriteLoader.loadFrames("/sprites/player1/jump_right/frame1.png"));
        animationManager1.addAnimation("attack_gun_left",
                SpriteLoader.loadFrames(
                        "/sprites/player1/attack_gun_left/frame1.png",
                        "/sprites/player1/attack_gun_left/frame2.png",
                        "/sprites/player1/attack_gun_left/frame3.png"
                ));
        animationManager1.addAnimation("attack_gun_right",
                SpriteLoader.loadFrames(
                        "/sprites/player1/attack_gun_right/frame1.png",
                        "/sprites/player1/attack_gun_right/frame2.png",
                        "/sprites/player1/attack_gun_right/frame3.png"
                ));

        animationManager1.setState("idle_right");

        animationManager2.addAnimation("idle_right",
                SpriteLoader.loadFrames("/sprites/player2/idle_right/frame1.png"));
        animationManager2.addAnimation("idle_left",
                SpriteLoader.loadFrames("/sprites/player2/idle_left/frame1.png"));
        animationManager2.addAnimation("walk_right",
                SpriteLoader.loadFrames(
                        "/sprites/player2/walk_right/frame1.png",
                        "/sprites/player2/walk_right/frame2.png",
                        "/sprites/player2/walk_right/frame3.png",
                        "/sprites/player2/walk_right/frame4.png"
                ));
        animationManager2.addAnimation("walk_left",
                SpriteLoader.loadFrames(
                        "/sprites/player2/walk_left/frame1.png",
                        "/sprites/player2/walk_left/frame2.png",
                        "/sprites/player2/walk_left/frame3.png",
                        "/sprites/player2/walk_left/frame4.png"
                ));
        animationManager2.addAnimation("jump_left",
                SpriteLoader.loadFrames("/sprites/player2/jump_left/frame1.png"));
        animationManager2.addAnimation("jump_right",
                SpriteLoader.loadFrames("/sprites/player2/jump_right/frame1.png"));
        animationManager2.addAnimation("attack_sword_left",
                SpriteLoader.loadFrames(
                        "/sprites/player2/attack_sword_left/frame1.png",
                        "/sprites/player2/attack_sword_left/frame2.png",
                        "/sprites/player2/attack_sword_left/frame3.png"
                ));
        animationManager2.addAnimation("attack_sword_right",
                SpriteLoader.loadFrames(
                        "/sprites/player2/attack_sword_right/frame1.png",
                        "/sprites/player2/attack_sword_right/frame2.png",
                        "/sprites/player2/attack_sword_right/frame3.png"
                ));
        animationManager2.setState("idle_left");
    }
}
