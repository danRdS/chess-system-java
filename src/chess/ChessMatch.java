package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	
	private Board board;
	
	public ChessMatch() {
		board = new Board(8, 8);
		initialSetup();
	}
	
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for(int i=0; i<board.getRows(); i++) {
			for(int j=0; j<board.getColumns(); j++) {
				mat[i][j] = (ChessPiece)board.piece(i, j);
			}
		}
		return mat;
	}
	
	/*m�todo pra tirar uma pe�a da posi��o de origem e coloc�-la na posi��o de destino*/
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		/*converter as duas posi��es para posi��es da matriz*/
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		/*Antes de fazer um movimento, devo verificar se na posi��o de origem(source) havia uma pe�a*/
		validateSourcePosition(source);	/*opera��o respons�vel por validar essa posi��o de origem, sen�o existir ela lan�ar� uma exce��o*/
		Piece capturedPiece = makeMove(source, target); /*makeMove � uma opera��o respons�vel por fazer o movimento da pe�a*/
		return (ChessPiece)capturedPiece; /*Downcasting pq 'capturedPiece' era do tipo 'Piece'*/
	}
	
	private Piece makeMove(Position source, Position target) {
		Piece p = board.removePiece(source);	/*removendo a pe�a que estava na posi��o de origem*/
		Piece capturePiece = board.removePiece(target); /*remove a possivel pe�a que est� na posi��o de destino e ela, por padr�o, 
		ser� a pe�a capturada.*/
		board.placePiece(p, target);			/*colocando a pe�a 'p' na posi��o de destino*/
		return capturePiece;
	}
	
	private void validateSourcePosition(Position position) {
		/*Se n�o existir uma pe�a nessa posi��o, vai retornar um erro*/
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if(!board.piece(position).IsThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}
	
	/*instancia as pe�as de xadrez informando as coordenadas no sistema do tabuleiro do xadrez e n�o da matriz pra n�o deixar confuso*/
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
	}
	
	private void initialSetup() {
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
		
}
