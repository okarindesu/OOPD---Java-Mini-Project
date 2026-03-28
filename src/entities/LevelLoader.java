package entities;

import utils.Vector2D;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LevelLoader {

    public static Level loadlevel(String filePath) {
        ArrayList<Tile> tiles = new ArrayList<>();
        ArrayList<ParallaxObject> parallaxObjects = new ArrayList<>();

        System.out.println("Loading level from: " + filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(" ");
                String type = parts[0];

                if (type.equals("PARALLAX")) {
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float depth = Float.parseFloat(parts[3]);
                    String path = parts[4];

                    BufferedImage img = TextureManager.getTexture(path);
                    parallaxObjects.add(new ParallaxObject(img, new Vector2D(x, y), depth));
                }
                else if (type.equals("STATIC")) {
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float w = Float.parseFloat(parts[3]);
                    float h = Float.parseFloat(parts[4]);
                    String texturePath = parts[5];

                    Tile tile = new Tile(new Vector2D(x, y), w, h, texturePath);
                    tiles.add(tile);
                }
                else if (type.equals("MOVING")) {
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float w = Float.parseFloat(parts[3]);
                    float h = Float.parseFloat(parts[4]);
                    float velX = Float.parseFloat(parts[5]);
                    float velY = Float.parseFloat(parts[6]);
                    float ampX = Float.parseFloat(parts[7]);
                    float ampY = Float.parseFloat(parts[8]);
                    String texturePath = parts[9];

                    Tile tile = new Tile(new Vector2D(x, y), w, h, new Vector2D(ampX, ampY), new Vector2D(velX, velY), texturePath);
                    if (parts.length == 11) {
                        float restitution = Float.parseFloat(parts[10]);
                        tile.restitution = restitution;
                    }
                    tiles.add(tile);
                }
                else {
                    System.out.println("Invalid line in level: " + line);
                    return null;
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to load level: " + filePath);
            e.printStackTrace();
            return null;
        }

        return new Level(tiles, parallaxObjects);
    }
}