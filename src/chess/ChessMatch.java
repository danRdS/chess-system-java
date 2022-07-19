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
	private ChessPiece promoted;
	
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
	
	public ChessPiece getPromoted() {
		return promoted;
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
		Position position = sourcePosition.toPosition();	/*convertendo a posição de xadrez pra uma posição de matriz normal*/
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();		/*retornar os movimentos possíveis da peça dessa posição*/
	}
	
	/*método pra tirar uma peça da posição de origem e colocá-la na posição de destino*/
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		/*converter as duas posições para posições da matriz*/
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		/*Antes de fazer um movimento, devo verificar se na posição de origem(source) havia uma peça*/
		validateSourcePosition(source);	/*operação responsável por validar essa posição de origem, senão existir ela lançará uma exceção*/
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target); /*makeMove é uma operação responsável por fazer o movimento da peça*/
		
		if(testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
		
		ChessPiece movedPiece = (ChessPiece)board.piece(target);
		
		// #specialmove promotion
		promoted = null;
		if(movedPiece instanceof Pawn) {
			if((movedPiece.getColor() == Color.WHITE && target.getRow() == 0 || movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
			/*se a peça movida for da cor branca && chegou na posição linha 0 || se a peça movida for da cor preta && chegou na posição linha 7 */
				promoted = (ChessPiece)board.piece(target);/*peça promovida foi a peão*/
				promoted = replacePromotedPiece("Q");
			}
		}
		
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if(testCheckMate(opponent(currentPlayer))) {
			/*Se o oponente da peça que foi mexida ficou em check mate. Testa se o jogo acabou*/
			checkMate = true;
		}
		else {
			nextTurn();
		}
		
		// #specialmove en passant
		if(movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2) || target.getRow() == source.getRow() + 2) {
		/*se o movimento inicial foi de um peão e ele andou 2 casas pra mais ou pra menos, então ele está passivel pra tomar o en passant*/
			enPassantVulnerable = movedPiece;
			/*o enPassantVulnerable recebe a peça que foi movida (movedPiece)*/
		}
		else {
			enPassantVulnerable = null;
		}
		
		return (ChessPiece)capturedPiece; /*Downcasting pq 'capturedPiece' era do tipo 'Piece'*/
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if(promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if(!type.equals("B") &&  !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			return promoted; /*Se o usuário digitar um valor inválido, vai retornar uma rainha, que é o valor inicial do 'promoted'*/
		}
		
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		/*removeu a peça da posição(pos) e colocou na posição(p)*/
		piecesOnTheBoard.remove(p);	/*excluir essa peça p da lista de peça do tabuleiro*/
		
		/*Instanciando uma nova peça que será colocada na posição(pos). Criar um método aulixiar pra fazer isso(newPiece)*/
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);/*adicionando essa nova peça na posição da peça que foi 'promovida'*/
		piecesOnTheBoard.add(newPiece);/*adicionado essa nova peça ao tabuleiro*/
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if(type.equals("B")) return new Bishop(board, color);
		/*Se o tipo for 'B' deve instanciar um bispo associado com o board e com o color informado */
		if(type.equals("N")) return new Knight(board, color);
		if(type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
	}
	
	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece)board.removePiece(source);	/*removendo a peça que estava na posição de origem*/
		p.increaseMoveCount(); /*ao fazer o movimento precisa incrementar a qtde de movimentos dessa peça*/
		Piece capturedPiece = board.removePiece(target); /*remove a possivel peça que está na posição de destino e ela, por padrão, 
		será a peça capturada.*/
		board.placePiece(p, target);			/*colocando a peça 'p' na posição de destino*/
		
		if(capturedPiece != null) {
			/*se a peça capturada for diferente de nulo, siginifa que capturei uma peça*/
			piecesOnTheBoard.remove(capturedPiece); /*remove a peça da posição*/
			capturedPieces.add(capturedPiece); 		/*adiciona ela na lista de peças removidas*/
		}
		
		// #specialmove castling kingside rook
		if(p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT); /*remove a torre da sua posição*/
			board.placePiece(rook, targetT);/*coloca a torre na posição de destino*/
			rook.increaseMoveCount();
		}
		// #specialmove castling queenside rook
		if(p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT); /*remove a torre da sua posição*/
			board.placePiece(rook, targetT);/*coloca a torre na posição de destino*/
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
				/*remove o peão do tabuleiro*/
				capturedPieces.add(capturedPiece);
				/*adicionou a peça na lista de peças removidas*/
				piecesOnTheBoard.remove(capturedPiece);
			}
		}
		
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		/*método para desfazer movimento indevido do jogador(pois o próprio jogado não pode colocar seu Rei em posição
		 * de cheque)*/
		ChessPiece p = (ChessPiece)board.removePiece(target);/*remover a peça da posição de destino*/
		p.decreaseMoveCount();
		board.placePiece(p, source);/*devolvendo a peça 'p' pra sua posição de origem*/
		
		if(capturedPiece != null) {
			/*voltar a peça que tinha sido capturada pra posição de destino*/
			board.placePiece(capturedPiece, target);
			/*tirar a peça que havia sido capturada da lista de peças capturadas e devolvê-la à lista de peças do tabuleiro*/
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		// #specialmove castling kingside rook
		if(p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT); /*remove a torre da sua posição, voltando a posição de origem*/
			board.placePiece(rook, sourceT);/*devolve a torre na posição de origem*/
			rook.increaseMoveCount();
		}
		// #specialmove castling queenside rook
		if(p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT); /*remove a torre da sua posição, voltando a posição de origem*/
			board.placePiece(rook, sourceT);/*devolve a torre na posição de origem*/
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
		/*Se não existir uma peça nessa posição, vai retornar um erro*/
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
		/*esse método 'nextTurn' será executado após fazer uma jogada*/
		turn++;/*incrementar o turno. Turno 1 passa pra turno 2 e assim sucessivamente*/
		/*agora temos que fazer a peça mudar de cor.*/
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
		/*Se esta cor não estiver em check, ela também não está em mate*/
		if(!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list) {
			/*Se alguma peça p nessa lista que possua um movimento que retire do check, então retorna 'false' pq o rei não está 
			 * em check mate*/
			boolean[][] mat = p.possibleMoves();
			for(int i=0; i<board.getRows(); i++) {
				for(int j=0; j<board.getColumns(); j++) {
					if(mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();/*posição da peça p. Fez downCasting pra 
						pegar a posição pq o atributo da peça 'p' está como 'protected'*/
						Position target = new Position(i, j);
						/*Movimento da peça 'p' da origem para o destino*/
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);/*testa se o Rei da minha cor ainda está em check*/
						undoMove(source, target, capturedPiece);/*para desfazer o movimento do 'capturedPiece' que foi só pra testar*/
						if(!testCheck) {
							return false;/*se não está em check, retirou o rei do check*/
						}
					}
				}
			}
		}
		/*se percorrer cada posição 'p' da lista e não encontrar nenhuma peça que retire do mate, então retorna 'true'*/
		return true;
	}
	
	/*instancia as peças de xadrez informando as coordenadas no sistema do tabuleiro do xadrez e não da matriz pra não deixar confuso*/
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
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));/*como o peão tem referência a partida, tem que informar que é a própria*/
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
