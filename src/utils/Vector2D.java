package utils;

public class Vector2D {
    private float x ;
    private float y ;

    public Vector2D() {
        this.x = 0.0f ;
        this.y = 0.0f ;
    }

    public Vector2D(float x , float y) {
        this.x = x ;
        this.y = y ;
    }

    public Vector2D add(Vector2D v) {
        return new Vector2D(this.x + v.x , this.y + v.y) ;
    }

    public void addLocal(Vector2D v) {
        this.x += v.x ;
        this.y += v.y ;
    }

    public Vector2D sub(Vector2D v) {
        return new Vector2D(this.x - v.x , this.y - v.y) ;
    }

    public void subLocal(Vector2D v) {
        this.x -= v.x ;
        this.y -= v.y ;
    }

    public Vector2D scale(float s) {
        return new Vector2D(this.x * s , this.y * s) ;
    }

    public void scaleLocal(float s) {
        this.x *= s ;
        this.y *= s ;
    }

    public Vector2D div(float s) {
        return new Vector2D(this.x / s , this.y / s) ;
    }

    public void divLocal(float s) {
        this.x /= s ;
        this.y /= s ;
    }

    public float length() {
        return (float) Math.sqrt(x*x + y*y) ;
    }

    public Vector2D normalize() {
        float len = length() ;
        if(len == 0) return new Vector2D(0,0) ;
        return new Vector2D(x/len , y/len) ;
    }

    public void normalizeLocal() {
        float len = length() ;
        if(len == 0) return ;
        this.x /= len ;
        this.y /= len ;
    }

    public float dot(Vector2D v) {
        return this.x * v.x + this.y * v.y ;
    }

    public void set(float x , float y) {
        this.x = x ;
        this.y = y ;
    }

    public void set(Vector2D v) {
        this.x = v.x ;
        this.y = v.y ;
    }

    public Vector2D copy() {
        return new Vector2D(this.x , this.y) ;
    }

    public float getVector2DX() { return this.x ; }
    public float getVector2DY() { return this.y ; }
}
