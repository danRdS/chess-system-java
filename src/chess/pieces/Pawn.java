package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece{

	public Pawn(Board board, Color color) {
		super(board, color);
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];/*criou uma matriz auxiliar booleana da mesma dimensão
		 do tabuleiro. Por padrão, todas as posições dessa matriz começam com falso*/ 
		
		Position p = new Position(0, 0);
		
		if(getColor() == Color.WHITE) {
			p.setValues(position.getRow() - 1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			/*o peão pode andar uma casa pra frente*/
			p.setValues(position.getRow() - 2, position.getColumn());
			Position p2 = new Position(position.getRow() - 1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2)&& getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			/*pode andar duas casas pra frente apenas no primeiro movimento*/
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			if(getBoard().positionExists(p) && IsThereOpponentPiece(p)) {	/*testa se a posião existe e se tem uma peça adversária lá*/
				mat[p.getRow()][p.getColumn()] = true;	/*se tiver, a peça pode se mover pra lá*/
			}
			/*pode capturar uma peça adversaria que esta na diagonal esquerda*/
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			if(getBoard().positionExists(p) && IsThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			/*pode capturar uma peça adversaria que esta na diagonal direita*/
		}
		else{
			p.setValues(position.getRow() + 1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			/*o peão pode andar uma casa pra frente*/
			p.setValues(position.getRow() + 2, position.getColumn());
			Position p2 = new Position(position.getRow() + 1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2)&& getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			/*pode andar duas casas pra frente apenas no primeiro movimento*/
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			if(getBoard().positionExists(p) && IsThereOpponentPiece(p)) {/*testa se a posião existe e se tem uma peça adversária lá*/
				mat[p.getRow()][p.getColumn()] = true;
			}
			/*pode capturar uma peça adversaria que esta na diagonal esquerda*/
			p.setValues(position.getRow() + 1, position.getColumn() + 1);
			if(getBoard().positionExists(p) && IsThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
		}
		return mat;
	}
	
	@Override
	public String toString() {
		return "P";
	}
	
	
}
