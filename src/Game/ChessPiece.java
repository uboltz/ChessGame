package Game;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import BoardGame.Piece;

public class ChessPiece extends Piece {
	
	public enum Type {
		PAWN ('P'),
		ROOK ('R'),
		KNIGHT ('H'),
		BISHOP ('B'),
		QUEEN ('Q'),
		KING ('K');
		
		public final char symbol;
		
		private Type(char symbol){
			this.symbol = symbol;
		}
	}
	
	
	public Type getPieceType() {
		return pieceType;
	}

	public ChessBoard.Player getOwner() {
		return owner;
	}

	private Type pieceType;
	private ChessBoard.Player owner;
	
	public ChessPiece(Type pieceType, ChessBoard.Player owner){
		this.pieceType = pieceType;
		this.owner = owner;
	}
	
	public BufferedImage getImage(int size){
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics graphics = image.getGraphics();
		
		graphics.setColor(new Color(0,0,0,0));
		graphics.fillRect(0, 0, size, size);
		
		if(owner == ChessBoard.Player.BLACK){
			graphics.setColor(Color.BLACK);
		}
		else {
			graphics.setColor(Color.WHITE);
		}
		
		graphics.setFont(new Font("", Font.BOLD, size - 2));
		graphics.drawString("" + pieceType.symbol, 2, size - 2);
		
		return image;
	}

}
