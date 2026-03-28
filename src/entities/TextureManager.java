package entities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Objects;

public class TextureManager {
    private static HashMap<String, BufferedImage> textures = new HashMap<>() ;

    public static BufferedImage getTexture(String path) {
        if (!textures.containsKey(path)) {
            try {
                String cleanPath = path.startsWith("/") ? path.substring(1) : path;

                BufferedImage img = ImageIO.read(
                        Objects.requireNonNull(
                                TextureManager.class.getClassLoader()
                                        .getResourceAsStream(cleanPath)
                        )
                );

                textures.put(path, img);
            } catch (Exception e) {
                System.out.println("❌ Failed to load texture: " + path);
                e.printStackTrace();
            }
        }
        return textures.get(path);
    }
}
