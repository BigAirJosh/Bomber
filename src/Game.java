import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Game extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int DIAMETER = 10;
	private static final double AIR_DRAG = 0.999999;
	private static final double GROUND_DRAG = 0.6;
	private static final double GRAVITY = -0.5;

	private JPanel gamePanel;
	private JPanel controlPanel;
	private JSlider powerSlider;
	private JLabel powerLabel;
	private JSlider angleSlider;
	private JLabel angleLabel;
	private JButton fireButton;
	private JButton resetButton;

	private int x, y, start = 50, floor = Integer.MIN_VALUE;
	private double dx, dy;

	public Game() {
		super("Cannon");
		setSize(1000, 600);
		setResizable(false);
		setLayout(new BorderLayout());
		init();
		y = x = Integer.MIN_VALUE;
	}

	private void init() {

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				System.exit(0);
			}
		});
		
		controlPanel = new JPanel(new FlowLayout());
		
		gamePanel = new JPanel(true) {
			private static final long serialVersionUID = -546325419638406681L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				paintGame(g);
			}
		};

		powerSlider = new JSlider(0, 25, 10);
		powerLabel = new JLabel("Power: 10");
		powerSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				powerLabel.setText("Power: " + powerSlider.getValue());
			}
		});
		angleSlider = new JSlider(0, 180, 45);
		angleLabel = new JLabel("Angle: 45");
		angleSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				angleLabel.setText("Angle: " + angleSlider.getValue());
			}
		});
		fireButton = new JButton("Fire");
		fireButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Fire");
				int angle = angleSlider.getValue();
				double power = (double)powerSlider.getValue();
				dx = Math.cos(Math.toRadians(angle)) * power;
				dy = Math.sin(Math.toRadians(angle)) * -power;
			}
		});
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				y = floor;
				x = start;
			}
		});

		controlPanel.add(powerSlider);
		controlPanel.add(powerLabel);
		controlPanel.add(angleSlider);
		controlPanel.add(angleLabel);
		controlPanel.add(fireButton);
		controlPanel.add(resetButton);
		
		add(controlPanel, BorderLayout.PAGE_START);
		add(gamePanel, BorderLayout.CENTER);

		Thread painter = new Thread() {
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
		painter.start();
	}
	
	public void paintGame(Graphics g) {
		Rectangle rect = g.getClipBounds();
		if(floor == Integer.MIN_VALUE) {
			// init game locations
			floor = (rect.height - 10) - DIAMETER;
			y = floor;
			x = start;
		}
		
		if(dx != 0 && dy != 0) {

			// change direction when we hit the wall
			if(x <= 0 && dx < 0) {
				dx = -dx;
				dx *= 1.05;
			} else if(x >= rect.width - DIAMETER && dx > 0) {
				dx = -dx;
				dx *= 1.05;
			}

			// change direction when we hit the floor :)
			if(y >= floor && dy > 0) {
				dy = -dy;
				dy *= GROUND_DRAG;
				dx *= GROUND_DRAG;
			}

			dx *= AIR_DRAG;
			dy *= AIR_DRAG;
			dy -= GRAVITY;

			x += (int)Math.round(dx);
			y += (int)Math.round(dy);
		}

		g.setColor(Color.cyan);
		g.fillRect(0, 0, rect.width, rect.height);

		g.setColor(new Color(0, 128, 0));
		g.fillRect(0, rect.height - 10, rect.width, 10);

		g.setColor(Color.GREEN);
		g.fillRect(900, rect.height - 10, 75, 10);

		g.setColor(Color.WHITE);
		g.fillOval(x, y, DIAMETER, DIAMETER);
	}

	public static void main(String[] args) {
		Game sc = new Game();
		sc.setVisible(true);
	}

}
