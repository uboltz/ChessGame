package Game;
import java.util.ArrayList;
import java.util.List;

import BoardGame.Board;

public class ChessBoard extends Board<ChessPiece>{
	
	public enum Player {
		BLACK{
			public Player getOpponent(){
				return WHITE;
			}
		},
		
		WHITE{
			public Player getOpponent(){
				return BLACK;
			}
		};
		
		public abstract Player getOpponent();
	}
	
	public String name = "default name chess board";
	public Player activePlayer;
	
	//used to determine if castling is possible
	private boolean blackKingHasMoved, whiteKingHasMoved,
					blackKingHasBeenThreatened, whiteKingHasBeenThreatened,
					blackLeftRookHasMoved, blackRightRookHasMoved,
					whiteLeftRookHasMoved, whiteRightRookHasMoved;
	
	public ChessBoard(){
		super(8,8);
		
		activePlayer = Player.WHITE;
		preparePieces();
		
	}
	
	//copy constructor
	public ChessBoard(ChessBoard original){
		super(8,8);
		
		this.activePlayer = original.activePlayer;
		copySquares(original);	
	}
	
	protected void copySquares(ChessBoard original){
		
		for(int x = 1; x <= width; x++){
			for(int y = 1; y <= height; y++){
				
				Square originalSquare = original.getSquare(x, y);
				
				if(!originalSquare.isEmpty()){
					ChessPiece originalPiece = originalSquare.getPiece();
					ChessPiece newPiece = new ChessPiece(originalPiece.getPieceType(), originalPiece.getOwner());
					this.putPiece(newPiece, x, y);
				}
				 
			}
		}
		
	}
	
	public boolean isLegalMoveForActivePlayer(Board<ChessPiece>.Move move){
		return isLegalMove(move, activePlayer);
	}
	
