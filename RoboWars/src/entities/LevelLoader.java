package entities;

import utils.Vector2D;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LevelLoader {
    public static Level loadlevel(String filePath) {
        ArrayList<Tile> tiles = new ArrayList<>() ;
        BufferedImage background = null ;

        System.out.println("Loading level from: " + filePath);
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line ;
            while((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(" ");
                String type = parts[0];

                if (type.equals("BACKGROUND")) {
                    String path = parts[1];
                    background = TextureManager.getTexture(path);
                }

                else if (type.equals("STATIC")) {
                    if(parts.length < 6) {
                        IO.println("Invalid File Format ! Cannot Load Level !");
                        return null ;
                    }
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float w = Float.parseFloat(parts[3]);
                    float h = Float.parseFloat(parts[4]);
                    String texturePath = parts[5] ;

                    Vector2D pos = new Vector2D(x, y);
                    Tile tile = new Tile(pos, w, h , texturePath);
                    tiles.add(tile);
                } else if (type.equals("MOVING")) {
                    if(parts.length < 10) {
                        IO.println("Invalid File Format ! Cannot Load Level !");
                        return null ;
                    }
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float w = Float.parseFloat(parts[3]);
                    float h = Float.parseFloat(parts[4]);

                    float velX = Float.parseFloat(parts[5]);
                    float velY = Float.parseFloat(parts[6]);
                    float ampX = Float.parseFloat(parts[7]);
                    float ampY = Float.parseFloat(parts[8]);
                    String texturePath = parts[9] ;


                    Vector2D pos = new Vector2D(x , y);
                    Vector2D vel = new Vector2D(velX, velY);
                    Vector2D amp = new Vector2D(ampX, ampY);

                    Tile tile = new Tile(pos, w, h, amp, vel , texturePath) ;
                    if(parts.length == 11) {
                        float restitution = Float.parseFloat(parts[10]) ;
                        tile.restitution = restitution ;
                    }
                    tiles.add(tile);
                } else {
                    IO.println("Invalid File Format ! Level Cannot Be Loaded !");
                    return null;
                }
            }
        } catch(IOException e) {
            e.printStackTrace(); ;
        }
        return new Level(tiles , background) ;
    }
}
