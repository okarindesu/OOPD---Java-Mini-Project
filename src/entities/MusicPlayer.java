package entities;

import javax.sound.sampled.*;
import java.io.File;

import exceptions.*;

public class MusicPlayer {

    private static Thread musicThread;
    private static volatile boolean playing = false;

    private static String currentTrack = "";
    private static FloatControl volumeControl;

    private static float currentVolume = 0.3f;

    public static void play(String path) {

        if (path == null || path.isEmpty()) {
            throw new AudioFileException("Audio path is null or empty");
        }

        if (playing && path.equals(currentTrack)) return;

        stop();

        File file = new File(path);
        if (!file.exists()) {
            throw new AudioFileException("Audio file not found: " + path);
        }

        playing = true;
        currentTrack = path;

        musicThread = new Thread(() -> {
            while (playing) {
                try {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                    AudioFormat format = ais.getFormat();

                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

                    if (!AudioSystem.isLineSupported(info)) {
                        throw new AudioPlaybackException("Audio line not supported for format");
                    }

                    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                    line.open(format);

                    if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                        applyVolume();
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

                } catch (UnsupportedAudioFileException e) {
                    throw new AudioFileException("Unsupported audio format: " + path);

                } catch (LineUnavailableException e) {
                    throw new AudioPlaybackException("Audio line unavailable");

                } catch (Exception e) {
                    throw new AudioPlaybackException("Error during audio playback: " + e.getMessage());
                }
            }
        });

        musicThread.start();
    }

    public static void stop() {
        playing = false;

        if (musicThread != null) {
            try {
                musicThread.join();
            } catch (InterruptedException e) {
                throw new AudioPlaybackException("Music thread interrupted");
            }
        }
    }

    public static void setVolume(float volume) {

        if (volume < 0.0f || volume > 1.0f) {
            throw new InvalidVolumeException("Volume must be between 0.0 and 1.0");
        }

        if (volume < 0.01f) volume = 0.01f;

        currentVolume = volume;
        applyVolume();
    }

    private static void applyVolume() {
        if (volumeControl == null) return;

        float dB = (float) (Math.log10(currentVolume) * 20.0);
        volumeControl.setValue(dB);
    }
}