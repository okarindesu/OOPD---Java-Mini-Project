package entities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class SpriteLoader {
    
    public static ArrayList<BufferedImage> loadFrames(String... framePaths) {
        ArrayList<BufferedImage> frames = new ArrayList<>();
        
        for (String path : framePaths) {
            try {
                BufferedImage img = null;
                
                // First try classpath loading
                var stream = SpriteLoader.class.getClassLoader().getResourceAsStream(path.substring(1));
                if (stream != null) {
                    img = ImageIO.read(stream);
                    stream.close();
                } else {
                    // Fallback to file system loading
                    File file = new File("resources" + path);
                    if (file.exists()) {
                        img = ImageIO.read(file);
                    } else {
                        System.err.println("Failed to load sprite: " + path);
                        continue;
                    }
                }
                
                frames.add(img);
            } catch (Exception e) {
                System.err.println("Failed to load sprite: " + path);
                e.printStackTrace();
            }
        }
        
        return frames;
    }
}
