package boardgame;

public class Board {
	private int rows;
	private int columns;
	private Piece[][] pieces;
	
	public Board(int rows, int columns) {
		if(rows <1 && columns <1) {
			throw new BoardException("Error creating board: there must be at least 1 row and 1 column");
		}/*Criando tratamento de exceção com programação defensiva. Pra que haja tabuleiro, deve ter pelo menos 1 linha e 1 coluna.
		Se não tiver, lançar a msgm de exceção*/
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece [rows][columns];
	}

	public int getRows() {
		return rows;
	}
	
	/*Foi retirado o metodo 'setRows' e 'setColumns', pq não posso permitir que sejam alteradas as linhas e colunas do tabuleiro*/

	public int getColumns() {
		return columns;
	}

	public Piece piece(int row, int column) {
		if(!positionExists(row, column)) {
			throw new BoardException("Position not on the board");
		}
		/*Ao acessar uma peça de uma linha de uma determinada coluna:
		 * Vou lançar(throw) uma nova exceção */
		return pieces[row][column];
	}
	
	public Piece piece(Position position) {
		if(!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		return pieces[position.getRow()][position.getColumn()];
	}
	
	/*método para colocar uma peça 'placePiece' em uma determinada posição*/
	public void placePiece(Piece piece, Position position) {
		/*Testar se já existe uma peça na posição. Se já tiver, não posso permitir que coloque outra peça lá*/
		if(thereIsAPiece(position)) {
			throw new BoardException("There is already a piece on position " + position);	
		}
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.position= position;
	}
	
	public Piece removePiece(Position position) {
		if(!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		if(piece(position) == null) {
			return null;
		}
		/*Neste 'if', se a peça estiver numa posição que está vazia(null), significa que não tem nenhuma peça nessa posição. Então retorna o 
		 * valor nulo. Se não acontecer, ai faz o procedimento de retirar a peça do tabuleiro.*/
		
		Piece aux = piece(position);	/*A variável 'aux' vai receber uma peça que está nesta posição*/
		aux.position = null; 			/*peça retirada do tabuleiro. Essa peça não tem posição mais.*/
		pieces[position.getRow()][position.getColumn()] = null;	/*Essa matriz de peças nesta linha e coluna. Essa matriz de peças na posição
		onde estou removendo a peça, agora vai ser nulo, indicando que não tem mais peça nessa posição.*/
		
		return aux; 					/*retorna a variável que contém a peça que foi retirada.*/
	}
	
	private boolean positionExists(int row, int column) {
		return row >= 0 && row < rows && column >= 0 && column < columns;
	}
	
	public boolean positionExists(Position position) {
		return positionExists(position.getRow(), position.getColumn());
	}
	
	public boolean thereIsAPiece(Position position) {
		if(!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		return piece(position) != null;
	}
	
}
