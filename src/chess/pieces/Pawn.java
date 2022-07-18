package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece{

	private ChessMatch chessMatch;
	
	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
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
			
			// #specialmove en passant white
			if(position.getRow() == 3) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				if(getBoard().positionExists(left) && IsThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable())  {
				/*verifica se a posição existe && se a peça é do oponente && se a peça tá que está lá é vulnerável ao en passant*/
				/*se for 'v', o peão pode capturar a peça que está na posição 'left'*/
					mat[left.getRow() - 1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() + 1);
				if(getBoard().positionExists(right) && IsThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable())  {
				/*verifica se a posição existe && se a peça é do oponente && se a peça tá que está lá é vulnerável ao en passant*/
				/*se for 'v', o peão pode capturar a peça que está na posição 'left'*/
					mat[right.getRow() - 1][right.getColumn()] = true;
				}
			}
			
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
			
			// #specialmove en passant black
			if(position.getRow() == 4) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				if(getBoard().positionExists(left) && IsThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable())  {
				/*verifica se a posição existe && se a peça é do oponente && se a peça tá que está lá é vulnerável ao en passant*/
				/*se for 'v', o peão pode capturar a peça que está na posição 'left'*/
					mat[left.getRow() + 1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() + 1);
				if(getBoard().positionExists(right) && IsThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable())  {
				/*verifica se a posição existe && se a peça é do oponente && se a peça tá que está lá é vulnerável ao en passant*/
				/*se for 'v', o peão pode capturar a peça que está na posição 'left'*/
					mat[right.getRow() + 1][right.getColumn()] = true;
				}
			}
		}
		return mat;
	}
	
	@Override
	public String toString() {
		return "P";
	}
	
}
