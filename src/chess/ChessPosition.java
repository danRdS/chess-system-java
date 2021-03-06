package chess;

import boardgame.Position;

public class ChessPosition {

	private char column;
	private int row;
	
	public ChessPosition(char column, int row) {
		if(column < 'a' || column > 'h' || row < 1 || row >8) {
			throw new ChessException("Error instantiating ChessPosition. Valid values are from a1 to h8.");
		}
		this.column = column;
		this.row = row;
	}

	public char getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}
	/*setRow e setColumn n?o ser?o usados pra n?o permitir que linha e coluna sejam alterados livremente*/
	
	protected Position toPosition() {
		return new Position(8 - row, column - 'a');
	}
	/*toPosition() ? um m?todo para converter essa posi??o do tabuleiro de xadrez pra uma posi??o comum de matriz*/
	
	protected static ChessPosition fromPosition(Position position) {
		/*pega o 'position' de matriz e converte pra 'position' de xadrez*/
		return new ChessPosition((char)('a' + position.getColumn()), 8 - position.getRow());
	}
	 
	@Override
	public String toString() {
		return  "" + column + row;
	}
}
