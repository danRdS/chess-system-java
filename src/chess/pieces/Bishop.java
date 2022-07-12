package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Bishop extends ChessPiece{

	public Bishop(Board board, Color color) {
		super(board, color);
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];/*criou uma matriz booleana da mesma dimens�o
		 do tabuleiro. Por padr�o, todas as posi��es dessa matriz come�am com falso*/ 
		
		Position p = new Position(0, 0);
		// NE
		p.setValues(position.getRow() - 1, position.getColumn() + 1);
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setValues(p.getRow() - 1, p.getColumn() + 1);
		}
		if(getBoard().positionExists(p) && IsThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// NW
		p.setValues(position.getRow() - 1, position.getColumn() -1);
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setValues(p.getRow() - 1, p.getColumn() - 1);
		}
		if(getBoard().positionExists(p) && IsThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// SE
		p.setValues(position.getRow() + 1, position.getColumn() + 1);
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setValues(p.getRow() + 1, p.getColumn() + 1);;
		}
		if(getBoard().positionExists(p) && IsThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// SW
		p.setValues(position.getRow() + 1, position.getColumn() - 1);
		while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setValues(p.getRow() + 1, p.getColumn() - 1);;
		}
		if(getBoard().positionExists(p) && IsThereOpponentPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		return mat;
	}

	@Override
	public String toString() {
		return "B";
	}
}
