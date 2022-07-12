package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece{

	public Knight(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "N";
	}

	private boolean canMove(Position position) {
		/*Pra saber se o cavalo pode mover pra essa posição 1º pega a peça 'p' que estiver nessa posição*/
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		/*verificar se essa peça p é nula ou se é uma peça adversária(se a cor da peça for diferente da do cavalo). No dois casos
		 * poderá mover a peça pra qq uma das posições. */
		return p == null || p.getColor() != getColor();
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getRows()];/*criou uma matriz booleana da mesma dimensão
		 do tabuleiro. Por padrão, todas as posições dessa matriz começam com falso*/ 
		
		Position p = new Position(0, 0);
		
		/*Pega essa posição 'p' e definir pra ela os valores das posiões acima do cavalo. Que seria a posição do cavalo 'position.getRow()'
		 * 'position.getColumn()'*/
		p.setValues(position.getRow() - 1, position.getColumn() - 2);
		if(getBoard().positionExists(p) && canMove(p)) {
			/*se isso acontecer, o cavalo(N) pode mover pra essa posição 'p'*/
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() - 2, position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		p.setValues(position.getRow() - 2, position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() - 1, position.getColumn() + 2);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() + 1, position.getColumn() + 2);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() + 2, position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() + 2, position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow() + 1, position.getColumn() - 2);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		return mat;
	}
}
