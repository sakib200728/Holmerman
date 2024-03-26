package pacman;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import java.io.File;

public class Pacman extends JFrame {

    public Pacman() {
        add(new Model());
        playMusic();
    }

    private void playMusic() {
        File audioFile = new File("path");
        AudioInputStream streamAudio = AudioSystem.getAudioInputStream(audioFile);
        Clip audioClip = AudioSystem.getClip();
        audioClip.open(new AudioInputStream());
        audioClip.loop(audioClip.LOOP_CONTINUOUSLY);
        audioClip.start();
    }


    public static void main(String[] args) {
        Pacman pac = new Pacman();
        pac.setVisible(true);
        pac.setTitle("Pacman");
        pac.setSize(380,420);
        pac.setDefaultCloseOperation(EXIT_ON_CLOSE);
        pac.setLocationRelativeTo(null);

    }

}