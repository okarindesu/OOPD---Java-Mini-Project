import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    boolean aPressed,dPressed,wPressed,fPressed;
    boolean leftPressed,rightPressed,upPressed,slashPressed;

    public void keyPressed(KeyEvent e){

        int code = e.getKeyCode();

        // PLAYER 1
        if(code == KeyEvent.VK_A) aPressed = true;
        if(code == KeyEvent.VK_D) dPressed = true;
        if(code == KeyEvent.VK_W) wPressed = true;
        if(code == KeyEvent.VK_F) fPressed = true;

        // PLAYER 2
        if(code == KeyEvent.VK_LEFT) leftPressed = true;
        if(code == KeyEvent.VK_RIGHT) rightPressed = true;
        if(code == KeyEvent.VK_UP) upPressed = true;
        if(code == KeyEvent.VK_SLASH) slashPressed = true;
    }

    public void keyReleased(KeyEvent e){

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_A) aPressed = false;
        if(code == KeyEvent.VK_D) dPressed = false;
        if(code == KeyEvent.VK_W) wPressed = false;
        if(code == KeyEvent.VK_F) fPressed = false;

        if(code == KeyEvent.VK_LEFT) leftPressed = false;
        if(code == KeyEvent.VK_RIGHT) rightPressed = false;
        if(code == KeyEvent.VK_UP) upPressed = false;
        if(code == KeyEvent.VK_SLASH) slashPressed = false;
    }

    public void keyTyped(KeyEvent e){}
}