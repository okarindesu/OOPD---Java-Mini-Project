package physics;

import entities.*;
import entities.Robot;
import utils.Vector2D;
import weapons.Projectile;
import weapons.ProjectileSystem;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class GameRenderer {
    private Canvas canvas ;
    private int width ;
    private int height ;
    private Font pixelFont;



    public GameRenderer(Canvas canvas , int width , int height) {
        this.canvas = canvas;
        this.width = width;
        this.height = height;

        try {
            InputStream is = getClass().getResourceAsStream("/fonts/ka1.ttf");

            if (is == null) {
                throw new RuntimeException("Font file not found!");
            }

            pixelFont = Font.createFont(Font.TRUETYPE_FONT, is);

        } catch (Exception e) {
            e.printStackTrace();
            pixelFont = new Font("Arial", Font.BOLD, 40);
        }
    }



    public void renderStartScreen(Level level) {
        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        try {
            // =========================
            // 🌄 BACKGROUND (your image)
            // =========================
            drawMenuBackground(g, level);

            Graphics2D g2d = (Graphics2D) g;

            // =========================
            // 🌫 LIGHT OVERLAY (soft)
            // =========================
            g2d.setColor(new Color(0, 0, 0, 60));
            g2d.fillRect(0, 0, width, height);

            // =========================
            // 🎮 TITLE
            // =========================
            String title = "ROBO WARS";

            g2d.setFont(pixelFont.deriveFont(48f));

            int titleW = g2d.getFontMetrics().stringWidth(title);
            g2d.setColor(new Color(40, 60, 80));

            g2d.drawString(title, (width - titleW) / 2, height / 4);

            // =========================
            // 🔘 BUTTONS (PIXEL STYLE)
            // =========================
            int btnW = 300;
            int btnH = 70;

            int centerX = width / 2;

            int playY = height / 2 - 40;
            int exitY = playY + 100;

            drawPixelButton(g2d, centerX - btnW / 2, playY, btnW, btnH, "PLAY", true);
            drawPixelButton(g2d, centerX - btnW / 2, exitY, btnW, btnH, "EXIT", false);

        } finally {
            g.dispose();
        }

        bs.show();
    }
    private void drawPixelButton(Graphics2D g, int x, int y, int w, int h, String text, boolean selected) {

        // Base color
        Color base = selected ? new Color(170, 190, 210) : new Color(140, 160, 180);
        Color borderDark = new Color(70, 90, 110);
        Color borderLight = new Color(220, 230, 240);

        // =========================
        // 🧱 BUTTON BODY
        // =========================
        g.setColor(base);
        g.fillRect(x, y, w, h);

        // =========================
        // ✨ PIXEL BORDER (top/left light)
        // =========================
        g.setColor(borderLight);
        g.fillRect(x, y, w, 4);           // top
        g.fillRect(x, y, 4, h);           // left

        // =========================
        // 🌑 SHADOW BORDER (bottom/right)
        // =========================
        g.setColor(borderDark);
        g.fillRect(x, y + h - 4, w, 4);   // bottom
        g.fillRect(x + w - 4, y, 4, h);   // right

        // =========================
        // 🎯 TEXT
        // =========================
        g.setFont(pixelFont.deriveFont(26f));

        int textW = g.getFontMetrics().stringWidth(text);

        int textX = x + (w - textW) / 2;
        int textY = y + (h / 2) + 10;

        // Shadow
        g.setColor(new Color(0, 0, 0, 120));
        g.drawString(text, textX + 2, textY + 2);

        // Main text
        g.setColor(new Color(40, 50, 60));
        g.drawString(text, textX, textY);
    }

    public void render(Robot robot1 , Robot robot2 , Level level , Camera camera, String winner, ProjectileSystem ps) {
        BufferStrategy bs = canvas.getBufferStrategy() ;
        if (bs == null) return ;

        camera.setCameraX(robot1.getPosition().getVector2DX() - width / 2) ;
        camera.setCameraY(robot1.getPosition().getVector2DY() - height / 2) ;

        Graphics g = bs.getDrawGraphics() ;
        g.setColor(Color.BLACK) ;
        g.fillRect(0 , 0 , width , height) ;

        drawLevel(g , level , camera) ;
        
        // Render robots using their draw() method (handles sprites + animations)
        robot1.draw(g);
        robot2.draw(g);
        
        drawUI(g , robot1 , robot2) ;

        g.setColor(Color.WHITE) ;
        g.drawString("RoboWars" , 10 , 20) ;

        if (winner != null) {
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.setColor(Color.YELLOW);

            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(winner);

            int x = (width - textWidth) / 2;
            int y = height / 2;

            g.drawString(winner, x, y);
        }

        for (Projectile p : ps.getProjectiles()) {
            if (!p.isActive()) continue;

            int x = (int)p.getPosition().getVector2DX();
            int y = (int)p.getPosition().getVector2DY();

            g.setColor(p.getColor());
            g.fillOval(x, y, 12, 12);
        }

        g.dispose() ;
        bs.show() ;
    }

    private void drawLevel(Graphics g , Level level , Camera camera) {
        drawBackground(g , level , camera) ;

        g.setColor(Color.BLUE) ;
        int levelSize = level.getLevelSize() ;
        for(int i = 0 ; i < levelSize ; i++) {
            Tile tile = level.findTile(i) ;
            if(tile != null) drawTile(g , tile) ;
        }
    }

    private void drawUI(Graphics g, Robot robot1 , Robot robot2) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // Title
        g.drawString("RoboWars - 2 Player", 10, 20);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("P1: WASD + Q | P2: Arrows + Space", 10, 35);

        // Player 1 Stats (BLUE)
        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P1", 10, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", robot1.getHealth()) + "/" + (int)Robot.getMaxHealth(), 10, 75);
        g.drawString("Lives: " + robot1.getLives(), 10, 90);

        // Health bar for P1 (BLUE)
        drawHealthBar(g, 10, 100, 150, 15, robot1.getHealth(), Robot.getMaxHealth(), Color.BLUE);

        // Player 2 Stats (GREEN)
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P2", width - 50, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", robot2.getHealth()) + "/" + (int)Robot.getMaxHealth(), width - 180, 75);
        g.drawString("Lives: " + robot2.getLives(), width - 180, 90);

        // Health bar for P2 (GREEN)
        drawHealthBar(g, width - 160, 100, 150, 15, robot2.getHealth(), Robot.getMaxHealth(), Color.GREEN);
    }

    private void drawHealthBar(Graphics g, int x, int y, int width, int height, float currentHealth, float maxHealth, Color color) {
        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);

        float percentage = currentHealth / maxHealth;
        int filledWidth = (int) (width * percentage);

        g.setColor(color);
        g.fillRect(x, y, filledWidth, height);

        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }

    private void drawBackground(Graphics g , Level level , Camera camera) {
        float scale = 5.0f ;
        for(ParallaxObject obj : level.getParallaxObjects()) {
            float depth = obj.getDepth() ;

            float drawX = obj.getPosition().getVector2DX() - camera.getCameraX() * depth ;
            float drawY = obj.getPosition().getVector2DY() - camera.getCameraY() * depth ;
            int w = (int)(obj.getImage().getWidth() * scale);
            int h = (int)(obj.getImage().getHeight() * scale);

            g.drawImage(
                   obj.getImage(),
                    (int) drawX,
                    (int) drawY,
                    w,
                    h,
                    null
            );
        }
    }

    private void drawMenuBackground(Graphics g, Level level) {
        for (ParallaxObject obj : level.getParallaxObjects()) {

            // Draw full screen, no scaling, no camera
            g.drawImage(
                    obj.getImage(),
                    0,
                    0,
                    width,
                    height,
                    null
            );
        }
    }


    private void drawTile(Graphics g , Tile tile) {
        BufferedImage tex = tile.getTexture() ;

        int tileX = (int) tile.getPosition().getVector2DX() ;
        int tileY = (int) tile.getPosition().getVector2DY() ;

        int tileW = (int) tile.getTileWidth() ;
        int tileH = (int) tile.getTileHeight() ;

        int texW = tex.getWidth() ;
        int texH = tex.getHeight() ;

        for(int x = 0 ; x < tileW ; x += texW) {
            for(int y = 0 ; y < tileH ; y += texH) {
                int drawW = Math.min(texW , tileW - x) ;
                int drawH = Math.min(texH , tileH - y) ;

                g.drawImage(
                        tex,
                        tileX + x,
                        tileY + y,
                        tileX + x + drawW,
                        tileY + y + drawH,
                        0,
                        0,
                        drawW,
                        drawH,
                        null
                );
            }
        }
    }
}
