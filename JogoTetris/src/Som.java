

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class Som {
	
	private Clip musicClip;
	private URL urls[] = new URL[5]; 
	
	public Som() {
		urls[0] = getClass().getResource("Sam River - Korobeiniki - Piano Version.mp3");
		urls[1] = getClass().getResource("delete line.wav");
		urls[2] = getClass().getResource("gameover.wav");
		urls[3] = getClass().getResource("rotation.wav");
		urls[4] = getClass().getResource("touch floor.wav");
	}
	
	public void play(int i, boolean music) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(urls[i]);
			Clip clip = AudioSystem.getClip();
			
			if (music) {
				musicClip = clip;
			}
			
			clip.open(ais);
			clip.addLineListener(new LineListener() {
				@Override
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP) {
						clip.close();
					}
				}
			});
			clip.start();
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	public void loop() {
		if (musicClip != null) {
			musicClip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	public void stop() {
		if (musicClip != null) {
			musicClip.stop();
		}
	}
}