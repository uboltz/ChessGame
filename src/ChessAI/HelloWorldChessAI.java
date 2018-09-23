package ChessAI;
import java.util.List;
import java.util.Random;

import BoardGame.Board;
import Game.ChessBoard;
import Game.ChessPiece;

public class HelloWorldChessAI extends ChessAI{

	private Random random;
	
	public HelloWorldChessAI(){
		random = new Random();
	}
	
	@Override
	public Board<ChessPiece>.Move nextMove(ChessBoard board) {
		
		List<ChessBoard.Move> moves = board.getAllLegalMoves();
		
		return moves.get(random.nextInt(moves.size()));
	}

}
