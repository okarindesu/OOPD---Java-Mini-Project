package entities;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    // Store file paths instead of Clips
    private static final Map<String, String> soundPaths = new HashMap<>();

    // Global SFX volume (0.0f → 1.0f)
    private static float sfxVolume = 1.0f;

    // ===== LOAD =====
    public static void load(String name, String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Sound not found: " + path);
            return;
        }
        soundPaths.put(name, path);
    }

    // ===== PLAY (MULTI-INSTANCE SAFE) =====
    public static void play(String name) {
        String path = soundPaths.get(name);
        if (path == null) return;

        new Thread(() -> {
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
                Clip clip = AudioSystem.getClip();
                clip.open(ais);

                // 🎚 Apply volume
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                    float volume = Math.max(0.01f, sfxVolume);
                    float dB = (float) (Math.log10(volume) * 20.0);
                    control.setValue(dB);
                }

                clip.start();

                // 🧹 Auto close after playback finishes
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ===== SET GLOBAL SFX VOLUME =====
    public static void setVolume(float volume) {
        if (volume < 0.01f) volume = 0.01f;
        if (volume > 1.0f) volume = 1.0f;

        sfxVolume = volume;
    }
}
