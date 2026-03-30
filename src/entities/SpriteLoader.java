package entities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;

public class SpriteLoader {
    
    public static ArrayList<BufferedImage> loadFrames(String... framePaths) {
        ArrayList<BufferedImage> frames = new ArrayList<>();
        
        for (String path : framePaths) {
            try {
                BufferedImage img = ImageIO.read(
                    Objects.requireNonNull(
                        SpriteLoader.class.getClassLoader().getResourceAsStream(path.substring(1))
                    )
                );
                frames.add(img);
            } catch (Exception e) {
                System.err.println("Failed to load sprite: " + path);
                e.printStackTrace();
            }
        }
        
        return frames;
    }
}
