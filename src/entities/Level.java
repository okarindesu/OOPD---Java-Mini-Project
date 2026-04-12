package entities;

import java.util.ArrayList;
import java.util.Objects;

public class Level {
    private ArrayList<Tile> tiles ;
    private ArrayList<ParallaxObject> parallaxObjects ;

    public Level(ArrayList<Tile> tiles , ArrayList<ParallaxObject> parallaxObjects) {
        this.parallaxObjects = parallaxObjects ;
        this.tiles = tiles ;
    }

    public Tile findTile(Tile tile) {
        for(Tile itertile : tiles) {
            if(Objects.equals(itertile , tile)) return tile ;
        }
        return null ;
    }

    public Tile findTile(int id) {
        if(id < 0 || id >= tiles.size()) return null ;
        return tiles.get(id) ;
    }

    public void checkTileVelocities() {
        for(Tile itertile : tiles) {
            itertile.changeVelocity() ;
        }
    }

    public int getLevelSize() { return tiles.size() ; }
    public ArrayList<ParallaxObject> getParallaxObjects() { return parallaxObjects ; }
}
