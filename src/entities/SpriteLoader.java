package entities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class SpriteLoader {

    public static ArrayList<BufferedImage> loadFrames(String... framePaths) {
        ArrayList<BufferedImage> frames = new ArrayList<>();

        for (String path : framePaths) {
            try {
            var stream = SpriteLoader.class.getResourceAsStream(path);
            if (stream == null) {
                System.err.println("FILE NOT FOUND IN RESOURCES: " + path);
                continue; 
            }
            BufferedImage img = ImageIO.read(stream);
            frames.add(img);
            System.out.println("Loaded: " + path); // DEBUG
            } catch (Exception e) {
                System.err.println("Failed to load sprite: " + path);
                e.printStackTrace();
            }
        }

        return frames;
    }
}
