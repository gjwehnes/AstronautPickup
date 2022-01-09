import javax.swing.*;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseMotionAdapter;


public class AstronautPickupFrame extends JFrame {

	final public static int FRAMES_PER_SECOND = 60;	
	final public static int SCREEN_HEIGHT = 750;	
	final public static int SCREEN_WIDTH = 900;

	private TitleFrame titleFrame = null;
	
	private int screenCenterX = SCREEN_WIDTH / 2;
	private int screenCenterY = SCREEN_HEIGHT / 2;

	private double scale = 1;
	//point in universe on which the screen will center
	private double logicalCenterX = 0;		
	private double logicalCenterY = 0;

	private JPanel panel = null;	
	private JButton btnPauseRun;	
	private JLabel lblStatus;
	private JLabel lblLevel;	
	private JLabel lblAmmoLabel;	
	private JLabel lblHealthLabel;	
	private JLabel lblHealth;	
	private JLabel lblFuelLabel;	
	private JLabel lblFuel;	
	private JLabel lblAmmo;

	private static boolean stop = false;

	private long current_time = 0;								//MILLISECONDS
	private long next_refresh_time = 0;							//MILLISECONDS
	private long last_refresh_time = 0;
	private long minimum_delta_time = 1000 / FRAMES_PER_SECOND;	//MILLISECONDS
	private long actual_delta_time = 0;							//MILLISECONDS
	private long elapsed_time = 0;
	private boolean isPaused = false;

	private KeyboardInput keyboard = new KeyboardInput();
	private Universe universe = null;

	//local (and direct references to various objects in universe ... should reduce lag by avoiding dynamic lookup
	private Animation animation = null;
	private SpaceShipSprite player1 = null;
	private ArrayList<DisplayableSprite> sprites = null;
	private Background background = null;
	boolean centreOnPlayer = false;
	int universeLevel = 0;
	
