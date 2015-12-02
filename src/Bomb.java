import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Bomb {
	
	private static final int EXPLOSION_SIZE = 256 / 4;
	private static final int EXPLOSION_RADIUS = EXPLOSION_SIZE / 2;
	private static final double FLIP = Math.toRadians(180);
	
	private double x, y, dx, dy;
	private boolean right;
	private boolean hit, exploaded;
	private int exploadPos = 0;
	
	private static Image bombImage;
	private static Image explosionImage;
	
	BombHitHandler bombHitHandler;
	
	static {
		try {
			bombImage = ImageIO.read(new File("./images/bomb1.png"));
			explosionImage = ImageIO.read(new File("./images/explosion1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Bomb(boolean right, BombHitHandler bombHitHandler, double x, double y, double dx, double dy) {
		super();
		this.right = right;
		this.bombHitHandler = bombHitHandler;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		
		
	}

	public void paint(Graphics g) {
		Rectangle rect = g.getClipBounds();
		computePosition(rect);
		Graphics2D g2d = (Graphics2D) g;
		if(!hit) {
			AffineTransform at = new AffineTransform();
			at.translate(x, y);
			if(!right)
			at.rotate(FLIP);
	        at.translate(-bombImage.getWidth(null) / 2, -bombImage.getHeight(null) / 2);
	        g2d.drawImage(bombImage, at, null);
		} else if(!exploaded){
			int pos = exploadPos / 4;
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
	}

	private void computePosition(Rectangle rect) {
		if(!exploaded) {
			dx *= 0.98;
			dy += 1.5;
			x += dx;
			y += dy;
			if(x > rect.width && dx > 0)
				x = 0;
			else if(x < 0 && dx < 0)
				x = rect.width;
			
			if(y > rect.height - 15) {
				y = rect.height - 15;
				dy = 0;
				dx = 0;
				// explode
				if(!hit)
					bombHitHandler.bomhBit((int)x, (int)y);
				hit = true;
				
				exploadPos++;
				if(exploadPos > 36) {
					exploaded = true;
				}
			}
		}
	}

}
