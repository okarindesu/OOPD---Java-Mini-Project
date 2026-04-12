package physics;

import Weapons.Projectile;
import engine.GameOverContext;
import engine.GameState;
import engine.LevelSelectionContext;
import engine.StartScreenContext;
import entities.*;
import entities.Robot;
import utils.Vector2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class GameRenderer {
    private Canvas canvas ;
    private int width ;
    private int height ;

    private Font titleFont ;
    private Font uiFont ;
    private Font smallTitle , mediumTitle , bigTitle ;
    private Font smallUI , mediumUI , bigUI ;
    private final BufferedImage mainMenuBackground;

    private long shakeStartTime = 0;
    private long shakeDuration = 0;
    private int shakeIntensity = 0;
    private boolean isShaking = false;

    boolean isRedTint = false;
    long redTintStartTime = 0;
    long redTintDuration = 0;

    public GameRenderer(Canvas canvas , int width , int height) {
        this.canvas = canvas ;
        this.width = width ;
        this.height = height ;
        titleFont = loadFont("resources/fonts/p2p.ttf" , 20f) ;
        uiFont = loadFont("resources/fonts/sk_bo.ttf" , 20f) ;

        mainMenuBackground = loadBackgroundImage(
                "resources/backgrounds/menu_bg2.png",
                "/backgrounds/menu_bg2.png");

        smallTitle = titleFont.deriveFont(14f) ;
        mediumTitle = titleFont.deriveFont(24f) ;
        bigTitle = titleFont.deriveFont(40f) ;

        smallUI = uiFont.deriveFont(14f) ;
        mediumUI = uiFont.deriveFont(24f) ;
        bigUI = uiFont.deriveFont(40f) ;
    }

    private BufferedImage loadBackgroundImage(String filePath, String classpathPath) {
        try {
            File f = new File(filePath);
            if (f.isFile()) {
                return ImageIO.read(f);
            }
        } catch (Exception e) {
            System.out.println("Could not load background file: " + filePath);
            e.printStackTrace();
        }
        try (InputStream in = GameRenderer.class.getResourceAsStream(classpathPath)) {
            if (in != null) {
                return ImageIO.read(in);
            }
        } catch (Exception e) {
            System.out.println("Could not load background classpath: " + classpathPath);
            e.printStackTrace();
        }
        System.out.println("Background not found: " + filePath + " or " + classpathPath);
        return null;
    }

    private Font loadFont(String path, float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
            return font.deriveFont(size);
        } catch (Exception e) {
            System.out.println("Failed to load font: " + path);
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int) size); // fallback
        }
    }

    private void drawMainMenuBackground(Graphics2D g2, boolean gameOver) {
        int vw = canvas.getWidth();
        int vh = canvas.getHeight();
        if (vw <= 0 || vh <= 0) {
            vw = width;
            vh = height;
        }

        if (mainMenuBackground == null) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, vw, vh);
            return;
        }

        int iw = mainMenuBackground.getWidth();
        int ih = mainMenuBackground.getHeight();
        if (iw <= 0 || ih <= 0) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, vw, vh);
            return;
        }

        // Cover 16:9 viewport: scale up uniformly so the image fills vw×vh, then center (crop overflow).
        double scale = Math.max((double) vw / iw, (double) vh / ih);
        int dw = (int) Math.ceil(iw * scale);
        int dh = (int) Math.ceil(ih * scale);
        int dx = (vw - dw) / 2;
        int dy = (vh - dh) / 2;

        Object oldInterp = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Shape oldClip = g2.getClip();
        g2.setClip(0, 0, vw, vh);
        g2.drawImage(mainMenuBackground, dx, dy, dw, dh, null);
        g2.setClip(oldClip);

        if (oldInterp != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterp);
        }

        if (gameOver) {
            g2.setColor(new Color(150, 0, 0, 180));
            g2.fillRect(0, 0, vw, vh);
        } else {
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(0, 0, vw, vh);
        }
    }

    public void render(StartScreenContext startScreenContext , LevelSelectionContext levelSelectionContext , GameOverContext gameOverContext , Robot robot1 , Robot robot2 , Camera camera, PowerUpSystem powerUpSystem) {
        BufferStrategy bs = canvas.getBufferStrategy() ;
        if (bs == null) return ;
        Graphics g = bs.getDrawGraphics() ;

        GameState gameState = levelSelectionContext.getGameStateHandler().getGameState() ;
        switch(gameState) {
            case START_SCREEN_STATE:
                startScreenRender(g , startScreenContext) ;
                break;
            case LEVEL_SELECTION_STATE :
                levelSelectionRender(g , levelSelectionContext) ;
                break;
            case GAME_PLAYING_STATE:
                gameRender(g , robot1 , robot2 , levelSelectionContext.getLevel() , camera, powerUpSystem) ;
                break;
            case GAME_OVER_STATE:
                gameOverRender(g , gameOverContext);
                break;
        }
        g.dispose() ;
        bs.show() ;
    }

    public void startScreenRender(Graphics g, StartScreenContext context) {

        String[] options = context.getOptions();
        int selected = context.getSelectedIndex();

        Graphics2D g2 = (Graphics2D) g;

        // ===== BACKGROUND =====
        drawMainMenuBackground(g2, false);

        // ===== TITLE =====
        g.setFont(bigTitle);
        g.setColor(Color.WHITE);

        String title = "RoboWars";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (width - titleWidth) / 2, 100);

        // ===== SUBTITLE =====
        g.setFont(mediumUI);
        String subtitle = "Main Menu";
        int subWidth = g.getFontMetrics().stringWidth(subtitle);
        g.drawString(subtitle, (width - subWidth) / 2, 140);

        // ===== MENU OPTIONS =====
        int btnW = 300;
        int btnH = 70;
        int centerX = width / 2;
        int startY = height / 2 - 40;
        int spacing = 100;

        for (int i = 0; i < options.length; i++) {

            int y = startY + i * spacing;
            drawPixelButton(
                    g2,
                    centerX - btnW / 2,
                    y,
                    btnW,
                    btnH,
                    options[i],
                    i == selected
            );
        }

        // ===== CONTROLS (BOXED LIKE LEVEL SCREEN) =====
        g.setFont(smallUI);

        String label = "SELECT :";
        String movement = "UP / DOWN";
        String plus = "+";
        String enter = "ENTER";

        int padding = 12;
        int boxHeightCtrl = 32;
        int gap = 15;

        FontMetrics fm = g.getFontMetrics();

        int labelWidth = fm.stringWidth(label);

        // Widths
        int moveW = fm.stringWidth(movement) + padding * 2;
        int plusW = fm.stringWidth(plus) + padding * 2;
        int enterW = fm.stringWidth(enter) + padding * 2;

        int totalWidth = labelWidth + 20 + moveW + gap + plusW + gap + enterW;

        int x = (width - totalWidth) / 2;
        int y = height - 70;

        // Label
        g.setColor(Color.WHITE);
        g.drawString(label, x, y + 22);
        x += labelWidth + 20;

        // ===== MOVEMENT BOX =====
        g.drawRect(x, y, moveW, boxHeightCtrl);
        int moveTextX = x + (moveW - fm.stringWidth(movement)) / 2;
        g.drawString(movement, moveTextX, y + 22);

        x += moveW + gap;

        // ===== PLUS BOX =====
        g.drawRect(x, y, plusW, boxHeightCtrl);
        int plusTextX = x + (plusW - fm.stringWidth(plus)) / 2;
        g.drawString(plus, plusTextX, y + 22);

        x += plusW + gap;

        // ===== ENTER BOX =====
        g.drawRect(x, y, enterW, boxHeightCtrl);
        int enterTextX = x + (enterW - fm.stringWidth(enter)) / 2;
        g.drawString(enter, enterTextX, y + 22);
    }

    private void gameOverRender(Graphics g, GameOverContext context) {
        String[] options = context.getOptions();
        int selected = context.getSelectedIndex();

        Graphics2D g2 = (Graphics2D) g;

        drawMainMenuBackground(g2, true);

        g.setFont(bigTitle);
        g.setColor(Color.WHITE);

        String title = "Game Over";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (width - titleWidth) / 2, 120);

        String winner = context.getWinnerText();
        if (!winner.isEmpty()) {
            g.setFont(mediumUI);
            int winnerWidth = g.getFontMetrics().stringWidth(winner);
            g.drawString(winner, (width - winnerWidth) / 2, 170);
        }

        int btnW = 320;
        int btnH = 70;
        int centerX = width / 2;
        int startY = height / 2 - 40;
        int spacing = 100;

        for (int i = 0; i < options.length; i++) {
            int y = startY + i * spacing;
            drawPixelButton(
                    g2,
                    centerX - btnW / 2,
                    y,
                    btnW,
                    btnH,
                    options[i],
                    i == selected
            );
        }

        g.setFont(smallUI);
        String label = "SELECT :";
        String movement = "UP / DOWN";
        String plus = "+";
        String enter = "ENTER";

        int padding = 12;
        int boxHeightCtrl = 32;
        int gap = 15;

        FontMetrics fm = g.getFontMetrics();

        int labelWidth = fm.stringWidth(label);

        int moveW = fm.stringWidth(movement) + padding * 2;
        int plusW = fm.stringWidth(plus) + padding * 2;
        int enterW = fm.stringWidth(enter) + padding * 2;

        int totalWidth = labelWidth + 20 + moveW + gap + plusW + gap + enterW;

        int x = (width - totalWidth) / 2;
        int y = height - 70;

        g.setColor(Color.WHITE);
        g.drawString(label, x, y + 22);
        x += labelWidth + 20;

        g.drawRect(x, y, moveW, boxHeightCtrl);
        int moveTextX = x + (moveW - fm.stringWidth(movement)) / 2;
        g.drawString(movement, moveTextX, y + 22);

        x += moveW + gap;

        g.drawRect(x, y, plusW, boxHeightCtrl);
        int plusTextX = x + (plusW - fm.stringWidth(plus)) / 2;
        g.drawString(plus, plusTextX, y + 22);

        x += plusW + gap;

        g.drawRect(x, y, enterW, boxHeightCtrl);
        int enterTextX = x + (enterW - fm.stringWidth(enter)) / 2;
        g.drawString(enter, enterTextX, y + 22);

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - context.startTime;

        if(elapsed < context.fadeDuration) {
            double progress = (double) elapsed / context.fadeDuration;

            // fade from black → transparent
            int alpha = (int)(255 * (1 - progress));
            alpha = Math.max(0, Math.min(255, alpha));

            g.setColor(new Color(0, 0, 0, alpha));
            g.fillRect(0, 0, width, height);
        }
    }

    private void drawPixelButton(Graphics2D g, int x, int y, int w, int h,
                                 String text, boolean selected) {

        // ===== COLORS =====
        Color base = selected ? new Color(170, 190, 210) : new Color(140, 160, 180);
        Color borderDark = new Color(70, 90, 110);
        Color borderLight = new Color(220, 230, 240);

        // ===== SMALL ANIMATION (selected lift) =====
        if (selected) {
            y -= 3; // subtle "pop up" effect
        }

        // =========================
        // 🧱 BUTTON BODY
        // =========================
        g.setColor(base);
        g.fillRect(x, y, w, h);

        // =========================
        // ✨ PIXEL BORDER (top/left)
        // =========================
        g.setColor(borderLight);
        g.fillRect(x, y, w, 4);
        g.fillRect(x, y, 4, h);

        // =========================
        // 🌑 SHADOW BORDER (bottom/right)
        // =========================
        g.setColor(borderDark);
        g.fillRect(x, y + h - 4, w, 4);
        g.fillRect(x + w - 4, y, 4, h);

        // =========================
        // 🔥 EXTRA GLOW IF SELECTED
        // =========================
        if (selected) {
            g.setColor(new Color(255, 255, 255, 40));
            g.fillRect(x, y, w, h);
        }

        // =========================
        // 🎯 TEXT
        // =========================
        g.setFont(mediumUI);

        FontMetrics fm = g.getFontMetrics();
        int textW = fm.stringWidth(text);

        int textX = x + (w - textW) / 2;
        int textY = y + (h / 2) + 10;

        // Shadow
        g.setColor(new Color(0, 0, 0, 120));
        g.drawString(text, textX + 2, textY + 2);

        // Main text
        g.setColor(new Color(40, 50, 60));
        g.drawString(text, textX, textY);
    }

    private void levelSelectionRender(Graphics g , LevelSelectionContext levelSelectionContext) {
        LevelInfo[] levels = levelSelectionContext.getLevels();
        int selectedLevel = levelSelectionContext.getSelectedLevel();

        Graphics2D g2 = (Graphics2D) g;

        // ===== BACKGROUND =====
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // ===== TITLE =====
        g.setFont(bigTitle);
        g.setColor(Color.WHITE);

        String title = "RoboWars";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (width - titleWidth) / 2, 80);

        g.setFont(mediumUI);
        String subtitle = "Select Terrain";
        int subWidth = g.getFontMetrics().stringWidth(subtitle);
        g.drawString(subtitle, (width - subWidth) / 2, 120);

        // ===== GRID CONFIG (720p FRIENDLY) =====
        int columns = 4;

        int spacingX = 30;
        int spacingY = 50;

        int totalSpacingX = (columns - 1) * spacingX;

        int cardWidth = (width - 160 - totalSpacingX) / columns;
        int cardHeight = (int)(cardWidth * 0.6f);

        int startX = (width - (columns * cardWidth + totalSpacingX)) / 2;
        int startY = 160;

        // ===== LEVEL GRID =====
        for (int i = 0; i < levels.length; i++) {

            int row = i / columns;
            int col = i % columns;

            int x = startX + col * (cardWidth + spacingX);
            int y = startY + row * (cardHeight + spacingY);

            LevelInfo lvl = levels[i];

            if (lvl.getPreviewImage() != null) {
                g.drawImage(lvl.getPreviewImage(), x, y, cardWidth, cardHeight, null);
            }

            // Highlight selected
            if (i == selectedLevel) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(4));

                // Outer glow
                g2.drawRect(x - 3, y - 3, cardWidth + 6, cardHeight + 6);

                // Light overlay
                g2.setColor(new Color(255, 255, 0, 40));
                g2.fillRect(x, y, cardWidth, cardHeight);
            }

            // Border
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(x, y, cardWidth, cardHeight);

            // Level title
            g.setFont(mediumUI);
            FontMetrics fm = g.getFontMetrics();

            String titleText = lvl.getTitle();
            int textWidth = fm.stringWidth(titleText);