	public boolean isLegalMove(Board<ChessPiece>.Move move, Player player){
		
		if(isLegalMoveCastling(move, player)){
			return true;
		}
		else if((isLegalMoveBasic(move, player))
				&& (kingWontBeThreatened(move, player))){
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean kingWontBeThreatened(Board<ChessPiece>.Move move, Player kingsOwner){
		ChessBoard board = new ChessBoard(this);
		board.name = "preview board";
		board.executeMove(move);
		return !board.kingIsThreatened(kingsOwner);
	}
	
	public boolean kingIsThreatened(Player kingsOwner){
				
		Square kingsSquare = null;
		
		//find the king
		for(Square square : squares){
			if(hasPieceOfPlayerOnIt(square, kingsOwner)){
				if(square.getPiece().getPieceType() == ChessPiece.Type.KING){
					kingsSquare = square;
					break;
				}
			}
		}
		
		if(kingsSquare == null){
			System.out.println("ERROR: At least one player does not have a king on the board");
			return false;
		}
		
		Player opponent = kingsOwner.getOpponent();
		
		
		return squareIsThreatened(kingsSquare, opponent);
	}
	
	private boolean squareIsThreatened(Square testedSquare, Player player){
		for(Square square : squares){
			if(hasPieceOfPlayerOnIt(square, player)){
				if(isLegalMoveBasic(new Move(square.x, square.y, testedSquare.x, testedSquare.y), player, true)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean isLegalMoveBasic(Board<ChessPiece>.Move move, Player player){
		return isLegalMoveBasic(move, player, false);
	}
	
	private boolean isLegalMoveBasic(Board<ChessPiece>.Move move, Player player, boolean checkingForThreats){
		
		Square startingSquare = getSquare(move.startX, move.startY);
		Square endSquare = getSquare(move.endX, move.endY);
		
		//check for out of bounds (should never happen anyway)
		if(outOfBounds(startingSquare)){
			return false;
		}
		if(outOfBounds(endSquare)){
			return false;
		}
		
		//check if there is a piece to move
		if(startingSquare.isEmpty()){
			return false;
		}
		
		//check if the piece to be moved belongs to the player attempting the move
		if(startingSquare.getPiece().getOwner() != player){
			return false;
		}
		
		//check if beginning and end of move are different
		if((startingSquare.x == endSquare.x) && (startingSquare.y == endSquare.y)){
			return false;
		}
		
		//check for player's own piece as target
		if(!endSquare.isEmpty()){
			if(endSquare.getPiece().getOwner() == startingSquare.getPiece().getOwner()){
				return false;
			}
		}
		
		ChessPiece piece = startingSquare.getPiece();
		
		if(piece.getPieceType() == ChessPiece.Type.PAWN){
			return isLegalMovePawn(move, checkingForThreats);
		}
		else if(piece.getPieceType() == ChessPiece.Type.ROOK){
			return isLegalMoveRook(move);
		}
		else if (piece.getPieceType() == ChessPiece.Type.KNIGHT){
			return isLegalMoveKnight(move);
		}
		else if (piece.getPieceType() == ChessPiece.Type.BISHOP){
			return isLegalMoveBishop(move);
		}
		else if (piece.getPieceType() == ChessPiece.Type.QUEEN){
			return isLegalMoveQueen(move);
		}
		else if (piece.getPieceType() == ChessPiece.Type.KING){
			return isLegalMoveKing(move);
		}
		
		//if we end up on this line, there is implementation for a piece missing
		return false;
	}
	
	private boolean isLegalMovePawn(Move move, boolean checkingForThreats){
		
		boolean isLegal = false;
		
		
		Square startingSquare = getSquare(move.startX, move.startY);
		Square endSquare = getSquare(move.endX, move.endY);
		int pawnMoveDirection = getPawnMoveDirection(startingSquare.getPiece().getOwner());
		
		if((endSquare.isEmpty())
				&& !checkingForThreats
				&& (startingSquare.x == endSquare.x)){
			
			if(endSquare.y == startingSquare.y + pawnMoveDirection){
				//move one square forward
				isLegal = true;
			}
			else if((endSquare.y == startingSquare.y + (2 * pawnMoveDirection))
					&& ((startingSquare.y == 1 + pawnMoveDirection) || (startingSquare.y == 8 + pawnMoveDirection))
					&& (squaresBetweenAreEmpty(startingSquare, endSquare))){
				//move two squares forward on a pawn's first move
				isLegal = true;
			}
		}
		else if ((!endSquare.isEmpty() && (endSquare.getPiece().getOwner() != activePlayer))
				|| checkingForThreats){
				
				//attack diagonally
				isLegal = (endSquare.y == startingSquare.y + pawnMoveDirection)
						&& ((endSquare.x == startingSquare.x + 1)
								|| (endSquare.x == startingSquare.x - 1));
			
		}
		
		
		return isLegal;
	}
	
	private boolean isLegalMoveRook(Move move){
		if(isStraightMove(move)){
			return squaresBetweenAreEmpty(move.getStartingSquare(this), move.getEndSquare(this));
			
		}
		else {
			return false;
		}
	}
	
	private boolean isLegalMoveKnight(Move move){
		int horizontalDistance = move.getStartingSquare(this).x - move.getEndSquare(this).x;
		int verticalDistance = move.getStartingSquare(this).y - move.getEndSquare(this).y;
		
		horizontalDistance = Math.abs(horizontalDistance);
		verticalDistance = Math.abs(verticalDistance);
		
		return ((horizontalDistance == 2) && (verticalDistance == 1))
				|| ((horizontalDistance == 1) && (verticalDistance == 2));
	}

	private boolean isLegalMoveBishop(Move move){
		if(isDiagonalMove(move)){
			return squaresBetweenAreEmpty(move.getStartingSquare(this), move.getEndSquare(this));
			
		}
		else {
			return false;
		}
	}
	
	private boolean isLegalMoveQueen(Move move){
		return isLegalMoveRook(move) || isLegalMoveBishop(move);
	}
	
	private boolean isLegalMoveKing(Move move){
		
		return isLegalMoveQueen(move)
				&& (Math.abs(move.getStartingSquare(this).x - move.getEndSquare(this).x) <= 1)
				&& (Math.abs(move.getStartingSquare(this).y - move.getEndSquare(this).y) <= 1);
		
		
	}
	
	private boolean isLegalMoveCastling(Move move, Player player){
		Square startingSquare = move.getStartingSquare(this);
		Square endSquare = move.getEndSquare(this);
		
		if(startingSquare.isEmpty() || endSquare.isEmpty()){
			return false;
		}
		else if(startingSquare.getPiece().getOwner() != player){
			return false;
		}
		
		
		if(player == Player.BLACK){
			if((startingSquare.getPiece().getPieceType() == ChessPiece.Type.KING)
					&& !blackKingHasMoved
					&& !blackKingHasBeenThreatened){
				if((endSquare.y == startingSquare.y)
						&& (endSquare.getPiece().getPieceType() == ChessPiece.Type.ROOK)
						&& (squaresBetweenAreEmpty(startingSquare, endSquare))
						&& !squaresBetweenAreThreatened(startingSquare, endSquare, player.getOpponent())){
					
					if((endSquare.x == 1) && (!blackLeftRookHasMoved)){
						return true;
					}
					else if((endSquare.x == 8) && (!blackRightRookHasMoved)){
						return true;
					}
				}
			}
		}
		else if(player == Player.WHITE){
			if((startingSquare.getPiece().getPieceType() == ChessPiece.Type.KING)
					&& !whiteKingHasMoved
					&& !whiteKingHasBeenThreatened){
				if((endSquare.y == startingSquare.y)
						&& (endSquare.getPiece().getPieceType() == ChessPiece.Type.ROOK)
						&& (squaresBetweenAreEmpty(startingSquare, endSquare))
						&& !squaresBetweenAreThreatened(startingSquare, endSquare, player.getOpponent())){
					
					if((endSquare.x == 1) && (!whiteLeftRookHasMoved)){
						return true;
					}
					else if((endSquare.x == 8) && (!whiteRightRookHasMoved)){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	
	private boolean squaresBetweenAreThreatened(Square square1, Square square2, Player player){
		for(Square square : getSquaresInBetween(square1, square2)){
			if(squareIsThreatened(square, player)){
				return true;
			}
		}
		return false;
	}
	
	//white always plays from bottom to top
	private int getPawnMoveDirection(Player player){
		if(player == Player.WHITE){
			return -1;
		}
		else {
			return 1;
		}
	}
	
	public void executeMove(ChessBoard.Move move){
		
		if(isLegalMoveCastling(move, activePlayer)){
			executeCastling(move);
		}
		else {
			movePiece(move);
		}
		
		updateCastlingRequirements(move);
		
		checkForPawnsAtEndOfBoard();
		
		activePlayer = activePlayer.getOpponent();
//		System.out.println("active player ( " + this.name + "): " + activePlayer.name());
	}
	
	public void movePiece(ChessBoard.Move move){
		
		ChessBoard.Square startingSquare = getSquare(move.startX, move.startY);
		ChessBoard.Square endSquare = getSquare(move.endX, move.endY);
		
//		System.out.println("moving piece: "
//				+ startingSquare.getPiece().getPieceType().name()
//				+ startingSquare.x
//				+ "," + startingSquare.y
//				+ " " + endSquare.x
//				+ "," + endSquare.y);
		
		ChessPiece piece = startingSquare.getPiece();
		startingSquare.removePiece();
		endSquare.setPiece(piece);
		
	}
	
	private void executeCastling(Move move){
		int y = move.startY;
		int kingX = move.startX;
		int rookX = move.endX;
		int newKingX, newRookX;
		
		if(rookX == 1){
			newKingX = kingX - 2;
			newRookX = kingX - 1;
		}
		else {
			newKingX = kingX + 2;
			newRookX = kingX + 1;
		}
		
		movePiece(new Move(kingX, y, newKingX, y));
		movePiece(new Move(rookX, y, newRookX, y));
		
	}
	
	private void updateCastlingRequirements(ChessBoard.Move move){
		updateCastlingRequirementsMovement(move);
		updateCastlingRequirementsKingThreatened(move);
	}
	
	private void updateCastlingRequirementsKingThreatened(Move move){
		blackKingHasBeenThreatened = blackKingHasBeenThreatened || kingIsThreatened(Player.BLACK);
		whiteKingHasBeenThreatened = whiteKingHasBeenThreatened || kingIsThreatened(Player.WHITE);
	}
	
	private void updateCastlingRequirementsMovement(ChessBoard.Move move){
		
		Square startingSquare = move.getStartingSquare(this);
		
		if(!startingSquare.isEmpty()){
			
			ChessPiece piece = startingSquare.getPiece();
		
			if(piece.getPieceType() == ChessPiece.Type.KING){
				if(piece.getOwner() == Player.BLACK){
					blackKingHasMoved = true;
				}
				else if(piece.getOwner() == Player.WHITE){
					whiteKingHasMoved = true;
				}
			}
			else if(piece.getPieceType() == ChessPiece.Type.ROOK){
				if(piece.getOwner() == Player.BLACK){
					if(startingSquare.x == 1){
						blackLeftRookHasMoved = true;
					}
					else if(startingSquare.x == 8){
						blackRightRookHasMoved = true;
					}
				}
				else if(piece.getOwner() == Player.WHITE){
					if(startingSquare.x == 1){
						whiteLeftRookHasMoved = true;
					}
					else if(startingSquare.x == 8){
						whiteRightRookHasMoved = true;
					}
				}
			}
			
		}
		
	}
	
	private void checkForPawnsAtEndOfBoard(){
		checkForPawnsInRow(1);
		checkForPawnsInRow(height);
	}
	
	private void checkForPawnsInRow(int y){
		for(int x = 1; x <= width; x++){
			Square square = getSquare(x,y);
			if(!square.isEmpty()
					&& (square.getPiece().getPieceType() == ChessPiece.Type.PAWN)){
				putPiece(new ChessPiece(selectPiece(), square.getPiece().getOwner()), x, y);
			}
		}
	}
	
	//TODO make selection possible
	private ChessPiece.Type selectPiece(){
		return ChessPiece.Type.QUEEN;
	}
	
	public void setUpNewGame(){
		clearPieces();
		preparePieces();
		resetCastlingRequirements();
		activePlayer = Player.WHITE;
	}
	
	private void resetCastlingRequirements(){
		blackKingHasMoved = false;
		whiteKingHasMoved = false;
		blackKingHasBeenThreatened = false;
		whiteKingHasBeenThreatened = false;
		blackLeftRookHasMoved = false;
		blackRightRookHasMoved = false;
		whiteLeftRookHasMoved = false;
		whiteRightRookHasMoved = false;
	}
	
	public void preparePieces(){
		
		
		for(int x = 1; x <= 8; x++){
			putPiece(new ChessPiece(ChessPiece.Type.PAWN, Player.BLACK), x, 2);
		}
		
		for(int x = 1; x <= 8; x++){
			putPiece(new ChessPiece(ChessPiece.Type.PAWN, Player.WHITE), x, 7);
		}
		
		putPiece(new ChessPiece(ChessPiece.Type.ROOK, Player.BLACK), 1, 1);
		putPiece(new ChessPiece(ChessPiece.Type.ROOK, Player.BLACK), 8, 1);
		putPiece(new ChessPiece(ChessPiece.Type.KNIGHT, Player.BLACK), 2, 1);
		putPiece(new ChessPiece(ChessPiece.Type.KNIGHT, Player.BLACK), 7, 1);
		putPiece(new ChessPiece(ChessPiece.Type.BISHOP, Player.BLACK), 3, 1);
		putPiece(new ChessPiece(ChessPiece.Type.BISHOP, Player.BLACK), 6, 1);
		putPiece(new ChessPiece(ChessPiece.Type.QUEEN, Player.BLACK), 4, 1);
		putPiece(new ChessPiece(ChessPiece.Type.KING, Player.BLACK), 5, 1);
		
		putPiece(new ChessPiece(ChessPiece.Type.ROOK, Player.WHITE), 1, 8);
		putPiece(new ChessPiece(ChessPiece.Type.ROOK, Player.WHITE), 8, 8);
		putPiece(new ChessPiece(ChessPiece.Type.KNIGHT, Player.WHITE), 2, 8);
		putPiece(new ChessPiece(ChessPiece.Type.KNIGHT, Player.WHITE), 7, 8);
		putPiece(new ChessPiece(ChessPiece.Type.BISHOP, Player.WHITE), 3, 8);
		putPiece(new ChessPiece(ChessPiece.Type.BISHOP, Player.WHITE), 6, 8);
		putPiece(new ChessPiece(ChessPiece.Type.QUEEN, Player.WHITE), 4, 8);
		putPiece(new ChessPiece(ChessPiece.Type.KING, Player.WHITE), 5, 8);
	}
	
	public List<Move> getAllLegalMoves(){
		
		List<Move> legalMoves = new ArrayList<Move>();
		
		for(Square startingSquare : squares){
			for(Square endSquare : squares){
				Move move = new Move(startingSquare, endSquare);
				if(isLegalMove(move, activePlayer)){
					legalMoves.add(move);
				}
			}
		}
		return legalMoves;
	}
	
	public void highlightAllLegalMoves(Square startingSquare){
		for(Square endSquare : squares){
			if(isLegalMove(new Move(startingSquare, endSquare), activePlayer)){
				endSquare.highlighted = true;
			}
		}
	}
	
	private boolean hasPieceOfPlayerOnIt(Square square, Player player){
		if(square.isEmpty()){
			return false;
		}
		else{
			return square.getPiece().getOwner() == player;
		}
	}
	
	public boolean checkMate(){
		return (getAllLegalMoves().isEmpty()) && kingIsThreatened(activePlayer);
	}
	
	public boolean draw(){
		return (getAllLegalMoves().isEmpty()) && !kingIsThreatened(activePlayer);
	}
	
	
}
