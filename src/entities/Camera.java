package entities;

public class Camera {
    float cameraX ;
    float cameraY ;

    public Camera(float cameraX , float cameraY) {
        this.cameraX = cameraX ;
        this.cameraY = cameraY ;
    }

    public float getCameraX() { return this.cameraX ; }
    public void setCameraX(float cameraX) { this.cameraX = cameraX ; }

    public float getCameraY() { return this.cameraY ; }
    public void setCameraY(float cameraY) { this.cameraY = cameraY ; }
}
