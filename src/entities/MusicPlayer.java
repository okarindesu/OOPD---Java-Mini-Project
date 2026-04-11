package entities;

import javax.sound.sampled.*;
import java.io.File;

public class MusicPlayer {

    private static Thread musicThread;
    private static volatile boolean playing = false;

    private static String currentTrack = "";
    private static FloatControl volumeControl;

    // 🎚 default volume (0.0f → 1.0f)
    private static float currentVolume = 0.3f;

    // ===== PLAY MUSIC (LOOPING) =====
    public static void play(String path) {

        // ✅ prevent restarting same track
        if (playing && path.equals(currentTrack)) return;

        stop();

        playing = true;
        currentTrack = path;

        musicThread = new Thread(() -> {
            while (playing) {
                try {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
                    AudioFormat format = ais.getFormat();

                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

                    line.open(format);

                    // 🎚 SET VOLUME CONTROL
                    if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                        applyVolume(); // apply current volume
                    }

                    line.start();

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while (playing && (bytesRead = ais.read(buffer)) != -1) {
                        line.write(buffer, 0, bytesRead);
                    }

                    line.drain();
                    line.close();
                    ais.close();

                    // 🔁 loops automatically because of while(playing)

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        musicThread.start();
    }

    // ===== STOP MUSIC =====
    public static void stop() {
        playing = false;

        if (musicThread != null) {
            try {
                musicThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // ===== SET VOLUME =====
    public static void setVolume(float volume) {
        // clamp
        if (volume < 0.01f) volume = 0.01f;
        if (volume > 1.0f) volume = 1.0f;

        currentVolume = volume;
        applyVolume();
    }

    // ===== APPLY VOLUME INTERNALLY =====
    private static void applyVolume() {
        if (volumeControl == null) return;

        float dB = (float) (Math.log10(currentVolume) * 20.0);
        volumeControl.setValue(dB);
    }
}