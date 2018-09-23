package Game;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

import ChessAI.ChessAI;
import ChessAI.HelloWorldChessAI;
import ChessAI.MinMaxChessAI;

public class BoardListener implements MouseListener{
	
	private enum Mode {
		SELECTPIECE,
		SELECTTARGET;
	}

	private ChessBoard board;
	private ChessAI ai;
	private MinMaxChessAI mmai;
	private Mode mode = Mode.SELECTPIECE;
	private ChessBoard.Square startOfMove;
	private JLabel label;
	
	private boolean cheatingEnabled;
	
	public BoardListener(ChessBoard board, JLabel label){
		this.board = board;
		this.label = label;
		
		this.ai = new HelloWorldChessAI();
		this.mmai = new MinMaxChessAI();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		System.out.println(mode);
		System.out.println("Click: " +  e.getX() + " " + e.getY());		
		
		ChessBoard.Square square = board.getSquareFromPixels(e.getX(), e.getY());		
		System.out.println("Square: " + square.x + " " + square.y);
				
		boolean leftClick = (e.getButton() == 1);
		
		if(mode == Mode.SELECTPIECE){
			if(!square.isEmpty()){
				
				ChessPiece piece = square.getPiece();
				if((piece.getOwner() == board.activePlayer)
						|| cheatingEnabled){
					startOfMove = square;
					square.highlighted = true;
					board.highlightAllLegalMoves(square);
					refreshGraphics();
					mode = Mode.SELECTTARGET;
					System.out.println("Piece selected");
				}
			}
			
		}
		else if (mode == Mode.SELECTTARGET){
			
			if(!leftClick){
				mode = Mode.SELECTPIECE;
				System.out.println("Selection canceled");
				board.lowlightAllSquares();
				refreshGraphics();
			}
			else {
				ChessBoard.Move move = board.new Move(startOfMove, square);
				if(cheatingEnabled){
					board.movePiece(move);
					board.lowlightAllSquares();
					refreshGraphics();
					mode = Mode.SELECTPIECE;
					System.out.println("target selected");
				}
				else if(board.isLegalMoveForActivePlayer(move)){
					board.executeMove(move);
					System.out.println("evaluation: " + mmai.evaluate(board, board.activePlayer));
					checkForGameOver();
//					if(!board.checkMate() && !board.draw()){
//						board.executeMove(ai.nextMove(new ChessBoard(board)));					
//						System.out.println("evaluation: " + mmai.evaluate(board, board.activePlayer));
//					}
					board.lowlightAllSquares();
					refreshGraphics();
					mode = Mode.SELECTPIECE;
					System.out.println("target selected");
				}
			}
		
			
			
		}
		
		
	}

	
	public void startNewGame(){
		board.setUpNewGame();
		refreshGraphics();
	}
	
	public void toggleCheating(){
		cheatingEnabled = !cheatingEnabled;
	}
	
	private void checkForGameOver(){
		
		if(board.kingIsThreatened(board.activePlayer)){
			System.out.println("check " + board.activePlayer.name());
		}
		else {
			System.out.println("no check " + board.activePlayer.name());
		}
		
		if(board.checkMate()){
			System.out.println("Checkmate. Winner: " + board.activePlayer.getOpponent().name());
		}
		else if(board.draw()){
			System.out.println("Draw. Neither player wins.");
		}
	}
	
	private void refreshGraphics(){
		label.setIcon(new ImageIcon(board.drawImage()));
	}
	
	public boolean isCheatingEnabled(){
		return cheatingEnabled;
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
	

}
