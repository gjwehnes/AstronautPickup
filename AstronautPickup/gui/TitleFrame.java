import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Window.Type;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.JRadioButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TitleFrame extends JDialog {

	private JPanel contentPane;
	private boolean windowClosed;

	/**
	 * Create the frame.
	 */
	public TitleFrame() {
		
//		this.setUndecorated(true);
		this.setBackground(Color.BLUE);
//		
		setType(Type.POPUP);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 365, 600);
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTitle = new JLabel("Astronaut");
		lblTitle.setForeground(Color.ORANGE);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Stencil", Font.PLAIN, 48));
		lblTitle.setBounds(0, 32, 369, 61);
		contentPane.add(lblTitle);
		
		JLabel lblPickup = new JLabel("Pickup");
		lblPickup.setHorizontalAlignment(SwingConstants.CENTER);
		lblPickup.setForeground(Color.ORANGE);
		lblPickup.setFont(new Font("Stencil", Font.PLAIN, 48));
		lblPickup.setBounds(0, 87, 369, 61);
		contentPane.add(lblPickup);
		
		JButton btnPlay = new JButton("PLAY");
		btnPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnPlay_mouseClicked(e);
			}
		});
		btnPlay.setBorder(null);
		btnPlay.setForeground(Color.DARK_GRAY);
		btnPlay.setOpaque(true);
		btnPlay.setBackground(new Color(211, 211, 211));
		btnPlay.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnPlay.setBounds(114, 155, 145, 41);
		contentPane.add(btnPlay);
		
		JLabel lblHighScores = new JLabel("High Scores");
		lblHighScores.setVerticalAlignment(SwingConstants.TOP);
		lblHighScores.setHorizontalAlignment(SwingConstants.CENTER);
		lblHighScores.setForeground(Color.WHITE);
		lblHighScores.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblHighScores.setBounds(0, 240, 369, 30);
		contentPane.add(lblHighScores);

		JTextArea lblHighScoreList = new JTextArea();
		lblHighScoreList.setOpaque(false);
		lblHighScoreList.setForeground(Color.WHITE);
		lblHighScoreList.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
		lblHighScoreList.setBounds(0, 270, 369, 330);
		contentPane.add(lblHighScoreList);
		
		/*
		 * Code for displaying a list of high scores
		 */
		String scores = "";
		for (int i = 0; i <= HighScores.LIST_SIZE - 1; i++) {
			NameAndScore score = HighScores.getHighScore(i);
			if (score != null) {
				String nameAndScore = String.format("  %5s%15d  ",score.getName(), score.getScore());
				scores += nameAndScore + (i < HighScores.LIST_SIZE - 1 ? System.getProperty("line.separator") : "");
			}
		}
		lblHighScoreList.setText(scores);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				this_windowClosing(e);
			}
		});
		
	}
	
	protected void this_windowClosing(WindowEvent e) {
		System.out.println("AnimationFrame.windowClosing()");
		this.windowClosed = true;				
	}

	protected void btnPlay_mouseClicked(MouseEvent e) {
		this.windowClosed = false;
		this.setVisible(false);
	}
	
	public boolean getWindowClosed() {
		return windowClosed;
	}
	
}
