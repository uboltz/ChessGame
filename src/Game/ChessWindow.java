package Game;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChessWindow {
	
	static final int frameHeight = 500;
	static final int frameWidth = 600;
	
	static final int labelHeight = 500;
	static final int labelWidth = 500;
	
	private JFrame frame;
	private Container mainPanel, leftVerticalPanel;
	private ChessBoard board;
	private BoardListener boardListener;
	
	public ChessWindow(){
		
		frame = createFrame();
		
		mainPanel = createMainPanel();
		
		leftVerticalPanel = createLeftVerticalPanel();		
		leftVerticalPanel.add(createNewGameButton());
		leftVerticalPanel.add(createCheatButton());
		mainPanel.add(leftVerticalPanel);
		
		
		JLabel label = new JLabel();
		mainPanel.add(label);
		
		BufferedImage image = new BufferedImage(
				labelWidth,labelHeight,BufferedImage.TYPE_3BYTE_BGR);
		
	
		board = new ChessBoard();
		board.setSquareSize(50);
		
		image = board.drawImage();
		
		label.setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
		label.setBackground(Color.BLUE);
		label.setOpaque(true);
		label.setIcon(new ImageIcon(image));
		
		boardListener = new BoardListener(board, label);
		label.addMouseListener(boardListener);
		
	}
	
	private JFrame createFrame(){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setBackground(Color.GREEN);
		frame.setSize(frameWidth, frameHeight);
		return frame;
	}
	
	private Container createMainPanel(){
		Container mainPanel = frame.getContentPane();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
		return mainPanel;
	}
	
	private JPanel createLeftVerticalPanel(){
		JPanel leftVerticalPanel = new JPanel();
		leftVerticalPanel.setLayout(new BoxLayout(leftVerticalPanel, BoxLayout.PAGE_AXIS));
		return leftVerticalPanel;
	}
	
	private JButton createNewGameButton(){
			
		JButton button = new JButton();
		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boardListener.startNewGame();
			}
			
		});
		
		button.setText("New Game");
		button.setSize(50, 25);
		return button;
		
	}
	
	private JButton createCheatButton(){
		
		JButton cheatButton = new JButton();
		cheatButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boardListener.toggleCheating();
				if(boardListener.isCheatingEnabled()){
					cheatButton.setText("Resume");
				}
				else {
					cheatButton.setText("Cheat");
				}
			}
			
		});
		
		cheatButton.setText("Cheat");
		cheatButton.setSize(50, 25);
		return cheatButton;
		
	}
	

}
