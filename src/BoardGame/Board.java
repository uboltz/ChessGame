package BoardGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Board<PieceType extends Piece> {
	
	public class Square {
		
		private PieceType piece = null;
		
		public int x,y;
		
		public boolean highlighted = false;
		
		public Square(int x, int y){
			this.x = x;
			this.y = y;
		}
		
		public void setPiece(PieceType piece){
			this.piece = piece;
		}
		
		public boolean isEmpty(){
			return piece == null;
		}
		
		public PieceType getPiece(){
			return piece;
		}
		
		public void removePiece(){
			this.piece = null;
		}
		
	}
	
	public class Move {

		public final int startX, startY, endX, endY;

		public Move(int sx, int sy, int ex, int ey){
			startX = sx;
			startY = sy;
			endX = ex;
			endY = ey;
		}
		
		public Move(Square startingSquare, Square endSquare){
			startX = startingSquare.x;
			startY = startingSquare.y;
			endX = endSquare.x;
			endY = endSquare.y;
		}
		
		public Square getStartingSquare(Board<PieceType> board){
			return board.getSquare(startX, startY);
		}
		
		public Square getEndSquare(Board<PieceType> board){
			return board.getSquare(endX, endY);
		}
		
		@Override
		public String toString(){
			return "Start: " + startX + "," + startY + " End: " + endX + "," + endY;
		}
			
	}
	
	public Color highlightColor = Color.green;
	
	protected int height;
	protected int width;
	protected int squareSize;
	protected int widthInPixels, heightInPixels;
	
	protected List<Square> squares = new ArrayList<Square>();
	
	
	public Board(int height, int width){
		this.height = height;
		this.width = width;
		
		setSquareSize(25);
		
		createListOfSquares();
	}
	
	
	public BufferedImage drawImage(){
		
		BufferedImage image = new BufferedImage(widthInPixels, heightInPixels, BufferedImage.TYPE_3BYTE_BGR);
		Graphics graphics = image.getGraphics();
		
		drawBoard(graphics);
		drawPieces(graphics);
		
		return image;
		
	}
	
	public void putPiece(PieceType piece, int x, int y){
		
		if(inBounds(x,y)){
			getSquare(x,y).setPiece(piece);
		}
		else {
			System.out.println("Board.putPiece: trying to put piece on square out of bounds");
			System.out.println("x: " + x + "  y: " + y);
			
		}
	}
	
	public void clearPieces(){
		for(Square s : squares){
			s.removePiece();
		}
	}
	
	public boolean inBounds(int x, int y){
		return (x>0) && (x<=width) && (y>0) && (y<=height);
	}
	
	public Square getSquare(int x, int y){
		return squares.get((y-1) * width + x - 1);
	}
	
	public Square getSquareFromPixels(int x, int y){
		return getSquare(x / squareSize + 1, y / squareSize + 1);
	}
	
	private void drawBoard(Graphics graphics){
		
		graphics.setColor(Color.GRAY);
		graphics.fillRect(0, 0, widthInPixels, heightInPixels);
		
		int columnWidth = widthInPixels / width;
		int lineHeight = heightInPixels / height;
		
		graphics.setColor(highlightColor);
		for(Square square : squares){
			if(square.highlighted){
				graphics.fillRect((square.x -1) * columnWidth,
						(square.y - 1) * lineHeight, 
						columnWidth, lineHeight);
			}
		}
		
		graphics.setColor(Color.BLACK);
		
		
		
		for(int x = 0; x <= width; x++){
			graphics.drawLine(x*columnWidth, 0, x*columnWidth, heightInPixels);
		}
		
		for(int y = 0; y <= width; y++){
			graphics.drawLine(0, y*lineHeight, widthInPixels, y*lineHeight);
		}
	}
	
	private void drawPieces(Graphics graphics){
		 for(Square square: squares){
			 if(!square.isEmpty()){
				 BufferedImage image = square.getPiece().getImage(squareSize - 1);
				 int x = (square.x - 1) * squareSize + 1;
				 int y = (square.y - 1) * squareSize + 1;
				 graphics.drawImage(image, x, y, null);
			 }
		 }
	}
	
	public void lowlightAllSquares(){
		for(Square square : squares){
			square.highlighted = false;
		}
	}
	
	public void setSquareSize(int size) {
		this.squareSize = size;
		widthInPixels = size * width;
		heightInPixels = size * height;
		
	}
	
	
	private void createListOfSquares(){
		
		squares = new ArrayList<Square>();
		
		for(int y=1; y<=height; y++)	
			 for(int x=1; x <= width; x++){
				squares.add(new Square(x,y));
		}

	}
	
	public boolean outOfBounds(Square square){
		return (square.x > width) || (square.y > height)
				|| (square.x <= 0) || (square.y <= 0);
	}
	
	protected boolean isStraightMove(Move move){
		return (move.startX == move.endX) 
				|| (move.startY == move.endY);
	}
	
	protected boolean isDiagonalMove(Move move){
		return (move.startX - move.endX == move.startY - move.endY)
				|| (move.startX - move.endX == - move.startY + move.endY);
	}
	
	//returns squares in between (exclusive). Returns an empty list if square2 and square2 are not
	//in a straight line or along a 45 degree diagonal line
	public List<Square> getSquaresInBetween(Square square1, Square square2){
		
		List<Square> squaresBetween = new ArrayList<Square>();
		
		//Make sure square1 is the left one. If there is no left one, make square1 the one with smaller y.
		Square dummy;
		if((square1.x > square2.x)
				|| ((square1.x == square2.x) && (square1.y > square2.y))){
			
			dummy = square1;
			square1 = square2;
			square2 = dummy;
		}

		
		//if squares are identical, there's nothing in between them
		if((square1.x != square2.x) || (square1.y != square2.y)){
			
			//horizontal straight
			if(square1.y == square2.y) {
				for(int x = square1.x + 1; x < square2.x; x++){
					squaresBetween.add(getSquare(x, square1.y));
				}
			}
			
			//vertical straight
			else if(square1.x == square2.x) {
				for(int y = square1.y + 1; y < square2.y; y++){
					squaresBetween.add(getSquare(square1.x, y));
				}
			}
		
			//diagonal top left to bottom right
			else if(square1.x - square2.x == square1.y - square2.y){
				for(int i = 1; i + square1.x < square2.x; i++){
					squaresBetween.add(getSquare(square1.x + i, square1.y + i));
				}
			}
			
			//diagonal bottom left to top right
			else if(square1.x - square2.x == - square1.y + square2.y){
				for(int i = 1; i + square1.x < square2.x; i++){
					squaresBetween.add(getSquare(square1.x + i, square1.y - i));
				}
			}
		}
		
		
		
		return squaresBetween;
	}
	
	public boolean squaresBetweenAreEmpty(Square square1, Square square2){
		boolean empty = true;
		for(Square square : getSquaresInBetween(square1, square2)){
			empty = empty && square.isEmpty();
		}
		return empty;
	}

	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
	
}
