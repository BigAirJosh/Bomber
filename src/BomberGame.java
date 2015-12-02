import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BomberGame extends JFrame implements BombHitHandler {

	private static final long serialVersionUID = 1L;

	private JPanel gamePanel;
	private Plane plane1, plane2;
	
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 600;
	private static final int TARGET_SIZE = 60;
	private static final int HALF_TARGET_SIZE = TARGET_SIZE / 2;
	private static final int QUATER_TARGET_SIZE = TARGET_SIZE / 4;
	
	private static Image hillsImage;
	private List<Cloud> clouds;
	
	static {
		try {
			hillsImage = ImageIO.read(new File("./images/hills1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean showHelp = true;
	private static final String INSTRUCTIONS = "Drop bombs on the target, the highest score wins. r - reset, h - show/hide help";
	private static final String P1_INSTRUCTIONS[] = 
		{"Plane 1 Controls:",
			"w - Power up",
			"s - Power down",
			"a - Rotate left",
			"d - Rotate right",
			"SPACE - Drop bomb"};
	
	private static final String P2_INSTRUCTIONS[] = 
		{"Plane 2 Controls:",
			"i - Power up",
			"k - Power down",
			"j - Rotate left",
			"l - Rotate right",
			"RETURN - Drop bomb"};
	
	private int targetX;
		
	public BomberGame() {
		super("Bomber");
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setLayout(new BorderLayout());
		init();
		initClouds();
		plane1 = new Plane(this, true);
		plane2 = new Plane(this, false);
		nextTargetPosition();
	}
	
	private void nextTargetPosition() {
		targetX = 100 + (int)(Math.random() * (WIDTH - 200));
	}
	
	@Override
	public boolean bomhBit(int x, int y) {
		int distance = x - targetX;
		if(Math.abs(distance) < TARGET_SIZE) {
			nextTargetPosition();
			return true;
		}
		return false;
	}

	private void init() {

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				System.exit(0);
			}
		});
		
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_SPACE) {
					plane1.dropBomb();
				}
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					plane2.dropBomb();
				}
				if(e.getKeyChar() == 'r') {
					nextTargetPosition();
					plane1.reset();
					plane2.reset();
				}
				if(e.getKeyChar() == 'h') {
					showHelp = !showHelp;
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyChar() == 'w') {
					plane1.setIncPower(false);
				}
				if(e.getKeyChar() == 's') {
					plane1.setDecPower(false);
				}
				if(e.getKeyChar() == 'd') {
					plane1.setIncAngle(false);
				}
				if(e.getKeyChar() == 'a') {
					plane1.setDecAngle(false);
				}
				if(e.getKeyChar() == 'i') {
					plane2.setIncPower(false);
				}
				if(e.getKeyChar() == 'k') {
					plane2.setDecPower(false);
				}
				if(e.getKeyChar() == 'l') {
					plane2.setIncAngle(false);
				}
				if(e.getKeyChar() == 'j') {
					plane2.setDecAngle(false);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == 'w') {
					plane1.setIncPower(true);
				}
				if(e.getKeyChar() == 's') {
					plane1.setDecPower(true);
				}
				if(e.getKeyChar() == 'd') {
					plane1.setIncAngle(true);
				}
				if(e.getKeyChar() == 'a') {
					plane1.setDecAngle(true);
				}
				if(e.getKeyChar() == 'i') {
					plane2.setIncPower(true);
				}
				if(e.getKeyChar() == 'k') {
					plane2.setDecPower(true);
				}
				if(e.getKeyChar() == 'l') {
					plane2.setIncAngle(true);
				}
				if(e.getKeyChar() == 'j') {
					plane2.setDecAngle(true);
				}
			}
		});
				
		gamePanel = new JPanel(true) {
			private static final long serialVersionUID = -546325419638406681L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				refresh(g);
			}
		};

		add(gamePanel, BorderLayout.CENTER);
		
		Thread gameThread = new Thread() {
			@Override
			public void run() {
				super.run();
				while(true) {
					gamePanel.repaint();
					try {
						sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		gameThread.start();
	}
	
	private void initClouds() {
		clouds = new ArrayList<Cloud>();
		int count = 1 + (int)(Math.random() * 3);
		for(int n = 0; n < count; n++) {
			clouds.add(new Cloud());
		}
	}
	
	public void refresh(Graphics g) {
		Rectangle rect = g.getClipBounds();
		
		// sky
		g.setColor(new Color(186, 230, 237));
		g.fillRect(0, 0, rect.width, rect.height);
		
		// hills
		g.drawImage(hillsImage, 0, 0, null);
		
		// runway
		g.setColor(Color.lightGray);
		g.fillRect(0, rect.height - 10, rect.width, 10);
		
		// target
		g.setColor(new Color(219, 32, 32));
		g.fillRect(targetX - HALF_TARGET_SIZE, rect.height - 10, TARGET_SIZE, 10);
		
		g.setColor(Color.white);
		g.fillRect(targetX - QUATER_TARGET_SIZE - 3, rect.height - 10, 6, 10);
		g.fillRect(targetX - 3, rect.height - 10, 6, 10);
		g.fillRect(targetX + QUATER_TARGET_SIZE - 3, rect.height - 10, 6, 10);
		
		// plane and bombs
		if(plane1 != null)
			plane1.paint(g);
		if(plane2 != null)
			plane2.paint(g);
		
		// clouds
		for(Cloud cloud : clouds)
			cloud.paint(g);
		
		// score and ammo
		g.setColor(Color.black);
		g.drawString("Ammo: " + plane1.getAmmo(), 10, 25);
		g.drawString("Score: " + plane1.getScore(), 10, 40);
		
		g.drawString("Ammo: " + plane2.getAmmo(), WIDTH - 110, 25);
		g.drawString("Score: " + plane2.getScore(), WIDTH - 110, 40);

		//instructions
		g.drawString(INSTRUCTIONS, WIDTH / 2 - 250, 25);
		
		if(showHelp) {
			for(int n = 0; n < P1_INSTRUCTIONS.length; n++)
				g.drawString(P1_INSTRUCTIONS[n], 10, 75 + (15 * n));
			
			for(int n = 0; n < P2_INSTRUCTIONS.length; n++)
				g.drawString(P2_INSTRUCTIONS[n], WIDTH - 150, 75 + (15 * n));
		}
	}

	public static void main(String[] args) {
		BomberGame sc = new BomberGame();
		sc.setVisible(true);
	}

}
