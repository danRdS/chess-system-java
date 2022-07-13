package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece{

	private ChessMatch chessMatch; /*porque o Rei precisa ter acesso a partida por causa da jogada especial 'Roque(Castling)'*/
	
	public King(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public String toString() {
		return "K";
	}

	private boolean canMove(Position position) {
		/*Pra saber se o rei pode mover pra essa posi��o 1� pega a pe�a 'p' que estiver nessa posi��o*/
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		/*verificar se essa pe�a p � nula ou se � uma pe�a advers�ria(se a cor da pe�a for diferente da do rei). No dois casos
		 * poder� mover a pe�a pra qq uma das posi��es. */
		return p == null || p.getColor() != getColor();
	}
	
	private boolean testRookCastling(Position position) {
		/*testa se a torre est� apta para o Castlings. Ela est� apta qdo sua qtde de movimentos � igual a 0*/
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		return p != null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getRows()];/*criou uma matriz booleana da mesma dimens�o
		 do tabuleiro. Por padr�o, todas as posi��es dessa matriz come�am com falso*/ 
		
		Position p = new Position(0, 0);
		
		// ABOVE
		/*Pega essa posi��o 'p' e definir pra ela os valores das posi�es acima do Rei. Que seria a posi��o do rei 'position.getRow()'
		 * 'position.getColumn()'*/
		p.setValues(position.getRow() - 1, position.getColumn());
		if(getBoard().positionExists(p) && canMove(p)) {
			/*se isso acontecer, o rei(K) pode mover pra essa posi��o 'p'*/
			mat[p.getRow()][p.getColumn()] = true;
		}
		// BELOW
		p.setValues(position.getRow() + 1, position.getColumn());
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// LEFT
		p.setValues(position.getRow(), position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// RIGHT
		p.setValues(position.getRow(), position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// NW
		p.setValues(position.getRow() - 1, position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// NE
		p.setValues(position.getRow() - 1, position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// SW
		p.setValues(position.getRow() + 1, position.getColumn() - 1);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// SE
		p.setValues(position.getRow() + 1, position.getColumn() + 1);
		if(getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		// #specialmove castling
		if(getMoveCount() == 0 && !chessMatch.getCheck()) {
			/*O rei est� apto ao castling desde que n�o tenha feito nenhum movimento e a partida n�o pode estar em check*/
			
			// #specialmove castling kingside rook
			Position posT1 = new Position(position.getRow(), position.getColumn() + 3);/*posT1 = posi��o da torre 1, que fica a 3 casas
			a direita do rei*/
			if(testRookCastling(posT1)) {
				/*testa se nessa posi��o tem uma torre apta pra castling*/
				Position p1 = new Position(position.getRow(), position.getColumn() + 1);
				Position p2 = new Position(position.getRow(), position.getColumn() + 2);
				if(getBoard().piece(p1) == null && getBoard().piece(p2) == null) {
					/*testa se as duas posi��es entre o rei e a torre da direita est�o vazias*/
					mat[position.getRow()][position.getColumn() + 2] = true;
					/*se todas as condi��es forem verdadeiras, ent�o o movimento do rei pra duas casas a direita pode 
					 * ser feito(Castling pequeno)*/
				}
			}
			
			// specialmove castling queenside rook
			Position posT2 = new Position(position.getRow(), position.getColumn() - 4);/*posT1 = posi��o da torre 1, que fica a 4 casas
			a direita do rei*/
			if(testRookCastling(posT2)) {
				/*testa se nessa posi��o tem uma torre apta pra castling*/
				Position p1 = new Position(position.getRow(), position.getColumn() - 1);
				Position p2 = new Position(position.getRow(), position.getColumn() - 2);
				Position p3 = new Position(position.getRow(), position.getColumn() - 3);
				if(getBoard().piece(p1) == null && getBoard().piece(p2) == null && getBoard().piece(p3) == null) {
					/*testa se as tr�s posi��es entre o rei e a torre da esquerda est�o vazias*/
					mat[position.getRow()][position.getColumn() - 2] = true;
		
				}
			}
		}
		return mat;
	}
}
