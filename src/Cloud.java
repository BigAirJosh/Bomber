import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Cloud {
	
	private static Image cloud1Image;
	private static Image cloud2Image;
		
	static {
		try {
			cloud1Image = ImageIO.read(new File("./images/cloud1.png"));
			cloud2Image = ImageIO.read(new File("./images/cloud2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private double x, y, dx;
	private Image image;
	
	public Cloud() {
		x = Math.random() * BomberGame.WIDTH;
		y = 300 - Math.random() * (BomberGame.HEIGHT - 300);
		dx = -Math.random() * 0.2;
		if(Math.random() > 0.5)
			image = cloud1Image;
		else
			image = cloud2Image;
	}
	
	public void paint(Graphics g) {
		x += dx;
		if(x < 0 - image.getWidth(null))
			x = BomberGame.WIDTH;
		g.drawImage(image, (int)x, (int)y, null);
	}

}