// Center text relative to card
            int textX = x + (cardWidth - textWidth) / 2;
            int textY = y + cardHeight + 25;

            g.drawString(titleText, textX, textY);
        }

        // ===== CONTROLS =====
        g.setFont(smallUI);

        String label = "SELECT :";
        String movement = "UP / DOWN / LEFT / RIGHT";
        String plus = "+";
        String enter = "ENTER";

        int padding = 12;
        int boxHeight = 32;
        int gap = 15;

        FontMetrics fm = g.getFontMetrics();

        int labelWidth = fm.stringWidth(label);

// Calculate widths
        int moveW = fm.stringWidth(movement) + padding * 2;
        int plusW = fm.stringWidth(plus) + padding * 2;
        int enterW = fm.stringWidth(enter) + padding * 2;

// Total width
        int totalWidth = labelWidth + 20 + moveW + gap + plusW + gap + enterW;

// Center horizontally
        int x = (width - totalWidth) / 2;
        int y = height - 70;

// Draw label
        g.setColor(Color.WHITE);
        g.drawString(label, x, y + 22);
        x += labelWidth + 20;

// ===== MOVEMENT BOX =====
        g.drawRect(x, y, moveW, boxHeight);

// Center text
        int moveTextX = x + (moveW - fm.stringWidth(movement)) / 2;
        g.drawString(movement, moveTextX, y + 22);

        x += moveW + gap;

