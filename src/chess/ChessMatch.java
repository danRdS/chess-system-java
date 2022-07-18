package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	
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
	
	public boolean getCheckmate() {
		return checkMate;
	}
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
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
		
		ChessPiece movedPiece = (ChessPiece)board.piece(target);
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if(testCheckMate(opponent(currentPlayer))) {
			/*Se o oponente da pe�a que foi mexida ficou em check mate. Testa se o jogo acabou*/
			checkMate = true;
		}
		else {
			nextTurn();
		}
		
		// #specialmove en passant
		if(movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2) || target.getRow() == source.getRow() + 2) {
		/*se o movimento inicial foi de um pe�o e ele andou 2 casas pra mais ou pra menos, ent�o ele est� passivel pra tomar o en passant*/
			enPassantVulnerable = movedPiece;
			/*o enPassantVulnerable recebe a pe�a que foi movida (movedPiece)*/
		}
		else {
			enPassantVulnerable = null;
		}
		
		return (ChessPiece)capturedPiece; /*Downcasting pq 'capturedPiece' era do tipo 'Piece'*/
	}
	
	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece)board.removePiece(source);	/*removendo a pe�a que estava na posi��o de origem*/
		p.increaseMoveCount(); /*ao fazer o movimento precisa incrementar a qtde de movimentos dessa pe�a*/
		Piece capturedPiece = board.removePiece(target); /*remove a possivel pe�a que est� na posi��o de destino e ela, por padr�o, 
		ser� a pe�a capturada.*/
		board.placePiece(p, target);			/*colocando a pe�a 'p' na posi��o de destino*/
		
		if(capturedPiece != null) {
			/*se a pe�a capturada for diferente de nulo, siginifa que capturei uma pe�a*/
			piecesOnTheBoard.remove(capturedPiece); /*remove a pe�a da posi��o*/
			capturedPieces.add(capturedPiece); 		/*adiciona ela na lista de pe�as removidas*/
		}
		
		// #specialmove castling kingside rook
		if(p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT); /*remove a torre da sua posi��o*/
			board.placePiece(rook, targetT);/*coloca a torre na posi��o de destino*/
			rook.increaseMoveCount();
		}
		// #specialmove castling queenside rook
		if(p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT); /*remove a torre da sua posi��o*/
			board.placePiece(rook, targetT);/*coloca a torre na posi��o de destino*/
			rook.increaseMoveCount();
		}
		
		/*sempre que fizer um movimento, precisa ver se ele foi um en passant*/
		// #specialmove en passant
		if(p instanceof Pawn) {
			if(source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if(p.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				}
				else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = board.removePiece(pawnPosition);
				/*remove o pe�o do tabuleiro*/
				capturedPieces.add(capturedPiece);
				/*adicionou a pe�a na lista de pe�as removidas*/
				piecesOnTheBoard.remove(capturedPiece);
			}
		}
		
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		/*m�todo para desfazer movimento indevido do jogador(pois o pr�prio jogado n�o pode colocar seu Rei em posi��o
		 * de cheque)*/
		ChessPiece p = (ChessPiece)board.removePiece(target);/*remover a pe�a da posi��o de destino*/
		p.decreaseMoveCount();
		board.placePiece(p, source);/*devolvendo a pe�a 'p' pra sua posi��o de origem*/
		
		if(capturedPiece != null) {
			/*voltar a pe�a que tinha sido capturada pra posi��o de destino*/
			board.placePiece(capturedPiece, target);
			/*tirar a pe�a que havia sido capturada da lista de pe�as capturadas e devolv�-la � lista de pe�as do tabuleiro*/
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		// #specialmove castling kingside rook
		if(p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT); /*remove a torre da sua posi��o, voltando a posi��o de origem*/
			board.placePiece(rook, sourceT);/*devolve a torre na posi��o de origem*/
			rook.increaseMoveCount();
		}
		// #specialmove castling queenside rook
		if(p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT); /*remove a torre da sua posi��o, voltando a posi��o de origem*/
			board.placePiece(rook, sourceT);/*devolve a torre na posi��o de origem*/
			rook.decreaseMoveCount();
		}
		
		// #specialmove en passant
		if(p instanceof Pawn) {
			if(source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece)board.removePiece(target);
				Position pawnPosition;
				if(p.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, target.getColumn());
				}
				else {
					pawnPosition = new Position(4, target.getColumn());
				}
				board.placePiece(pawn, pawnPosition);
			}
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
	
	private boolean testCheckMate(Color color) {
		/*Se esta cor n�o estiver em check, ela tamb�m n�o est� em mate*/
		if(!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list) {
			/*Se alguma pe�a p nessa lista que possua um movimento que retire do check, ent�o retorna 'false' pq o rei n�o est� 
			 * em check mate*/
			boolean[][] mat = p.possibleMoves();
			for(int i=0; i<board.getRows(); i++) {
				for(int j=0; j<board.getColumns(); j++) {
					if(mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();/*posi��o da pe�a p. Fez downCasting pra 
						pegar a posi��o pq o atributo da pe�a 'p' est� como 'protected'*/
						Position target = new Position(i, j);
						/*Movimento da pe�a 'p' da origem para o destino*/
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);/*testa se o Rei da minha cor ainda est� em check*/
						undoMove(source, target, capturedPiece);/*para desfazer o movimento do 'capturedPiece' que foi s� pra testar*/
						if(!testCheck) {
							return false;/*se n�o est� em check, retirou o rei do check*/
						}
					}
				}
			}
		}
		/*se percorrer cada posi��o 'p' da lista e n�o encontrar nenhuma pe�a que retire do mate, ent�o retorna 'true'*/
		return true;
	}
	
	/*instancia as pe�as de xadrez informando as coordenadas no sistema do tabuleiro do xadrez e n�o da matriz pra n�o deixar confuso*/
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));/*como o pe�o tem refer�ncia a partida, tem que informar que � a pr�pria*/
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
	}
		
}
