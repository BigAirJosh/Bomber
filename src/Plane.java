import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Plane implements BombHitHandler {
	
	private final static double MAX_POWER = 15;
	private final static double POWER_STEP = 1;
	private final static double ANGLE_STEP = 0.1;
	private final static double GRAVITY = 15;
	private final static double TERMINAL_VELOCITY = 15;	
	private static final int EXPLOSION_SIZE = 256 / 4;
	private static final int EXPLOSION_RADIUS = EXPLOSION_SIZE / 2;
	private static final int MAX_AMMO = 10;
	
	private double x, y, dx, dy, power, angle, speed;
	private Image image;
	private boolean crash, crashed;
	private int crashPos = 0;
	
	private static Image explosionImage;
	
	static {
		try {
			explosionImage = ImageIO.read(new File("./images/explosion1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean incPower, decPower, incAngle, decAngle;
	
	private List<Bomb> bombs;
	
	private int ammo = MAX_AMMO;
	private int score = 0;
	
	private Thread planeThread;
	private BombHitHandler bombHitHandler;
	private boolean right;
	
	public Plane(BombHitHandler bombHitHandler, boolean right) {
		this.bombHitHandler = bombHitHandler;
		this.right = right;
		x = y = Integer.MAX_VALUE;
		power = 0d;
		angle = Math.toRadians(0d);
		try {
			image = ImageIO.read(new File("./images/plane" + (right ? "3" : "4") + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		bombs = new ArrayList<Bomb>();
		initPlaneThread();
	}
	
	public void reset() {
		crash = false; 
		crashed = false;
		crashPos = 0;
		power = speed = 0;
		angle = 0;
		score = 0;
		ammo = MAX_AMMO;
		dx = 0;
		dy = 0;
		x = y = Integer.MAX_VALUE;
	}
	
	private void initPlaneThread() {
		planeThread = new Thread() {
			@Override
			public void run() {
				super.run();
				while(true) {
					
					if(incPower)
						power = power < MAX_POWER ? power + POWER_STEP : MAX_POWER;
					else if(decPower)
						power = power > 0 ? power - POWER_STEP : 0;
						
					if(speed > 2.5) {
						if(incAngle)
							angle += ANGLE_STEP;
						else if(decAngle)
							angle -= ANGLE_STEP;
					}
					try {
						sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		planeThread.start();
	}
	
	public void dropBomb() {
		if(ammo > 0 && !crash) {
			Bomb bomb = new Bomb(right, this, x, y + 5, dx, dy);
			bombs.add(bomb);
			ammo--;
		}
	}
	
	@Override
	public boolean bomhBit(int x, int y) {
		int xDist = Math.abs((int)this.x - x);
		int yDist = Math.abs((int)this.y - y);
		double dist = Math.sqrt((xDist *xDist) + (yDist * yDist));
		if(dist < 50)
			crash = true;
		
		if(bombHitHandler.bomhBit(x, y))
			score++;
		
		return true;
	}
	
	public int getAmmo() {
		return ammo;
	}

	public void paint(Graphics g) {
		Rectangle rect = g.getClipBounds();
		Graphics2D g2d = (Graphics2D) g;
		computePosition(rect);	
		if(!crash) {
			AffineTransform at = new AffineTransform();
			at.translate(x, y);
	        at.rotate(angle);
	        at.translate(-image.getWidth(null) / 2, -image.getHeight(null) / 2);
	        
	        g2d.drawImage(image, at, null);
		} else if(!crashed) {
			dx = dy = power = 0;
			int pos = crashPos / 4;
			int a = pos * EXPLOSION_SIZE;
			int b = pos * EXPLOSION_SIZE;
			b += EXPLOSION_SIZE;
			
	        g2d.drawImage(explosionImage, 
	        		(int)x - EXPLOSION_RADIUS,
	        		(int)y - EXPLOSION_RADIUS, 
	        		(int)x + EXPLOSION_RADIUS,
	        		(int)y + EXPLOSION_RADIUS, 
	        		a,
	        		a,
	        		b, b, null);
		}
        for(Bomb bomb : bombs) {
        	bomb.paint(g);
        }
	}
	
	private void computePosition(Rectangle rect) {
		// init the position first time when we have the screen bounds
		if(y == Integer.MAX_VALUE) {
			y = rect.height - 25;
			x = right ? 50 : BomberGame.WIDTH - 50;
		}
		
		double newDx = Math.cos(angle) * power;
		double newDy = Math.sin(angle) * power;
		dx = right ? newDx : -newDx;
		dy = right ? newDy : -newDy;
		
		speed = Math.sqrt((dx * dx) + (dy * dy));
		
		if(y < 10) {
			dx *= 0.5;
			dy = 0;
			power = 0;
		}
		
		if(y < rect.height - 25) {
			double drop = GRAVITY / speed;
			if(drop > 1.8) {
				double deg = Math.abs(Math.toDegrees(angle) % 360);
				double fall_rotation = Math.toRadians(drop < 10 ? drop : 10);
				if(deg <= 90 || (deg > 270 && deg < 360))
					angle = right ? angle + fall_rotation : angle - fall_rotation;
				else if(deg > 90 && deg <= 270)
					angle = right ? angle - fall_rotation : angle + fall_rotation;
			}
			dy += drop < TERMINAL_VELOCITY ? drop : TERMINAL_VELOCITY;
		}
		
		x += dx;
		y += dy;
		
		if(x > rect.width && dx > 0)
			x = 0;
		else if(x < 0 && dx < 0)
			x = rect.width;
		
		if(y > rect.height - 25) {
			y = rect.height - 25;
			double deg = Math.abs(Math.toDegrees(angle) % 360);
			if(deg > 30) {
				if(!crash)
					score++;
				crash = true;
			}
		}
		if(crash) {
			crashPos++;
			if(crashPos > 36) {
				crashed = true;
			}
		}
		
	}

	public void setIncPower(boolean incPower) {
		this.incPower = incPower;
	}
	
	public void setDecPower(boolean decPower) {
		this.decPower = decPower;
	}

	public void setIncAngle(boolean incAngle) {
		this.incAngle = incAngle;
	}
	
	public void setDecAngle(boolean decAngle) {
		this.decAngle = decAngle;
	}

	public int getScore() {
		return score;
	}

	@Override
	public String toString() {
		return "Plane [x=" + x + ", y=" + y + ", dx=" + dx + ", dy=" + dy
				+ ", speed=" + speed
				+ ", power=" + power + ", angle=" + angle + ", incPower="
				+ incPower + ", decPower=" + decPower + ", incAngle="
				+ incAngle + ", decAngle=" + decAngle + "]";
	}
}
