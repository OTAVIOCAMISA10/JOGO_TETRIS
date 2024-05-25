import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.PaintEvent;
import java.util.logging.SocketHandler;

import javax.swing.JPanel;

public class Painel extends JPanel implements  Runnable{
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	final int FPS = 60;
	public static Som music = new Som ();
	public static Som se = new Som ();
	Thread gameThread;
	Gerenciador pm;
	
	
	public Painel () {
		
		this.setPreferredSize(new Dimension (WIDTH,HEIGHT));
		this.setBackground(Color.black);
		this.setLayout(null);
		
		this.addKeyListener(new Movimento());
		this.setFocusable(true);
		
		pm = new Gerenciador();
		
	}
	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
		
		music.play (0, true);
		music.loop();
	}
	
	@Override
	public void run () {
	
		// Loop 
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while (gameThread != null) {
			
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			
			if(delta >- 1) {
				update();
				repaint();
				delta --;
			}
		}
		
	}
	private void update() {
		
		if(Movimento.pausePressed == false && pm.gameOver == false) {
		   pm.update();
		
		}
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		  pm.draw(g2);
	}

}