package ChessAI;
import java.util.List;

import BoardGame.Board;
import Game.ChessBoard;
import Game.ChessPiece;

public class MinMaxChessAI extends ChessAI 
{

	public int searchDepth = 3;
	
	private ChessBoard.Player player;
	private int evaluationCount;
	
	@Override
	public Board<ChessPiece>.Move nextMove(ChessBoard board) {
		
		player = board.activePlayer;
		evaluationCount = 0;
		long time = System.currentTimeMillis();
		
		ChessBoard.Move move = getBestMove(board);
		
		System.out.println("elapsed time (ms): " + (System.currentTimeMillis() - time));
		return move;
	}
	
	private ChessBoard.Move getBestMove(ChessBoard board){
		
		List<ChessBoard.Move> legalMoves = board.getAllLegalMoves();
		ChessBoard.Move bestMove = legalMoves.get(0);
		int bestMoveScore = 0;
		
		for(ChessBoard.Move move : legalMoves){
			
			ChessBoard nextBoard = new ChessBoard(board);
			nextBoard.executeMove(move);
			int nextScore = recursiveEvaluation(nextBoard, 1);
			
			System.out.println(move.startX + "," + move.startY + " " + move.endX + "," + move.endY + " : " + nextScore);
			
			if(nextScore > bestMoveScore){
				bestMove = move;
				bestMoveScore = nextScore;
			}
				
		}
		System.out.println("evaluation count: " + evaluationCount);
		return bestMove;
	}
	
	
	private int recursiveEvaluation(ChessBoard board, int recursionCount){
		if(recursionCount >= searchDepth){
			evaluationCount++;
			return evaluate(board, player);
		}
		else {
			
			int score;
			
			//TODO this seems like a bad hack
			if(board.activePlayer == player){
				score = -2000;
			}
			else {
				score = 2000;
			}
			
			System.out.println("legal moves: " + board.getAllLegalMoves().size());
			
			for(ChessBoard.Move move : board.getAllLegalMoves()){
				
				ChessBoard nextBoard = new ChessBoard(board);
				nextBoard.executeMove(move);
				int nextScore = recursiveEvaluation(nextBoard, recursionCount + 1);
				
//				System.out.println(recursionCount + " next score: " + nextScore);
				
				if(board.activePlayer == player){
					//our turn; we get to choose the highest score
					score = Math.max(score, nextScore);
				}
				else {
					//opponents turn; he will choose the worst (lowest) score for us
					score = Math.min(score, nextScore);
				}
				
//				System.out.println(recursionCount + " min max score: " + score);
			}
			
			System.out.println(recursionCount + " returned score: " + score);
			return score;
		}
	}
	
	
	public int evaluate(ChessBoard board, ChessBoard.Player player){
		
		
		if(board.draw()){
			return 0;
		}
		else if(board.checkMate()){
			if(board.activePlayer == player){
				return 1000;
			}
			else {
				return -1000;
			}
		}
		else {
			int score = 0;

			for(int y = 1; y <= board.getHeight(); y++){
				for(int x = 1; x <= board.getWidth(); x++){
					ChessBoard.Square square = board.getSquare(x, y);
					if(!square.isEmpty()){
						ChessPiece piece = square.getPiece();
						if(piece.getOwner() == player){
							score = score + getValue(piece);
						}
						else {
							score = score - getValue(piece);
						}
					}
				}
			}
			
//			System.out.println("score: " + score);
			return score;
		}
	
	}
	
	private int getValue(ChessPiece piece){
		switch(piece.getPieceType()){
		case PAWN : return 1;
		case ROOK : return 7;
		case KNIGHT : return 5;
		case BISHOP : return 5;
		case QUEEN : return 10;
		case KING : return 0;
		default : return 0;
		}
	}

}
