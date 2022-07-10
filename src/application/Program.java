package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();
		
		while (true) {
			try {
				UI.clearScreen();
				UI.printMatch(chessMatch, captured);
				System.out.println();
				System.out.print("Source: ");
				ChessPosition source = UI.readChessPosition(sc); /*ler a posição de origem da peça*/
				
				boolean[][] possibleMoves = chessMatch.possibleMoves(source);
				UI.clearScreen();
				UI.printBoard(chessMatch.getPieces(), possibleMoves); //sobrecarga do método 'printBoard'
				System.out.println();
				System.out.print("Target: ");
				ChessPosition target = UI.readChessPosition(sc); /*ler a posição de destino da peça*/
				
				ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
				/*sempre que efetuar um movimento e ele resultar em uma peça capturada, fazer o if pra adicionar a peça à lista*/
				if(capturedPiece != null) {
					/*se a peça capturada for diferente de nulo, significa que alguma peça foi capturada*/
					captured.add(capturedPiece);/*adiciona essa peça a lista de peças capturadas*/
				}
			}
			catch(ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
			catch(InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
	}

}
