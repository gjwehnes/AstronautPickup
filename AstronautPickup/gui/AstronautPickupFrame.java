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
	
	final public static int SCREEN_HEIGHT = 750;
	final public static int SCREEN_WIDTH = 900;

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
		
	protected void animationStart() {
		
		System.out.println("AnimationFrame.animationStart");
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
		
		//when title screen has been closed, execution will resume here.
		titleFrame.dispose();
		this.setVisible(true);
		
	}

	@Override
	protected void universeSwitched() {
				
		System.out.println("AnimationFrame.universeSwitched");

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
					stop = true;
				}
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
					universe = animation.switchUniverse(null);
					keyboard.poll();					
				}
				else {
					stop = true;
				}
			}				
		}
				
	}
	
	protected void updateControls() {
		
		SpaceShipSprite ship = (SpaceShipSprite)player1;
		
		this.lblTop.setText(String.format("Score: %8d  Rescued: %2d/%2d",AstronautPickupAnimation.getScore(), ship.getAstronautsRescued(), ((Level01Universe) universe).getTarget()));	
		this.lblLevel.setText(String.format("Level: %3d",((AstronautPickupAnimation)animation).getLevel()));
		this.lblFuel.setText(String.format("%3.1f", ship.getFuel()));	
		setBarLabelBounds(this.lblFuel, ship.getFuel());	
		this.lblAmmo.setText(Integer.toString(ship.getAmmo()));	
		setBarLabelBounds(this.lblAmmo, ship.getAmmo() / (float)ship.getMaxAmmo() * 100);	
		this.lblHealth.setText(String.format("%3.2f", ship.getHealth()));	
		setBarLabelBounds(this.lblHealth, ship.getHealth());
	}


		
}
