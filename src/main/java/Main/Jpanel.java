package Main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import Entity.Player;
import tile.TileManager;

public class Jpanel extends JPanel implements Runnable{
	
	// In your Jpanel class
	public final int originalTileSize = 16; // Native size of your tiles
	public final int scale = 3; // How much to scale up for display
	public final int tileSize = originalTileSize * scale; // 48 pixels

	public final int maxScreenCol = 16;
	public final int maxScreenRow = 12;
	public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
	public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

	// World settings
	public final int maxWorldCol = 50;
	public final int maxWorldRow = 50;
	public final int worldWidth = tileSize * maxWorldCol;  // 48 * 50 = 2400
    public final int worldHeight = tileSize * maxWorldRow; // 48 * 50 = 2400
	
	
	TileManager tileM = new TileManager(this);
	KeyHandler keyH = new KeyHandler();
	public CollisionChecker cChecker = new CollisionChecker(this);
	public Player player = new Player(this, keyH);
	Thread gameThread;
	int FPS = 60;
	
	
	public Jpanel() {
		this.setPreferredSize(new Dimension (screenWidth,screenHeight));
		this.setBackground(Color.decode("#57CEEB"));
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
	}
	
	public void gameStart() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
		
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			
			if (delta >= 1) { 
			    update();
			    repaint();
			    delta--;
			}
			
		
		}
		
	}
	
	public void update() {
		
		player.update();
		
	}
	 public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D) g;
	        player.draw(g2);
	        tileM.draw(g2);
	        
	        cChecker.drawCollisionAreas(g2); // Debug
	        g2.dispose();
	    }
}