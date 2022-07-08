package boardgame;

public abstract class Piece {
	
	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
		position = null;
	}
	
	protected Board getBoard() {
		return board;
	}
	
	public abstract boolean[][] possibleMoves();
	
	public boolean PossibleMove(Position position) {
		return possibleMoves()[position.getRow()][position.getColumn()];
	}/*hook methods - método que faz um gancho com a subclasse(um método concreto que utilizou um abstrato)*/
	
	public boolean IsThereAnyPossibleMove() {
		boolean [][] mat = possibleMoves(); /*variável auxiliar mat que recebe o possibleMoves()-método abstrato-*/
		/*essa matriz booleana precisa ser percorrida */
		for(int i=0; i<mat.length; i++) {
			for(int j=0; j<mat.length; j++) {
				if(mat[i][j]) {
					return true; /*se esse movimento for possível, então retorna verdadeiro*/
				}
			}
		}
		return false;/*se na varredura da matriz não encontrar true em nenhum lugar, então nenhuma posição era verdadeira*/
	}
}
