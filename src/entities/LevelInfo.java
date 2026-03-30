package entities;

import java.awt.image.BufferedImage;

public class LevelInfo {
    private String filePath ;
    private String title ;
    private BufferedImage previewImage ;

    public LevelInfo(String filePath , String title , BufferedImage previewImage) {
        this.filePath = filePath ;
        this.title = title ;
        this.previewImage = previewImage ;
    }

    public String getFilePath() { return filePath; }
    public String getTitle() { return title; }
    public BufferedImage getPreviewImage() { return previewImage; }
}
