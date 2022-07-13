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
		/*Pra saber se o rei pode mover pra essa posição 1º pega a peça 'p' que estiver nessa posição*/
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		/*verificar se essa peça p é nula ou se é uma peça adversária(se a cor da peça for diferente da do rei). No dois casos
		 * poderá mover a peça pra qq uma das posições. */
		return p == null || p.getColor() != getColor();
	}
	
	private boolean testRookCastling(Position position) {
		/*testa se a torre está apta para o Castlings. Ela está apta qdo sua qtde de movimentos é igual a 0*/
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		return p != null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getRows()];/*criou uma matriz booleana da mesma dimensão
		 do tabuleiro. Por padrão, todas as posições dessa matriz começam com falso*/ 
		
		Position p = new Position(0, 0);
		
		// ABOVE
		/*Pega essa posição 'p' e definir pra ela os valores das posiões acima do Rei. Que seria a posição do rei 'position.getRow()'
		 * 'position.getColumn()'*/
		p.setValues(position.getRow() - 1, position.getColumn());
		if(getBoard().positionExists(p) && canMove(p)) {
			/*se isso acontecer, o rei(K) pode mover pra essa posição 'p'*/
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
			/*O rei está apto ao castling desde que não tenha feito nenhum movimento e a partida não pode estar em check*/
			
			// #specialmove castling kingside rook
			Position posT1 = new Position(position.getRow(), position.getColumn() + 3);/*posT1 = posição da torre 1, que fica a 3 casas
			a direita do rei*/
			if(testRookCastling(posT1)) {
				/*testa se nessa posição tem uma torre apta pra castling*/
				Position p1 = new Position(position.getRow(), position.getColumn() + 1);
				Position p2 = new Position(position.getRow(), position.getColumn() + 2);
				if(getBoard().piece(p1) == null && getBoard().piece(p2) == null) {
					/*testa se as duas posições entre o rei e a torre da direita estão vazias*/
					mat[position.getRow()][position.getColumn() + 2] = true;
					/*se todas as condições forem verdadeiras, então o movimento do rei pra duas casas a direita pode 
					 * ser feito(Castling pequeno)*/
				}
			}
			
			// specialmove castling queenside rook
			Position posT2 = new Position(position.getRow(), position.getColumn() - 4);/*posT1 = posição da torre 1, que fica a 4 casas
			a direita do rei*/
			if(testRookCastling(posT2)) {
				/*testa se nessa posição tem uma torre apta pra castling*/
				Position p1 = new Position(position.getRow(), position.getColumn() - 1);
				Position p2 = new Position(position.getRow(), position.getColumn() - 2);
				Position p3 = new Position(position.getRow(), position.getColumn() - 3);
				if(getBoard().piece(p1) == null && getBoard().piece(p2) == null && getBoard().piece(p3) == null) {
					/*testa se as três posições entre o rei e a torre da esquerda estão vazias*/
					mat[position.getRow()][position.getColumn() - 2] = true;
		
				}
			}
		}
		return mat;
	}
}
