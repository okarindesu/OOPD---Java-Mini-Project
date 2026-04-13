package entities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Objects;

public class TextureManager {
    private static HashMap<String, BufferedImage> textures = new HashMap<>() ;

    public static BufferedImage getTexture(String path) {
        if(!textures.containsKey(path)) {
            try {
                BufferedImage img = ImageIO.read(Objects.requireNonNull(TextureManager.class.getClassLoader().getResourceAsStream(path.substring(1)))) ;
                textures.put(path, img) ;
            } catch(Exception e) {
                e.printStackTrace() ;
            }
        }
        return textures.get(path) ;
    }
}
