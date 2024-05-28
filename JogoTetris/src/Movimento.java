import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Movimento implements KeyListener {
	
	public static boolean upPressed, downPressed, leftPressed, rightPressed, pausePressed;

	public Movimento(Gerenciador gm) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		if (code == KeyEvent.VK_W) {
			upPressed = true;
		}
		if (code == KeyEvent.VK_A) {
			leftPressed = true;
		}
		if (code == KeyEvent.VK_S) {
			downPressed = true;
		}
		if (code == KeyEvent.VK_D) {
			rightPressed = true;
		}
		if (code == KeyEvent.VK_SPACE) {
			if (pausePressed) {
				pausePressed = false;
				Painel.music.play(0, true);
				Painel.music.loop();
			} else {
				pausePressed = true;
				Painel.music.stop();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();

		if (code == KeyEvent.VK_W) {
			upPressed = false;
		}
		if (code == KeyEvent.VK_A) {
			leftPressed = false;
		}
		if (code == KeyEvent.VK_S) {
			downPressed = false;
		}
		if (code == KeyEvent.VK_D) {
			rightPressed = false;
		}
	}
}
