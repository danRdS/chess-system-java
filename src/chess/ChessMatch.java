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
	
	/*método pra tirar uma peça da posição de origem e colocá-la na posição de destino*/
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		/*converter as duas posições para posições da matriz*/
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		/*Antes de fazer um movimento, devo verificar se na posição de origem(source) havia uma peça*/
		validateSourcePosition(source);	/*operação responsável por validar essa posição de origem, senão existir ela lançará uma exceção*/
		Piece capturedPiece = makeMove(source, target); /*makeMove é uma operação responsável por fazer o movimento da peça*/
		return (ChessPiece)capturedPiece; /*Downcasting pq 'capturedPiece' era do tipo 'Piece'*/
	}
	
	private Piece makeMove(Position source, Position target) {
		Piece p = board.removePiece(source);	/*removendo a peça que estava na posição de origem*/
		Piece capturePiece = board.removePiece(target); /*remove a possivel peça que está na posição de destino e ela, por padrão, 
		será a peça capturada.*/
		board.placePiece(p, target);			/*colocando a peça 'p' na posição de destino*/
		return capturePiece;
	}
	
	private void validateSourcePosition(Position position) {
		/*Se não existir uma peça nessa posição, vai retornar um erro*/
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if(!board.piece(position).IsThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}
	
	/*instancia as peças de xadrez informando as coordenadas no sistema do tabuleiro do xadrez e não da matriz pra não deixar confuso*/
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
