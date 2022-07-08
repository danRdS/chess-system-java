package boardgame;

public class Board {
	private int rows;
	private int columns;
	private Piece[][] pieces;
	
	public Board(int rows, int columns) {
		if(rows <1 && columns <1) {
			throw new BoardException("Error creating board: there must be at least 1 row and 1 column");
		}/*Criando tratamento de exce��o com programa��o defensiva. Pra que haja tabuleiro, deve ter pelo menos 1 linha e 1 coluna.
		Se n�o tiver, lan�ar a msgm de exce��o*/
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece [rows][columns];
	}

	public int getRows() {
		return rows;
	}
	
	/*Foi retirado o metodo 'setRows' e 'setColumns', pq n�o posso permitir que sejam alteradas as linhas e colunas do tabuleiro*/

	public int getColumns() {
		return columns;
	}

	public Piece piece(int row, int column) {
		if(!positionExists(row, column)) {
			throw new BoardException("Position not on the board");
		}
		/*Ao acessar uma pe�a de uma linha de uma determinada coluna:
		 * Vou lan�ar(throw) uma nova exce��o */
		return pieces[row][column];
	}
	
	public Piece piece(Position position) {
		if(!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		return pieces[position.getRow()][position.getColumn()];
	}
	
	/*m�todo para colocar uma pe�a 'placePiece' em uma determinada posi��o*/
	public void placePiece(Piece piece, Position position) {
		/*Testar se j� existe uma pe�a na posi��o. Se j� tiver, n�o posso permitir que coloque outra pe�a l�*/
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
		/*Neste 'if', se a pe�a estiver numa posi��o que est� vazia(null), significa que n�o tem nenhuma pe�a nessa posi��o. Ent�o retorna o 
		 * valor nulo. Se n�o acontecer, ai faz o procedimento de retirar a pe�a do tabuleiro.*/
		
		Piece aux = piece(position);	/*A vari�vel 'aux' vai receber uma pe�a que est� nesta posi��o*/
		aux.position = null; 			/*pe�a retirada do tabuleiro. Essa pe�a n�o tem posi��o mais.*/
		pieces[position.getRow()][position.getColumn()] = null;	/*Essa matriz de pe�as nesta linha e coluna. Essa matriz de pe�as na posi��o
		onde estou removendo a pe�a, agora vai ser nulo, indicando que n�o tem mais pe�a nessa posi��o.*/
		
		return aux; 					/*retorna a vari�vel que cont�m a pe�a que foi retirada.*/
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
