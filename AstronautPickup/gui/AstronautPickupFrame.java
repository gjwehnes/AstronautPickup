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


public class AstronautPickupFrame extends AnimationFrame {
	
	protected static final int STANDARD_SCREEN_HEIGHT = 800;
	protected static final int STANDARD_SCREEN_WIDTH = 1200;
	
	private TitleFrame titleFrame = null;
	private JLabel lblLevel;	
	private JLabel lblAmmoLabel;	
	private JLabel lblHealthLabel;	
	private JLabel lblHealth;	
	private JLabel lblFuelLabel;	
	private JLabel lblFuel;	
	private JLabel lblAmmo;

	public AstronautPickupFrame(Animation animation)
	{
		super(animation);

		lblLevel = new JLabel("");
		lblLevel.setForeground(Color.YELLOW);
		lblLevel.setFont(new Font("Tahoma", Font.BOLD, 30));
		getContentPane().add(lblLevel);
		getContentPane().setComponentZOrder(lblLevel, 0);
		
		lblAmmoLabel = new JLabel("Ammo:");
		lblAmmoLabel.setForeground(Color.WHITE);
		lblAmmoLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		getContentPane().add(lblAmmoLabel);
		getContentPane().setComponentZOrder(lblAmmoLabel, 0);
		
		lblAmmo = new JLabel("95");
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
		getContentPane().add(lblHealthLabel);
		getContentPane().setComponentZOrder(lblHealthLabel, 0);
		
		lblHealth = new JLabel("5");
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
		getContentPane().add(lblFuelLabel);
		getContentPane().setComponentZOrder(lblFuelLabel, 0);
		
		lblFuel = new JLabel("80");
		lblFuel.setHorizontalAlignment(SwingConstants.CENTER);
		lblFuel.setBackground(Color.GREEN);
		lblFuel.setForeground(Color.BLACK);
		setBarLabelBounds(this.lblFuel, 100);
		lblFuel.setOpaque(true);
		getContentPane().add(lblFuel);
		getContentPane().setComponentZOrder(lblFuel, 0);

	}
	
	protected void repositionComponents() {
		//call the super class's version of this method so it can also resize the
		//components it controls
		super.repositionComponents();
		
		if (lblFuel != null) {
			lblLevel.setBounds(690, 20, 192, 30);
			lblAmmoLabel.setBounds(20, screenHeight - 98, 152, 30);
			lblAmmo.setBounds(190, screenHeight - 90, 100,14);
			lblHealthLabel.setBounds(20, screenHeight - 57, 152, 30);
			lblHealth.setBounds(190, screenHeight - 49, 100,14);
			lblFuelLabel.setBounds(20, screenHeight - 139, 152, 30);
			lblFuel.setBounds(190, screenHeight - 131, 100,14);		
		}
		
	}
	

	private void setBarLabelBounds(JLabel label, double percent) {

		int minX = 189;
		int minY = label.getY();
		int maxX = screenWidth - 32;
		
		label.setBounds(minX, minY,(int)(minX + (maxX - minX) * percent / 100) - minX, 14);
	}
		
	protected void animationStart() {
		
		System.out.println("AstronautPickupFrame.animationStart");
		//hide interface
		this.setVisible(false);
		titleFrame = new TitleFrame();
		//center on the parent
		titleFrame.setLocationRelativeTo(this);
		//display title screen
		//set the modality to APPLICATION_MODAL
		titleFrame.setModalityType(ModalityType.APPLICATION_MODAL);
		//by setting the dialog to visible, the application will start running the dialog
		titleFrame.setVisible(true);
		
		if (titleFrame.getWindowClosed()) {
			// if the title frame was closed by user, end the application completely
			this.stopApplication = true;
			this.stopAnimation = true;
			this.windowClosed = true;
		}
		titleFrame.dispose();

		//		this.setVisible(true);
		
	}
		
	@Override
	protected void universeSwitched() {
				
		System.out.println("AstronautPickupFrame.universeSwitched");

		if (universe.isComplete()) {
			
			int level = ((AstronautPickupAnimation)animation).getLevel();
			
			if (((Level01Universe)universe).isSuccessful()) {
				universe = animation.switchUniverse(null);
				if (animation.isComplete() == false) {
					JOptionPane.showMessageDialog(this,
							"Proceed to next level!");					
				}
				else {
					JOptionPane.showMessageDialog(this,
							"You have beaten the game... Congratulations!");
					stopAnimation = true;
				}
			}
			else {
								
				JOptionPane.showOptionDialog(this,
						"Spaceship go Boom",
								"Game Over",
								JOptionPane.CLOSED_OPTION,
								JOptionPane.PLAIN_MESSAGE,
								null,
								null,
								null);

				int score = ((AstronautPickupAnimation) animation).getScore();
				int rank = HighScores.getScoreRank(score);
				if (rank >= 0) {
					String name = JOptionPane.showInputDialog(this,
							"You achieved a high score!",
							"High Score",
							JOptionPane.OK_OPTION);
					name = name.length() <= 3 ? String.format("%3s", name): name.substring(0,3); 
					HighScores.setHighScore(new NameAndScore(name, score));
				}
				
				stopAnimation = true;

			}				
		}
				
	}
	
	protected void updateControls() {
		
		SpaceShipSprite ship = ((Level01Universe)universe).getPlayer1();
		
		this.lblTop.setText(String.format("Score: %8d  Rescued: %2d/%2d",AstronautPickupAnimation.getScore(), ship.getAstronautsRescued(), ((Level01Universe) universe).getTarget()));	
		this.lblLevel.setText(String.format("Level: %3d",((AstronautPickupAnimation)animation).getLevel()));
		this.lblFuel.setText(String.format("%3.1f", ship.getFuel()));	
		setBarLabelBounds(this.lblFuel, ship.getFuel());	
		this.lblAmmo.setText(Integer.toString(ship.getAmmo()));	
		setBarLabelBounds(this.lblAmmo, ship.getAmmo() / (float)ship.getMaxAmmo() * 100);	
		this.lblHealth.setText(String.format("%3.2f", ship.getHealth()));	
		setBarLabelBounds(this.lblHealth, ship.getHealth());
	}

	protected void handleKeyboardInput() {
		super.handleKeyboardInput();
		if (keyboard.keyDown(KeyboardInput.KEY_Q) && ! isPaused) {
//			stop = true;
//			animation.setComplete(true);
			SpaceShipSprite ship = ((Level01Universe)universe).getPlayer1();
			ship.setHealth(0);
		}		
	}
		
}