	public AstronautPickupFrame(Animation animation)
	{
		super("");
		this.setFocusable(true);
		this.setSize(SCREEN_WIDTH + 20, SCREEN_HEIGHT + 36);
		this.animation = animation;

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				this_windowClosing(e);
			}
		});

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				keyboard.keyPressed(arg0);
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				keyboard.keyReleased(arg0);
			}
		});

		Container cp = getContentPane();
		cp.setBackground(Color.BLACK);
		cp.setLayout(null);

		panel = new DrawPanel();
		panel.setLayout(null);
		panel.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		getContentPane().add(panel, BorderLayout.CENTER);

		btnPauseRun = new JButton("||");
		btnPauseRun.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				btnPauseRun_mouseClicked(arg0);
			}
		});

		btnPauseRun.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPauseRun.setBounds(20, 20, 48, 32);
		btnPauseRun.setFocusable(false);
		getContentPane().add(btnPauseRun);
		getContentPane().setComponentZOrder(btnPauseRun, 0);

		lblStatus = new JLabel("");
		lblStatus.setForeground(Color.YELLOW);
		lblStatus.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblStatus.setBounds(84, 22, 500, 30);
		getContentPane().add(lblStatus);
		getContentPane().setComponentZOrder(lblStatus, 0);

		lblLevel = new JLabel("");
		lblLevel.setForeground(Color.YELLOW);
		lblLevel.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblLevel.setBounds(690, 20, 192, 30);
		getContentPane().add(lblLevel);
		getContentPane().setComponentZOrder(lblLevel, 0);
		
		lblAmmoLabel = new JLabel("Ammo:");
		lblAmmoLabel.setForeground(Color.WHITE);
		lblAmmoLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblAmmoLabel.setBounds(20, SCREEN_HEIGHT - 98, 152, 30);
		getContentPane().add(lblAmmoLabel);
		getContentPane().setComponentZOrder(lblAmmoLabel, 0);
		
		lblAmmo = new JLabel("95");
		lblAmmo.setBounds(190, SCREEN_HEIGHT - 90, 100,14);
		setBarLabelBounds(this.lblAmmo, 100);
		lblAmmo.setHorizontalAlignment(SwingConstants.CENTER);
		lblAmmo.setOpaque(true);
		lblAmmo.setForeground(Color.BLACK);
		lblAmmo.setBackground(Color.GREEN);
		getContentPane().add(lblAmmo);
		getContentPane().setComponentZOrder(lblAmmo, 0);

		lblHealthLabel = new JLabel("Health");
		lblHealthLabel.setForeground(Color.WHITE);
		lblHealthLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblHealthLabel.setBounds(20, SCREEN_HEIGHT - 57, 152, 30);
		getContentPane().add(lblHealthLabel);
		getContentPane().setComponentZOrder(lblHealthLabel, 0);
		
		lblHealth = new JLabel("5");
		lblHealth.setBounds(190, SCREEN_HEIGHT - 49, 100,14);
		lblHealth.setHorizontalAlignment(SwingConstants.CENTER);
		lblHealth.setOpaque(true);
		lblHealth.setForeground(Color.BLACK);
		lblHealth.setBackground(Color.ORANGE);
		setBarLabelBounds(this.lblHealth, 100);
		getContentPane().add(lblHealth);
		getContentPane().setComponentZOrder(lblHealth, 0);

		lblFuelLabel = new JLabel("Fuel:");
		lblFuelLabel.setForeground(Color.WHITE);
		lblFuelLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblFuelLabel.setBounds(20, SCREEN_HEIGHT - 139, 152, 30);
		getContentPane().add(lblFuelLabel);
		getContentPane().setComponentZOrder(lblFuelLabel, 0);
		
		lblFuel = new JLabel("80");
		lblFuel.setBounds(190, SCREEN_HEIGHT - 131, 100,14);
		lblFuel.setHorizontalAlignment(SwingConstants.CENTER);
		lblFuel.setBackground(Color.GREEN);
		lblFuel.setForeground(Color.BLACK);
		setBarLabelBounds(this.lblFuel, 100);
		lblFuel.setOpaque(true);
		getContentPane().add(lblFuel);
		getContentPane().setComponentZOrder(lblFuel, 0);

	}

	private void setBarLabelBounds(JLabel label, double percent) {

		int minX = 189;
		int minY = label.getY();
		int maxX = this.getWidth() - 32;
		
		label.setBounds(minX, minY,(int)(minX + (maxX - minX) * percent / 100) - minX, 14);
	}
	
	public void start()
	{
		
		//hide interface
		this.setVisible(false);

		Thread thread = new Thread()
		{
			public void run()
			{
				animationLoop();
				System.out.println("run() complete");
			}
		};

		//start the animation loop so that it can initialize at the same time as the title screen being visible
		//as it runs on a separate thread, it will execute asynchronously
		thread.start();
		
		//create a title frame
		titleFrame = new TitleFrame();
		//center on the parent
		titleFrame.setLocationRelativeTo(this);
		//display title screen
		//set the modality to APPLICATION_MODAL
		titleFrame.setModalityType(ModalityType.APPLICATION_MODAL);
		//by setting the dialog to visible, the application will start running the dialog
		titleFrame.setVisible(true);
		
		//when title screen has been closed, execution will resume here.
		titleFrame.dispose();
		this.setVisible(true);
		
		System.out.println("main() complete");

	}	
	private void animationLoop() {

		universe = animation.getNextUniverse();
		universeLevel++;
		
		while (stop == false && universe != null) {

			//initialize animation
			sprites = universe.getSprites();
			player1 = (SpaceShipSprite) universe.getPlayer1();
			background = universe.getBackground();
			centreOnPlayer = universe.centerOnPlayer();
			this.scale = universe.getScale();
			this.logicalCenterX = universe.getXCenter();
			this.logicalCenterY = universe.getYCenter();
			
			//pause while title screen is displayed
			while (titleFrame != null && titleFrame.isVisible() == true) {
				Thread.yield();
				try {
					Thread.sleep(1);
				}
				catch(Exception e) {    					
				} 				
			}

			// main game loop
			while (stop == false && universe.isComplete() == false) {

				//adapted from http://www.java-gaming.org/index.php?topic=24220.0
				last_refresh_time = System.currentTimeMillis();
				next_refresh_time = current_time + minimum_delta_time;

				//sleep until the next refresh time
				while (current_time < next_refresh_time)
				{
					//allow other threads (i.e. the Swing thread) to do its work
					Thread.yield();

					try {
						Thread.sleep(1);
					}
					catch(Exception e) {    					
					} 

					//track current time
					current_time = System.currentTimeMillis();
				}

				//read input
				keyboard.poll();
				handleKeyboardInput();

				//UPDATE STATE
				updateTime();
				
				universe.update(keyboard, actual_delta_time);
				updateControls();

				//REFRESH
				this.logicalCenterX = universe.getXCenter();
				this.logicalCenterY = universe.getYCenter();
				this.repaint();
			}
			
			if (universe.isComplete()) {
				
				if (((Level01Universe)universe).isSuccessful()) {
					JOptionPane.showMessageDialog(this,
							"Proceed to next level!");
					
					universe = animation.getNextUniverse();
				}
				else {
					int choice = JOptionPane.showOptionDialog(this,
							"Spaceship go Boom. Play again?",
									"Game Over",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE,
									null,
									null,
									null);
					
					if (choice == 0) {
						((AstronautPickupAnimation) animation).restart();
						universe = animation.getNextUniverse();
						keyboard.poll();					
					}
					else {
						universe = null;
					}
				}
				


			}


			
		}

		System.out.println("animation complete");
		AudioPlayer.setStopAll(true);
		dispose();	

	}

	private void updateControls() {
		
		this.lblStatus.setText(String.format("Score: %8d  Rescued: %2d/%2d",AstronautPickupAnimation.getScore(), player1.getAstronautsRescued(), ((Level01Universe) universe).getTarget()));	
		this.lblLevel.setText(String.format("Level: %3d",((AstronautPickupAnimation)animation).getLevel()));
		this.lblFuel.setText(String.format("%3.1f", player1.getFuel()));	
		setBarLabelBounds(this.lblFuel, player1.getFuel());	
		this.lblAmmo.setText(Integer.toString(player1.getAmmo()));	
		setBarLabelBounds(this.lblAmmo, player1.getAmmo() / (float)player1.getMaxAmmo() * 100);	
		this.lblHealth.setText(String.format("%3.2f", player1.getHealth()));	
		setBarLabelBounds(this.lblHealth, player1.getHealth());
	}

	private void updateTime() {

		current_time = System.currentTimeMillis();
		actual_delta_time = (isPaused ? 0 : current_time - last_refresh_time);
		last_refresh_time = current_time;
		elapsed_time += actual_delta_time;

	}

	protected void btnPauseRun_mouseClicked(MouseEvent arg0) {
		if (isPaused) {
			isPaused = false;
			this.btnPauseRun.setText("||");
		}
		else {
			isPaused = true;
			this.btnPauseRun.setText(">");
		}
	}

	private void handleKeyboardInput() {
		
		if (keyboard.keyDown(80) && ! isPaused) {
			btnPauseRun_mouseClicked(null);	
		}
		if (keyboard.keyDown(79) && isPaused ) {
			btnPauseRun_mouseClicked(null);
		}
		if (keyboard.keyDown(112)) {
			scale *= 1.01;
		}
		if (keyboard.keyDown(113)) {
			scale /= 1.01;
		}
		
		if (keyboard.keyDown(65)) {
			screenCenterX -= 1;
		}
		if (keyboard.keyDown(68)) {
			screenCenterX += 1;
		}
		if (keyboard.keyDown(83)) {
			screenCenterY -= 1;
		}
		if (keyboard.keyDown(88)) {
			screenCenterY += 1;
		}
		
	}

	class DrawPanel extends JPanel {

		public void paintComponent(Graphics g)
		{	
			if (universe == null) {
				return;
			}

			if (player1 != null && centreOnPlayer) {
				logicalCenterX = player1.getCenterX();
				logicalCenterY = player1.getCenterY();     
			}

			paintBackground(g, background);

			for (DisplayableSprite activeSprite : sprites) {
				DisplayableSprite sprite = activeSprite;
				if (sprite.getVisible()) {
					if (sprite.getImage() != null) {
						g.drawImage(sprite.getImage(), translateToScreenX(sprite.getMinX()), translateToScreenY(sprite.getMinY()), scaleLogicalX(sprite.getWidth()), scaleLogicalY(sprite.getHeight()), null);
					}
					else {
						g.setColor(Color.BLUE);
						g.fillRect(translateToScreenX(sprite.getMinX()), translateToScreenY(sprite.getMinY()), scaleLogicalX(sprite.getWidth()), scaleLogicalY(sprite.getHeight()));
					}
				}

			}

		}
		
		private void paintBackground(Graphics g, Background background) {

			if ((g == null) || (background == null)) {
				return;
			}
			
			//what tile covers the top-left corner?
			double logicalLeft = ( logicalCenterX - (screenCenterX / scale));
			double logicalTop =  (logicalCenterY - (screenCenterY / scale)) ;
			
			int row = background.getRow((int)logicalTop);
			int col = background.getCol((int)logicalLeft);
			Tile tile = null;

			boolean rowDrawn = false;
			boolean screenDrawn = false;
			while (screenDrawn == false) {
				while (rowDrawn == false) {
					tile = background.getTile(col, row);
					if (tile.getWidth() <= 0 || tile.getHeight() <= 0) {
						//no increase in width; will cause an infinite loop, so consider this screen to be done
						g.setColor(Color.GRAY);
						g.fillRect(0,0, SCREEN_WIDTH, SCREEN_HEIGHT);					
						rowDrawn = true;
						screenDrawn = true;						
					}
					else {
						Tile nextTile = background.getTile(col+1, row+1);
						int width = translateToScreenX(nextTile.getMinX()) - translateToScreenX(tile.getMinX());
						int height = translateToScreenY(nextTile.getMinY()) - translateToScreenY(tile.getMinY());
						g.drawImage(tile.getImage(), translateToScreenX(tile.getMinX()), translateToScreenY(tile.getMinY()), width, height, null);
					}					
					//does the RHE of this tile extend past the RHE of the visible area?
					if (translateToScreenX(tile.getMinX() + tile.getWidth()) > SCREEN_WIDTH || tile.isOutOfBounds()) {
						rowDrawn = true;
					}
					else {
						col++;
					}
				}
				//does the bottom edge of this tile extend past the bottom edge of the visible area?
				if (translateToScreenY(tile.getMinY() + tile.getHeight()) > SCREEN_HEIGHT || tile.isOutOfBounds()) {
					screenDrawn = true;
				}
				else {
					//TODO - should be passing in a double, as this represents a universe coordinate
					col = background.getCol((int)logicalLeft);
					row++;
					rowDrawn = false;
				}
			}
		}				
	}

	private int translateToScreenX(double logicalX) {
		return screenCenterX + scaleLogicalX(logicalX - logicalCenterX);
	}		
	private int scaleLogicalX(double logicalX) {
		return (int) Math.round(scale * logicalX);
	}
	private int translateToScreenY(double logicalY) {
		return screenCenterY + scaleLogicalY(logicalY - logicalCenterY);
	}		
	private int scaleLogicalY(double logicalY) {
		return (int) Math.round(scale * logicalY);
	}

	private double translateToLogicalX(int screenX) {
		int offset = screenX - screenCenterX;
		return offset / scale;
	}
	private double translateToLogicalY(int screenY) {
		int offset = screenY - screenCenterY;
		return offset / scale;			
	}
	
	protected void contentPane_mouseMoved(MouseEvent e) {
		MouseInput.screenX = e.getX();
		MouseInput.screenY = e.getY();
		MouseInput.logicalX = translateToLogicalX(MouseInput.screenX);
		MouseInput.logicalY = translateToLogicalY(MouseInput.screenY);
	}

	protected void this_windowClosing(WindowEvent e) {
		System.out.println("windowClosing()");
		stop = true;
		dispose();	
	}
}
