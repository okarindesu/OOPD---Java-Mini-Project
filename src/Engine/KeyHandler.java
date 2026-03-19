package Engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean aPressed, dPressed, wPressed, fPressed, rPressed;
    public boolean leftPressed, rightPressed, upPressed, slashPressed, rshiftPressed;

    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if (code == KeyEvent.VK_A)
            aPressed = true;
        if (code == KeyEvent.VK_D)
            dPressed = true;
        if (code == KeyEvent.VK_W)
            wPressed = true;
        if (code == KeyEvent.VK_F)
            fPressed = true;
        if (code == KeyEvent.VK_R)
            rPressed = true;

        if (code == KeyEvent.VK_LEFT)
            leftPressed = true;
        if (code == KeyEvent.VK_RIGHT)
            rightPressed = true;
        if (code == KeyEvent.VK_UP)
            upPressed = true;
        if (code == KeyEvent.VK_SLASH)
            slashPressed = true;
        if (code == KeyEvent.VK_SHIFT)
            rshiftPressed = true;
    }

    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if (code == KeyEvent.VK_A)
            aPressed = false;
        if (code == KeyEvent.VK_D)
            dPressed = false;
        if (code == KeyEvent.VK_W)
            wPressed = false;
        if (code == KeyEvent.VK_F)
            fPressed = false;

        if (code == KeyEvent.VK_LEFT)
            leftPressed = false;
        if (code == KeyEvent.VK_RIGHT)
            rightPressed = false;
        if (code == KeyEvent.VK_UP)
            upPressed = false;
        if (code == KeyEvent.VK_SLASH)
            slashPressed = false;
    }

    public void keyTyped(KeyEvent e) {
    }
}