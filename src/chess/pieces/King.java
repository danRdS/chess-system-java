package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece{

	public King(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "K";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getRows()];/*criou uma matriz booleana da mesma dimensão
		 do tabuleiro. Por padrão, todas as posições dessa matriz começam com falso*/ 
		return mat;
	}
}