// ===== PLUS BOX =====
        g.drawRect(x, y, plusW, boxHeight);
        int plusTextX = x + (plusW - fm.stringWidth(plus)) / 2;
        g.drawString(plus, plusTextX, y + 22);

        x += plusW + gap;

        g.drawRect(x, y, enterW, boxHeight);
        int enterTextX = x + (enterW - fm.stringWidth(enter)) / 2;
        g.drawString(enter, enterTextX, y + 22);
    }

    private void gameRender(Graphics g , Robot robot1 , Robot robot2 , Level level , Camera camera, PowerUpSystem powerUpSystem) {
        int offsetX = 0;
        int offsetY = 0;

        if(isShaking) {
            long currentTime = System.currentTimeMillis();

            if(currentTime - shakeStartTime < shakeDuration) {
                offsetX = (int)(Math.random() * shakeIntensity * 2) - shakeIntensity;
                offsetY = (int)(Math.random() * shakeIntensity * 2) - shakeIntensity;
            } else {
                isShaking = false;
            }
        }

        camera.setCameraX(robot1.getPosition().getVector2DX() - width / 2) ;
        camera.setCameraY(robot1.getPosition().getVector2DY() - height / 2) ;

        g.setColor(Color.BLACK) ;
        g.fillRect(0 , 0 , width , height) ;

        g.translate(offsetX, offsetY);
        drawLevel(g , level , camera) ;
        drawRobot(g , robot1 , Color.RED) ;
        drawRobot(g , robot2 , Color.CYAN) ;
        drawProjectiles(g , robot1, Color.RED) ;
        drawProjectiles(g , robot2, Color.GREEN) ;
        drawPowerUps(g, powerUpSystem.getPowerUps());
        g.translate(-offsetX, -offsetY);

        long currentTime = System.currentTimeMillis();
        if(isRedTint) {
            long elapsed = currentTime - redTintStartTime;

            if(elapsed < redTintDuration) {
                double progress = (double) elapsed / redTintDuration;

                int alpha = (int)(150 * (1 - progress)); // max 150 → 0

                alpha = Math.max(0, Math.min(150, alpha));

                g.setColor(new Color(255, 0, 0, alpha));
                g.fillRect(0, 0, width, height);
            } else {
                isRedTint = false;
            }
        }

        drawUI(g , robot1 , robot2) ;

        g.setColor(Color.WHITE) ;
        g.drawString("RoboWars" , 10 , 20) ;
    }

    private void drawPowerUps(Graphics g, List<PowerUp> powerUps) {
        for (PowerUp powerUp : powerUps) {
            int x = (int) powerUp.getPosition().getVector2DX();
            int y = (int) powerUp.getPosition().getVector2DY();
            int w = (int) powerUp.getWidth();
            int h = (int) powerUp.getHeight();

            if (powerUp.getType() == PowerUpType.SPEED_BOOST) {
                g.setColor(new Color(66, 245, 164)); // Green for speed
            } else {
                g.setColor(new Color(255, 170, 0)); // Orange for damage
            }

            g.fillOval(x, y, w, h);
            g.setColor(Color.WHITE);
            g.drawOval(x, y, w, h);
        }
    }

    private void drawProjectiles(Graphics g , Robot robot, Color color) {
        List<Projectile> p1 = robot.getHandGun().getProjectileSystem().getProjectiles() ;

        for(Projectile p : p1) {
            if(p.getHasHitTile() || p.getHasHitRobot() || p.getHasReachedBoundary()) continue ;

            int pX = (int) p.getPosition().getVector2DX() ;
            int pY = (int) p.getPosition().getVector2DY() ;

            g.setColor(color);

            g.fillOval(pX , pY , (int) p.getProjectileRadius() , (int) p.getProjectileRadius()) ;
        }
    }

    private void drawRobot(Graphics g , Robot robot , Color color) {
        if(robot.gameOverforRobo) return ;
        g.setColor(color) ;
        Vector2D vec = robot.getPosition() ;
        float roboWidth = robot.getRoboWidth() ;
        float roboHeight = robot.getRoboHeight() ;

        BufferedImage sprite = robot.getAnimationManager().getCurrentFrame() ;

        boolean isHit = false ;
        if(robot.isHit && (System.currentTimeMillis() - robot.hitTime < robot.HIT_DURATION)) isHit = true ;

        if(sprite != null) {
            if(isHit) sprite = toGrayScale(sprite) ;
            g.drawImage(sprite , (int) vec.getVector2DX() , (int) vec.getVector2DY() , (int) roboWidth , (int) roboHeight , null) ;
        }
        else {
            if (isHit) g.setColor(Color.DARK_GRAY);
            else g.setColor(color);
            g.fillRect((int) vec.getVector2DX(), (int) vec.getVector2DY(), (int) roboWidth, (int) roboHeight);
        }
    }

    private BufferedImage toGrayScale(BufferedImage image) {
        BufferedImage grayscale = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);

                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                int gray = (int)(0.299 * red + 0.587 * green + 0.114 * blue);

                int grayscaleArgb = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                grayscale.setRGB(x, y, grayscaleArgb);
            }
        }

        return grayscale;
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

        // Player 1 Stats
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P1", 10, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", robot1.getHealth()) + "/" + (int)Robot.getMaxHealth(), 10, 75);
        g.drawString("Lives: " + robot1.getLives(), 10, 90);

        drawHealthBar(g, 10, 100, 150, 15, robot1.getHealth(), Robot.getMaxHealth(), Color.RED);

        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P2", width - 50, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", robot2.getHealth()) + "/" + (int)Robot.getMaxHealth(), width - 180, 75);
        g.drawString("Lives: " + robot2.getLives(), width - 180, 90);

        drawHealthBar(g, width - 160, 100, 150, 15, robot2.getHealth(), Robot.getMaxHealth(), Color.CYAN);
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
        float scale = 5.0f ; // Temporary Change
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

    public void triggerScreenShake(long duration, int intensity) {
        isShaking = true;
        shakeStartTime = System.currentTimeMillis();
        shakeDuration = duration;
        shakeIntensity = intensity;
    }

    public void triggerRedTint(long duration) {
        isRedTint = true;
        redTintStartTime = System.currentTimeMillis();
        redTintDuration = duration;
    }
}
