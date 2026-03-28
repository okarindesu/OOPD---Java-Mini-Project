package entities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class TextureManager {
    private static HashMap<String, BufferedImage> textures = new HashMap<>() ;

    public static BufferedImage getTexture(String path) {
        if(!textures.containsKey(path)) {
            try {
                BufferedImage img = null;
                
                // First try classpath loading
                var stream = TextureManager.class.getClassLoader().getResourceAsStream(path.substring(1));
                if (stream != null) {
                    img = ImageIO.read(stream);
                    stream.close();
                } else {
                    // Fallback to file system loading
                    File file = new File("resources" + path);
                    if (file.exists()) {
                        img = ImageIO.read(file);
                    } else {
                        System.err.println("Failed to load texture: " + path);
                        return null;
                    }
                }
                
                textures.put(path, img) ;
            } catch(Exception e) {
                System.err.println("Failed to load texture: " + path);
                e.printStackTrace() ;
                return null;
            }
        }
        return textures.get(path) ;
    }
}
