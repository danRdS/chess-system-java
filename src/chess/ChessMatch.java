package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
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
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition){
		Position position = sourcePosition.toPosition();	/*convertendo a posi��o de xadrez pra uma posi��o de matriz normal*/
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();		/*retornar os movimentos poss�veis da pe�a dessa posi��o*/
	}
	
	/*m�todo pra tirar uma pe�a da posi��o de origem e coloc�-la na posi��o de destino*/
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		/*converter as duas posi��es para posi��es da matriz*/
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		/*Antes de fazer um movimento, devo verificar se na posi��o de origem(source) havia uma pe�a*/
		validateSourcePosition(source);	/*opera��o respons�vel por validar essa posi��o de origem, sen�o existir ela lan�ar� uma exce��o*/
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target); /*makeMove � uma opera��o respons�vel por fazer o movimento da pe�a*/
		
		if(testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		nextTurn();
		return (ChessPiece)capturedPiece; /*Downcasting pq 'capturedPiece' era do tipo 'Piece'*/
	}
	
	private Piece makeMove(Position source, Position target) {
		Piece p = board.removePiece(source);	/*removendo a pe�a que estava na posi��o de origem*/
		Piece capturedPiece = board.removePiece(target); /*remove a possivel pe�a que est� na posi��o de destino e ela, por padr�o, 
		ser� a pe�a capturada.*/
		board.placePiece(p, target);			/*colocando a pe�a 'p' na posi��o de destino*/
		
		if(capturedPiece != null) {
			/*se a pe�a capturada for diferente de nulo, siginifa que capturei uma pe�a*/
			piecesOnTheBoard.remove(capturedPiece); /*remove a pe�a da posi��o*/
			capturedPieces.add(capturedPiece); 		/*adiciona ela na lista de pe�as removidas*/
		}
		
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		/*m�todo para desfazer movimento indevido do jogador(pois o pr�prio jogado n�o pode colocar seu Rei em posi��o
		 * de cheque)*/
		Piece p = board.removePiece(target);/*remover a pe�a da posi��o de destino*/
		board.placePiece(p, source);/*devolvendo a pe�a 'p' pra sua posi��o de origem*/
		
		if(capturedPiece != null) {
			/*voltar a pe�a que tinha sido capturada pra posi��o de destino*/
			board.placePiece(capturedPiece, target);
			/*tirar a pe�a que havia sido capturada da lista de pe�as capturadas e devolv�-la � lista de pe�as do tabuleiro*/
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
	}
	
	private void validateSourcePosition(Position position) {
		/*Se n�o existir uma pe�a nessa posi��o, vai retornar um erro*/
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
		if(!board.piece(position).IsThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if(!board.piece(source).PossibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}
	
	private void nextTurn() {
		/*esse m�todo 'nextTurn' ser� executado ap�s fazer uma jogada*/
		turn++;/*incrementar o turno. Turno 1 passa pra turno 2 e assim sucessivamente*/
		/*agora temos que fazer a pe�a mudar de cor.*/
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list) {
			if(p instanceof King) {
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for(Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if(mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false; 
	}
	
	/*instancia as pe�as de xadrez informando as coordenadas no sistema do tabuleiro do xadrez e n�o da matriz pra n�o deixar confuso*/
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
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
