package pacman;

import javax.sound.sampled.*;
import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;

public class Pacman extends JFrame {

    public Pacman() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        add(new Model());
        playMusic();
    }

    private void playMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File audioFile = new File("pacman_beginning.wav");
        AudioInputStream streamAudio = AudioSystem.getAudioInputStream(audioFile);
        Clip audioClip = AudioSystem.getClip();
        audioClip.open(streamAudio);
        audioClip.loop(audioClip.LOOP_CONTINUOUSLY);
        audioClip.start();
    }


    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        Pacman pac = new Pacman();
        pac.setVisible(true);
        pac.setTitle("Pacman");
        pac.setSize(380,420);
        pac.setDefaultCloseOperation(EXIT_ON_CLOSE);
        pac.setLocationRelativeTo(null);

    }

}